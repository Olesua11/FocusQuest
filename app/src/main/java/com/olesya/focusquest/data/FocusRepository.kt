package com.olesya.focusquest.data

import androidx.room.withTransaction
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FocusRepository(private val db: AppDatabase) {
    private val dao = db.focusDao()

    val profile: Flow<ProfileEntity> = dao.observeProfile().map { it ?: ProfileEntity() }

    suspend fun ensureProfile() {
        if (dao.getProfile() == null) dao.saveProfile(ProfileEntity())
    }

    suspend fun completeSession(durationMinutes: Int) {
        val xpReward = when {
            durationMinutes >= 50 -> 80
            durationMinutes >= 25 -> 40
            else -> 20
        }
        val coinReward = when {
            durationMinutes >= 50 -> 20
            durationMinutes >= 25 -> 10
            else -> 5
        }
        val today = LocalDate.now()

        db.withTransaction {
            val current = dao.getProfile() ?: ProfileEntity()
            val previous = current.lastSessionDate?.let(LocalDate::parse)
            val newStreak = when {
                previous == null -> 1
                previous == today -> current.streak
                ChronoUnit.DAYS.between(previous, today) == 1L -> current.streak + 1
                else -> 1
            }
            val updated = current.copy(
                xp = current.xp + xpReward,
                coins = current.coins + coinReward,
                totalSessions = current.totalSessions + 1,
                totalMinutes = current.totalMinutes + durationMinutes,
                streak = newStreak,
                bestStreak = maxOf(current.bestStreak, newStreak),
                lastSessionDate = today.toString()
            )
            dao.saveProfile(updated)
            dao.insertSession(
                FocusSessionEntity(
                    durationMinutes = durationMinutes,
                    earnedXp = xpReward,
                    earnedCoins = coinReward
                )
            )
        }
    }
}
