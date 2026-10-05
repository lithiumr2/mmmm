package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScienceCategory
import com.example.data.UserStat
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

@Composable
fun TechTreeHUD(
    resources: Map<String, Int>,
    userStats: Map<ScienceCategory, UserStat>,
    selectedFilter: ScienceCategory?,
    dueReviewsCount: Int,
    onFilterSelected: (ScienceCategory?) -> Unit,
    onGatherResource: (String) -> Unit,
    onExpeditionClick: () -> Unit,
    onOpenSRSReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkBackground.copy(alpha = 0.94f),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(DarkSurfaceBorder, Color.Transparent)
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Row 1: App Header & Quick Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and theme badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(AmberForge.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, AmberForge, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AmberForge,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TECH SURVIVAL",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Árbol de Ingeniería Inversa",
                            color = CyanAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Spaced Repetition Due Card Button
                    ElevatedButton(
                        onClick = onOpenSRSReview,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (dueReviewsCount > 0) AmberForge.copy(alpha = 0.25f) else DarkSurfaceElevated,
                            contentColor = if (dueReviewsCount > 0) AmberForge else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("srs_review_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "SRS",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SRS ($dueReviewsCount)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Expedition Gather Button
                    ElevatedButton(
                        onClick = onExpeditionClick,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = CyanAccent.copy(alpha = 0.2f),
                            contentColor = CyanAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("expedition_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Recolectar",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Expedición",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Row 2: Resources Bar (Horizontal Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                val primaryResources = listOf("Madera", "Arcilla", "Carbón", "Cobre", "Malaquita", "Estaño", "Bronce", "Agua")
                primaryResources.forEach { resName ->
                    val amount = resources[resName] ?: 0
                    ResourceChip(
                        name = resName,
                        amount = amount,
                        onAdd = { onGatherResource(resName) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Row 3: Science Category Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                // "All" filter chip
                CategoryFilterChip(
                    title = "TODAS",
                    color = CyanAccent,
                    isSelected = selectedFilter == null,
                    level = null,
                    onClick = { onFilterSelected(null) }
                )
                Spacer(modifier = Modifier.width(6.dp))

                ScienceCategory.values().forEach { category ->
                    val stat = userStats[category]
                    CategoryFilterChip(
                        title = category.displayName.take(8).uppercase(),
                        color = Color(category.hexColor),
                        isSelected = selectedFilter == category,
                        level = stat?.level ?: 1,
                        onClick = { onFilterSelected(category) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun ResourceChip(
    name: String,
    amount: Int,
    onAdd: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
            .padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Text(
            text = name,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$amount",
            color = if (amount > 0) TextPrimary else Color(0xFFFF5252),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(2.dp))
        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(20.dp)
                .testTag("add_res_$name")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar $name",
                tint = CyanAccent,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun CategoryFilterChip(
    title: String,
    color: Color,
    isSelected: Boolean,
    level: Int?,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                if (isSelected) color.copy(alpha = 0.25f) else DarkSurface,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                if (isSelected) color else DarkSurfaceBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
        if (level != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Lv$level",
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
