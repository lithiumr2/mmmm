package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tech_nodes")
data class TechNodeEntity(
    @PrimaryKey val id: String,
    val isUnlocked: Boolean,
    val isMastered: Boolean,
    val srsInterval: Int,
    val srsEaseFactor: Float,
    val srsRepetitions: Int,
    val nextReviewDate: Long
)

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Int
)

@Entity(tableName = "user_stats")
data class UserStatEntity(
    @PrimaryKey val category: String,
    val xp: Int,
    val level: Int
)
