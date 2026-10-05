package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TechDao {
    @Query("SELECT * FROM tech_nodes")
    fun getAllNodeStates(): Flow<List<TechNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNodeState(node: TechNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAllNodeStates(nodes: List<TechNodeEntity>)

    @Query("SELECT * FROM resources")
    fun getAllResources(): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResource(resource: ResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAllResources(resources: List<ResourceEntity>)

    @Query("SELECT * FROM user_stats")
    fun getAllUserStats(): Flow<List<UserStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStat(stat: UserStatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAllUserStats(stats: List<UserStatEntity>)
}
