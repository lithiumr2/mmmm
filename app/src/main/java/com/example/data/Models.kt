package com.example.data

enum class ScienceCategory(
    val displayName: String,
    val hexColor: Long,
    val iconName: String
) {
    EARTH("Ciencias de la Tierra", 0xFF8D6E63, "terrain"),
    CHEMISTRY("Química", 0xFF00E676, "science"),
    PHYSICS("Física", 0xFF00E5FF, "bolt"),
    MECHANICS("Mecánica", 0xFFFF7043, "precision_manufacturing"),
    BIOLOGY("Biología", 0xFF76FF03, "eco"),
    MATH("Matemáticas", 0xFFB388FF, "functions")
}

data class TechNode(
    val id: String,
    val title: String,
    val description: String,
    val category: ScienceCategory,
    val requiredNodes: List<String>,
    val resourceCosts: Map<String, Int>,
    val x: Float,
    val y: Float,
    val scientificPrinciple: String,
    val formulaOrEquation: String,
    val technicalData: Map<String, String>,
    val targetTemperature: Float = 800f,
    val toleranceRange: Float = 40f,
    val isUnlocked: Boolean = false,
    val isMastered: Boolean = false,
    val srsInterval: Int = 1,
    val srsEaseFactor: Float = 2.5f,
    val srsRepetitions: Int = 0,
    val nextReviewDate: Long = 0L
) {
    fun canUnlock(unlockedNodeIds: Set<String>, currentInventory: Map<String, Int>): Boolean {
        if (isUnlocked) return false
        val hasPrereqs = requiredNodes.all { it in unlockedNodeIds }
        if (!hasPrereqs) return false
        val hasResources = resourceCosts.all { (res, cost) -> (currentInventory[res] ?: 0) >= cost }
        return hasResources
    }

    fun hasPrerequisites(unlockedNodeIds: Set<String>): Boolean {
        return requiredNodes.all { it in unlockedNodeIds }
    }
}

data class ResourceItem(
    val id: String,
    val name: String,
    val amount: Int,
    val unit: String = "u",
    val description: String = ""
)

data class UserStat(
    val category: ScienceCategory,
    val xp: Int,
    val level: Int
)
