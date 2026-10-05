package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScienceCategory
import com.example.data.TechNode
import com.example.ui.theme.AmberForge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldScience
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun TechTreeCanvas(
    nodes: List<TechNode>,
    unlockedNodeIds: Set<String>,
    resources: Map<String, Int>,
    categoryFilter: ScienceCategory?,
    onNodeClick: (TechNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(0.9f) }
    var offsetX by remember { mutableFloatStateOf(60f) }
    var offsetY by remember { mutableFloatStateOf(100f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val nodeMap = remember(nodes) { nodes.associateBy { it.id } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.45f, 1.8f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        // Grid background drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 60f * scale
            val startX = (offsetX % gridSize)
            val startY = (offsetY % gridSize)
            val gridColor = Color(0xFF1E293B).copy(alpha = 0.35f)

            var x = startX
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += gridSize
            }

            var y = startY
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }
        }

        // 2D DAG Vector Connectors (Curved Paths)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            nodes.forEach { childNode ->
                childNode.requiredNodes.forEach { parentId ->
                    val parent = nodeMap[parentId]
                    if (parent != null) {
                        val start = Offset(parent.x + 105f, parent.y + 55f)
                        val end = Offset(childNode.x + 5f, childNode.y + 55f)

                        val path = Path().apply {
                            moveTo(start.x, start.y)
                            val midX = (start.x + end.x) / 2f
                            cubicTo(
                                midX, start.y,
                                midX, end.y,
                                end.x, end.y
                            )
                        }

                        val isParentUnlocked = parent.id in unlockedNodeIds
                        val isChildUnlocked = childNode.id in unlockedNodeIds
                        val isChildReady = childNode.canUnlock(unlockedNodeIds, resources)

                        val lineColor: Color
                        val strokeWidth: Float
                        val pathEffect: PathEffect?

                        when {
                            isParentUnlocked && isChildUnlocked -> {
                                lineColor = CyanAccent.copy(alpha = 0.85f)
                                strokeWidth = 3.5f
                                pathEffect = null
                            }
                            isParentUnlocked && isChildReady -> {
                                lineColor = AmberForge.copy(alpha = 0.9f)
                                strokeWidth = 3f
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            }
                            isParentUnlocked -> {
                                lineColor = Color(0xFF64748B).copy(alpha = 0.5f)
                                strokeWidth = 2f
                                pathEffect = null
                            }
                            else -> {
                                lineColor = Color(0xFF334155).copy(alpha = 0.3f)
                                strokeWidth = 1.5f
                                pathEffect = null
                            }
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                pathEffect = pathEffect
                            )
                        )
                    }
                }
            }
        }

        // Render Nodes positioned in 2D space
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            nodes.forEach { node ->
                val isUnlocked = node.id in unlockedNodeIds
                val isReadyToUnlock = !isUnlocked && node.canUnlock(unlockedNodeIds, resources)
                val matchesFilter = categoryFilter == null || node.category == categoryFilter

                TechNodeCard(
                    node = node,
                    isUnlocked = isUnlocked,
                    isReadyToUnlock = isReadyToUnlock,
                    matchesFilter = matchesFilter,
                    pulseGlow = pulseGlow,
                    onClick = { onNodeClick(node) },
                    modifier = Modifier.offset {
                        IntOffset(node.x.roundToInt(), node.y.roundToInt())
                    }
                )
            }
        }
    }
}

@Composable
fun TechNodeCard(
    node: TechNode,
    isUnlocked: Boolean,
    isReadyToUnlock: Boolean,
    matchesFilter: Boolean,
    pulseGlow: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(node.category.hexColor)
    val cardAlpha = if (matchesFilter) 1.0f else 0.35f

    val borderColor = when {
        isUnlocked -> CyanAccent
        isReadyToUnlock -> AmberForge.copy(alpha = pulseGlow)
        else -> DarkSurfaceBorder
    }

    val borderWidth = when {
        isUnlocked -> 1.5.dp
        isReadyToUnlock -> (2.dp * pulseGlow).coerceAtLeast(1.5.dp)
        else -> 1.dp
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
            isUnlocked -> DarkSurfaceElevated
            isReadyToUnlock -> Color(0xFF1B2332)
            else -> DarkSurface
        },
        shadowElevation = if (isUnlocked) 8.dp else 2.dp,
        modifier = modifier
            .width(210.dp)
            .height(110.dp)
            .graphicsLayer { alpha = cardAlpha }
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("tech_node_${node.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Category Badge + Status Icon
            Row(
                modifier = Modifier.fillMaxSize().weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Category Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(categoryColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = node.category.displayName.take(8).uppercase(),
                        color = categoryColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Status Icon or Mastery Star
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (node.isMastered) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Mastered",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    when {
                        isUnlocked -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Desbloqueado",
                            tint = EmeraldScience,
                            modifier = Modifier.size(18.dp)
                        )
                        isReadyToUnlock -> Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Listo para desbloquear",
                            tint = AmberForge,
                            modifier = Modifier.size(18.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Node Title
            Text(
                text = node.title,
                color = if (isUnlocked || isReadyToUnlock) TextPrimary else TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Technical brief / formula
            Text(
                text = node.formulaOrEquation,
                color = if (isUnlocked) CyanAccent.copy(alpha = 0.85f) else TextMuted,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Cost or SRS Footer
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUnlocked) {
                    Text(
                        text = "SRS: Int ${node.srsInterval}d | EF ${String.format("%.1f", node.srsEaseFactor)}",
                        fontSize = 9.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    val costString = node.resourceCosts.entries.joinToString(" • ") { "${it.value} ${it.key.take(4)}" }
                    Text(
                        text = if (costString.isNotBlank()) "Coste: $costString" else "Sin coste",
                        fontSize = 9.sp,
                        color = if (isReadyToUnlock) AmberForge else TextMuted,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
