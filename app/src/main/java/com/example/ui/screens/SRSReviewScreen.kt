package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TechNode
import com.example.ui.theme.AmberForge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldScience
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletMath

@Composable
fun SRSReviewScreen(
    unlockedNodes: List<TechNode>,
    onScoreSubmitted: (TechNode, Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentNode = unlockedNodes.getOrNull(currentIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = AmberForge,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REPETICIÓN ESPACIADA (SM-2)",
                            color = AmberForge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = if (unlockedNodes.isNotEmpty()) "Tarjeta ${currentIndex + 1} de ${unlockedNodes.size}" else "Sin tarjetas pendientes",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentNode == null) {
                // Empty state
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldScience,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "¡Al día con las revisiones!",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Has repasado todos los conceptos científicos desbloqueados. El algoritmo SM-2 programará la próxima sesión.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkBackground)
                        ) {
                            Text("Volver al Árbol", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Flashcard Container
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isFlipped) CyanAccent else DarkSurfaceBorder),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { isFlipped = !isFlipped }
                        .testTag("srs_flashcard")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Top metadata
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentNode.category.displayName.uppercase(),
                                    color = Color(currentNode.category.hexColor),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "SM-2: Int ${currentNode.srsInterval}d • EF ${String.format("%.1f", currentNode.srsEaseFactor)}",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Technology Title
                            Text(
                                text = currentNode.title,
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Front side: Prompt
                            if (!isFlipped) {
                                Text(
                                    text = "¿Cuál es el principio científico fundamental, la reacción o la fórmula que hace posible ${currentNode.title}?",
                                    color = TextSecondary,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkBackground, RoundedCornerShape(10.dp))
                                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Flip, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Toca para voltear la tarjeta", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            } else {
                                // Back side: Scientific explanation & formula
                                Text(
                                    text = "Mecanismo Científico:",
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentNode.scientificPrinciple,
                                    color = TextPrimary,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkBackground, RoundedCornerShape(8.dp))
                                        .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = currentNode.formulaOrEquation,
                                        color = CyanAccent,
                                        fontSize = 11.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (currentNode.technicalData.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    currentNode.technicalData.forEach { (k, v) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = k, color = TextMuted, fontSize = 11.sp)
                                            Text(text = v, color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }

                        // Flip prompt footer
                        if (isFlipped) {
                            Text(
                                text = "Califica la facilidad con la que recordaste el concepto:",
                                color = TextMuted,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SM-2 Recall Scoring Buttons (0 to 5)
                if (isFlipped) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScoreButton("0: Olvido", DangerRed, modifier = Modifier.weight(1f)) {
                                submitScore(0, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                            ScoreButton("1: Mal", DangerRed.copy(alpha = 0.8f), modifier = Modifier.weight(1f)) {
                                submitScore(1, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                            ScoreButton("2: Difícil", AmberForge, modifier = Modifier.weight(1f)) {
                                submitScore(2, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScoreButton("3: Regular", CyanAccent, modifier = Modifier.weight(1f)) {
                                submitScore(3, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                            ScoreButton("4: Fácil", EmeraldScience, modifier = Modifier.weight(1f)) {
                                submitScore(4, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                            ScoreButton("5: Perfecto", Color(0xFFFFD700), modifier = Modifier.weight(1f)) {
                                submitScore(5, currentNode, onScoreSubmitted) {
                                    isFlipped = false
                                    if (currentIndex < unlockedNodes.size - 1) currentIndex++ else currentIndex = 0
                                }
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { isFlipped = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberForge, contentColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("flip_card_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Solución y Principio", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun submitScore(
    score: Int,
    node: TechNode,
    onSubmit: (TechNode, Int) -> Unit,
    onNext: () -> Unit
) {
    onSubmit(node, score)
    onNext()
}

@Composable
private fun ScoreButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = modifier
            .height(42.dp)
            .clickable(onClick = onClick)
            .testTag("score_btn_$label")
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                color = color,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
