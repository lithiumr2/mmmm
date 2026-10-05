package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.TechNode
import com.example.ui.TechTreeViewModel
import com.example.ui.components.NodeDetailModal
import com.example.ui.components.TechTreeCanvas
import com.example.ui.components.TechTreeHUD

enum class CurrentScreen {
    TREE,
    LABORATORY,
    FEYNMAN,
    SRS
}

@Composable
fun TechTreeScreen(
    viewModel: TechTreeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf(CurrentScreen.TREE) }
    var activeLabNode by remember { mutableStateOf<TechNode?>(null) }
    var activeFeynmanNode by remember { mutableStateOf<TechNode?>(null) }

    val unlockedIds = remember(uiState.nodes) {
        uiState.nodes.filter { it.isUnlocked }.map { it.id }.toSet()
    }
    val nodesMap = remember(uiState.nodes) {
        uiState.nodes.associateBy { it.id }
    }
    val dueReviews = remember(uiState.nodes) {
        uiState.nodes.filter { it.isUnlocked }
    }

    // Show snackbars when message changes
    LaunchedEffect(uiState.messageSnackbar) {
        uiState.messageSnackbar?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                CurrentScreen.TREE -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 2D Interactive DAG Canvas
                        TechTreeCanvas(
                            nodes = uiState.nodes,
                            unlockedNodeIds = unlockedIds,
                            resources = uiState.resources,
                            categoryFilter = uiState.categoryFilter,
                            onNodeClick = { clickedNode ->
                                viewModel.selectNode(clickedNode)
                            }
                        )

                        // Floating Top HUD
                        TechTreeHUD(
                            resources = uiState.resources,
                            userStats = uiState.userStats,
                            selectedFilter = uiState.categoryFilter,
                            dueReviewsCount = dueReviews.size,
                            onFilterSelected = { filter ->
                                viewModel.setCategoryFilter(filter)
                            },
                            onGatherResource = { resName ->
                                viewModel.gatherResource(resName)
                            },
                            onExpeditionClick = {
                                viewModel.gatherExpeditionAll()
                            },
                            onOpenSRSReview = {
                                currentScreen = CurrentScreen.SRS
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .statusBarsPadding()
                        )

                        // Node Detail Modal
                        uiState.selectedNode?.let { selected ->
                            NodeDetailModal(
                                node = selected,
                                allNodesMap = nodesMap,
                                unlockedNodeIds = unlockedIds,
                                resources = uiState.resources,
                                onDismiss = { viewModel.selectNode(null) },
                                onUnlockWithResources = {
                                    viewModel.unlockNodeWithResources(selected)
                                    viewModel.selectNode(null)
                                },
                                onOpenLaboratory = {
                                    activeLabNode = selected
                                    viewModel.selectNode(null)
                                    currentScreen = CurrentScreen.LABORATORY
                                },
                                onOpenFeynmanTest = {
                                    activeFeynmanNode = selected
                                    viewModel.selectNode(null)
                                    currentScreen = CurrentScreen.FEYNMAN
                                },
                                onOpenSRSReview = {
                                    viewModel.selectNode(null)
                                    currentScreen = CurrentScreen.SRS
                                }
                            )
                        }
                    }
                }

                CurrentScreen.LABORATORY -> {
                    activeLabNode?.let { labNode ->
                        LaboratoryScreen(
                            node = labNode,
                            resources = uiState.resources,
                            onCraftSuccess = { completedNode ->
                                viewModel.completeLaboratoryThermalCrafting(completedNode)
                            },
                            onBack = { currentScreen = CurrentScreen.TREE }
                        )
                    } ?: run {
                        currentScreen = CurrentScreen.TREE
                    }
                }

                CurrentScreen.FEYNMAN -> {
                    activeFeynmanNode?.let { feynmanNode ->
                        FeynmanTestScreen(
                            node = feynmanNode,
                            isEvaluating = uiState.isFeynmanEvaluating,
                            lastResult = uiState.lastFeynmanResult,
                            onSubmitExplanation = { explanation ->
                                viewModel.submitFeynmanExplanation(feynmanNode, explanation)
                            },
                            onBack = { currentScreen = CurrentScreen.TREE }
                        )
                    } ?: run {
                        currentScreen = CurrentScreen.TREE
                    }
                }

                CurrentScreen.SRS -> {
                    SRSReviewScreen(
                        unlockedNodes = dueReviews,
                        onScoreSubmitted = { node, score ->
                            viewModel.submitSRSReview(node, score)
                        },
                        onBack = { currentScreen = CurrentScreen.TREE }
                    )
                }
            }
        }
    }
}
