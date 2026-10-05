package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScienceCategory
import com.example.data.SeedData
import com.example.data.TechNode
import com.example.data.TechRepository
import com.example.data.UserStat
import com.example.data.db.AppDatabase
import com.example.service.AIBrainService
import com.example.service.FeynmanEvaluation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TechTreeUiState(
    val nodes: List<TechNode> = emptyList(),
    val resources: Map<String, Int> = emptyMap(),
    val userStats: Map<ScienceCategory, UserStat> = emptyMap(),
    val selectedNode: TechNode? = null,
    val categoryFilter: ScienceCategory? = null,
    val isFeynmanEvaluating: Boolean = false,
    val lastFeynmanResult: FeynmanEvaluation? = null,
    val messageSnackbar: String? = null
)

private data class RepoData(
    val nodes: List<TechNode>,
    val resources: Map<String, Int>,
    val userStats: Map<ScienceCategory, UserStat>
)

private data class ViewExtras(
    val selectedNode: TechNode?,
    val categoryFilter: ScienceCategory?,
    val isFeynmanEvaluating: Boolean,
    val lastFeynmanResult: FeynmanEvaluation?,
    val messageSnackbar: String?
)

class TechTreeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TechRepository(AppDatabase.getDatabase(application))

    private val _selectedNode = MutableStateFlow<TechNode?>(null)
    private val _categoryFilter = MutableStateFlow<ScienceCategory?>(null)
    private val _isFeynmanEvaluating = MutableStateFlow(false)
    private val _lastFeynmanResult = MutableStateFlow<FeynmanEvaluation?>(null)
    private val _messageSnackbar = MutableStateFlow<String?>(null)

    private val repoFlow = combine(
        repository.getTechNodes(),
        repository.getResources(),
        repository.getUserStats()
    ) { nodes, resources, stats ->
        RepoData(nodes, resources, stats)
    }

    private val extrasFlow = combine(
        _selectedNode,
        _categoryFilter,
        _isFeynmanEvaluating,
        _lastFeynmanResult,
        _messageSnackbar
    ) { sel, filter, evaluating, result, msg ->
        ViewExtras(sel, filter, evaluating, result, msg)
    }

    val uiState: StateFlow<TechTreeUiState> = combine(
        repoFlow,
        extrasFlow
    ) { repo, extras ->
        val updatedSelected = extras.selectedNode?.let { sel ->
            repo.nodes.find { it.id == sel.id }
        } ?: extras.selectedNode

        TechTreeUiState(
            nodes = repo.nodes,
            resources = repo.resources,
            userStats = repo.userStats,
            selectedNode = updatedSelected,
            categoryFilter = extras.categoryFilter,
            isFeynmanEvaluating = extras.isFeynmanEvaluating,
            lastFeynmanResult = extras.lastFeynmanResult,
            messageSnackbar = extras.messageSnackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TechTreeUiState(
            nodes = SeedData.initialNodes,
            resources = SeedData.initialResources
        )
    )

    fun selectNode(node: TechNode?) {
        _selectedNode.value = node
        _lastFeynmanResult.value = null
    }

    fun setCategoryFilter(category: ScienceCategory?) {
        _categoryFilter.value = category
    }

    fun clearSnackbar() {
        _messageSnackbar.value = null
    }

    fun gatherResource(resourceName: String, amount: Int = 15) {
        viewModelScope.launch {
            repository.modifyResource(resourceName, amount)
            _messageSnackbar.value = "+$amount $resourceName recolectado para la ciencia"
        }
    }

    fun gatherExpeditionAll() {
        viewModelScope.launch {
            val keyResources = listOf("Madera", "Arcilla", "Carbón", "Malaquita", "Agua")
            keyResources.forEach { res ->
                repository.modifyResource(res, (10..20).random())
            }
            _messageSnackbar.value = "¡Expedición de supervivencia completada! Recursos abastecidos."
        }
    }

    fun unlockNodeWithResources(node: TechNode) {
        viewModelScope.launch {
            val unlockedIds = uiState.value.nodes.filter { it.isUnlocked }.map { it.id }.toSet()
            if (!node.hasPrerequisites(unlockedIds)) {
                _messageSnackbar.value = "Prerrequisitos científicos incompletos."
                return@launch
            }

            val consumed = repository.consumeResources(node.resourceCosts)
            if (consumed) {
                repository.unlockNode(node.id)
                _messageSnackbar.value = "¡${node.title} investigado con éxito! +50 XP"
            } else {
                _messageSnackbar.value = "Recursos insuficientes para forjar esta tecnología."
            }
        }
    }

    fun completeLaboratoryThermalCrafting(node: TechNode) {
        viewModelScope.launch {
            val unlockedIds = uiState.value.nodes.filter { it.isUnlocked }.map { it.id }.toSet()
            if (!node.hasPrerequisites(unlockedIds)) {
                _messageSnackbar.value = "Faltan prerrequisitos para sintetizar este material."
                return@launch
            }

            val consumed = repository.consumeResources(node.resourceCosts)
            if (consumed) {
                repository.unlockNode(node.id)
                _messageSnackbar.value = "¡Simulación térmica exitosa! ${node.title} desbloqueado. +80 XP"
            } else {
                _messageSnackbar.value = "Recursos insuficientes para alimentar el horno."
            }
        }
    }

    fun submitFeynmanExplanation(node: TechNode, explanation: String) {
        viewModelScope.launch {
            _isFeynmanEvaluating.value = true
            _lastFeynmanResult.value = null
            try {
                val result = AIBrainService.evaluateFeynmanExplanation(node, explanation)
                _lastFeynmanResult.value = result
                if (result.isPassed) {
                    repository.setNodeMastered(node.id)
                    _messageSnackbar.value = "¡Maestría Feynman demostrada! +150 XP de investigación"
                }
            } catch (e: Exception) {
                _messageSnackbar.value = "Error al evaluar: ${e.message}"
            } finally {
                _isFeynmanEvaluating.value = false
            }
        }
    }

    fun submitSRSReview(node: TechNode, qualityScore: Int) {
        viewModelScope.launch {
            repository.updateSRS(node, qualityScore)
            _messageSnackbar.value = "Repetición espaciada registrada (calidad $qualityScore/5)."
        }
    }
}
