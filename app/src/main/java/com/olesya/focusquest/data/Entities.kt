package com.olesya.focusquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val coins: Int = 0,
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val lastSessionDate: String? = null
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val earnedXp: Int,
    val earnedCoins: Int,
    val completedAt: Long = System.currentTimeMillis()
)
