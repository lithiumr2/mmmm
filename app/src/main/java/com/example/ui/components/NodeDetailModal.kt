package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeDetailModal(
    node: TechNode,
    allNodesMap: Map<String, TechNode>,
    unlockedNodeIds: Set<String>,
    resources: Map<String, Int>,
    onDismiss: () -> Unit,
    onUnlockWithResources: () -> Unit,
    onOpenLaboratory: () -> Unit,
    onOpenFeynmanTest: () -> Unit,
    onOpenSRSReview: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isUnlocked = node.id in unlockedNodeIds
    val canAfford = node.resourceCosts.all { (res, cost) -> (resources[res] ?: 0) >= cost }
    val hasPrereqs = node.hasPrerequisites(unlockedNodeIds)
    val categoryColor = Color(node.category.hexColor)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(DarkSurfaceBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Category Badge + Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, categoryColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(categoryColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = node.category.displayName.uppercase(),
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = node.title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (node.isMastered) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "DOMINADO",
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = node.description,
                color = TextSecondary,
                fontSize = 13.5.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scientific Principle Card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRINCIPIO FÍSICO / QUÍMICO",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = node.scientificPrinciple,
                        color = TextPrimary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Equation box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground, RoundedCornerShape(6.dp))
                            .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = node.formulaOrEquation,
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Technical Data Grid
            if (node.technicalData.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CONSTANTES Y ESPECIFICACIONES",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        node.technicalData.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = key, color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    text = value,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Prerequisites & Resource Cost Section
            if (!isUnlocked) {
                // Prerequisites list
                if (node.requiredNodes.isNotEmpty()) {
                    Text(
                        text = "PRERREQUISITOS DE INVESTIGACIÓN",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    node.requiredNodes.forEach { reqId ->
                        val reqNode = allNodesMap[reqId]
                        val isReqMet = reqId in unlockedNodeIds
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isReqMet) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isReqMet) EmeraldScience else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = reqNode?.title ?: reqId,
                                color = if (isReqMet) TextPrimary else TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Resource Costs
                Text(
                    text = "RECURSOS NECESARIOS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                node.resourceCosts.forEach { (resName, requiredAmount) ->
                    val owned = resources[resName] ?: 0
                    val hasEnough = owned >= requiredAmount
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = resName, color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "$owned / $requiredAmount",
                            color = if (hasEnough) EmeraldScience else Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                // SRS Info Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Repetición Espaciada (SM-2)", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Intervalo: ${node.srsInterval} días • Facilidad: ${String.format("%.2f", node.srsEaseFactor)}", color = TextSecondary, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onOpenSRSReview,
                            modifier = Modifier.testTag("modal_srs_btn")
                        ) {
                            Text("Repasar", fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Action Buttons
            if (!isUnlocked) {
                // Direct Unlock Button
                Button(
                    onClick = onUnlockWithResources,
                    enabled = hasPrereqs && canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberForge,
                        disabledContainerColor = DarkSurfaceElevated,
                        contentColor = DarkBackground,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_node_btn")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!hasPrereqs) "Prerrequisitos pendientes" else if (!canAfford) "Recursos insuficientes" else "Desbloquear con Recursos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Laboratory Simulation Button
                OutlinedButton(
                    onClick = onOpenLaboratory,
                    enabled = hasPrereqs,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyanAccent,
                        disabledContentColor = TextMuted
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hasPrereqs) CyanAccent else DarkSurfaceBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_lab_btn")
                ) {
                    Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Forjar en Laboratorio Térmico",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Feynman Test Socratic AI Button
                Button(
                    onClick = onOpenFeynmanTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_feynman_btn")
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (node.isMastered) "Volver al Test Feynman (IA)" else "Filtro de Feynman (+150 XP IA)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
