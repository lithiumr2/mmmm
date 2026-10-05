package com.example.data

import com.example.data.db.AppDatabase
import com.example.data.db.ResourceEntity
import com.example.data.db.TechNodeEntity
import com.example.data.db.UserStatEntity
import com.example.logic.SRSEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TechRepository(private val database: AppDatabase) {
    private val dao = database.techDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingNodes = dao.getAllNodeStates().firstOrNull()
        if (existingNodes.isNullOrEmpty()) {
            val entities = SeedData.initialNodes.map { node ->
                TechNodeEntity(
                    id = node.id,
                    isUnlocked = node.isUnlocked,
                    isMastered = node.isMastered,
                    srsInterval = node.srsInterval,
                    srsEaseFactor = node.srsEaseFactor,
                    srsRepetitions = node.srsRepetitions,
                    nextReviewDate = node.nextReviewDate
                )
            }
            dao.insertOrUpdateAllNodeStates(entities)

            val resourceEntities = SeedData.initialResources.map { (resId, amount) ->
                ResourceEntity(id = resId, name = resId, amount = amount)
            }
            dao.insertOrUpdateAllResources(resourceEntities)

            val statEntities = ScienceCategory.values().map { cat ->
                UserStatEntity(category = cat.name, xp = 0, level = 1)
            }
            dao.insertOrUpdateAllUserStats(statEntities)
        }
    }

    fun getTechNodes(): Flow<List<TechNode>> {
        return dao.getAllNodeStates().map { nodeEntities ->
            val entityMap = nodeEntities.associateBy { it.id }
            SeedData.initialNodes.map { staticNode ->
                val entity = entityMap[staticNode.id]
                if (entity != null) {
                    staticNode.copy(
                        isUnlocked = entity.isUnlocked,
                        isMastered = entity.isMastered,
                        srsInterval = entity.srsInterval,
                        srsEaseFactor = entity.srsEaseFactor,
                        srsRepetitions = entity.srsRepetitions,
                        nextReviewDate = entity.nextReviewDate
                    )
                } else {
                    staticNode
                }
            }
        }
    }

    fun getResources(): Flow<Map<String, Int>> {
        return dao.getAllResources().map { resourcesList ->
            resourcesList.associate { it.name to it.amount }
        }
    }

    fun getUserStats(): Flow<Map<ScienceCategory, UserStat>> {
        return dao.getAllUserStats().map { statsList ->
            statsList.mapNotNull { entity ->
                try {
                    val cat = ScienceCategory.valueOf(entity.category)
                    cat to UserStat(category = cat, xp = entity.xp, level = entity.level)
                } catch (e: Exception) {
                    null
                }
            }.toMap()
        }
    }

    suspend fun unlockNode(nodeId: String) = withContext(Dispatchers.IO) {
        val staticNode = SeedData.initialNodes.find { it.id == nodeId } ?: return@withContext
        val entity = TechNodeEntity(
            id = nodeId,
            isUnlocked = true,
            isMastered = false,
            srsInterval = 1,
            srsEaseFactor = 2.5f,
            srsRepetitions = 0,
            nextReviewDate = System.currentTimeMillis() + 86400000L
        )
        dao.insertOrUpdateNodeState(entity)
        addCategoryExperience(staticNode.category, 50)
    }

    suspend fun setNodeMastered(nodeId: String) = withContext(Dispatchers.IO) {
        val staticNode = SeedData.initialNodes.find { it.id == nodeId } ?: return@withContext
        val entity = TechNodeEntity(
            id = nodeId,
            isUnlocked = true,
            isMastered = true,
            srsInterval = 6,
            srsEaseFactor = 2.6f,
            srsRepetitions = 2,
            nextReviewDate = System.currentTimeMillis() + (6L * 86400000L)
        )
        dao.insertOrUpdateNodeState(entity)
        addCategoryExperience(staticNode.category, 150)
    }

    suspend fun updateSRS(node: TechNode, qualityScore: Int) = withContext(Dispatchers.IO) {
        val srsResult = SRSEngine.calculateNextReview(
            quality = qualityScore,
            previousInterval = node.srsInterval,
            previousEaseFactor = node.srsEaseFactor,
            repetitions = node.srsRepetitions
        )
        val entity = TechNodeEntity(
            id = node.id,
            isUnlocked = true,
            isMastered = node.isMastered || (qualityScore >= 4 && srsResult.repetitions >= 2),
            srsInterval = srsResult.intervalDays,
            srsEaseFactor = srsResult.easeFactor,
            srsRepetitions = srsResult.repetitions,
            nextReviewDate = srsResult.nextReviewDateMillis
        )
        dao.insertOrUpdateNodeState(entity)
        addCategoryExperience(node.category, 25 * (qualityScore + 1))
    }

    suspend fun modifyResource(resourceId: String, delta: Int) = withContext(Dispatchers.IO) {
        val currentResources = dao.getAllResources().firstOrNull()?.associate { it.name to it.amount } ?: emptyMap()
        val current = currentResources[resourceId] ?: 0
        val newAmount = (current + delta).coerceAtLeast(0)
        dao.insertOrUpdateResource(ResourceEntity(id = resourceId, name = resourceId, amount = newAmount))
    }

    suspend fun consumeResources(costs: Map<String, Int>): Boolean = withContext(Dispatchers.IO) {
        val currentResources = dao.getAllResources().firstOrNull()?.associate { it.name to it.amount } ?: emptyMap()
        val canAfford = costs.all { (res, cost) -> (currentResources[res] ?: 0) >= cost }
        if (!canAfford) return@withContext false

        costs.forEach { (res, cost) ->
            val current = currentResources[res] ?: 0
            dao.insertOrUpdateResource(ResourceEntity(id = res, name = res, amount = current - cost))
        }
        true
    }

    suspend fun addCategoryExperience(category: ScienceCategory, xpToAdd: Int) = withContext(Dispatchers.IO) {
        val existingStats = dao.getAllUserStats().firstOrNull()?.associateBy { it.category } ?: emptyMap()
        val currentStat = existingStats[category.name]
        val currentXp = currentStat?.xp ?: 0
        val newXp = currentXp + xpToAdd
        val newLevel = 1 + (newXp / 100)
        dao.insertOrUpdateUserStat(
            UserStatEntity(category = category.name, xp = newXp, level = newLevel)
        )
    }
}
