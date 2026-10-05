package com.olesya.focusquest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olesya.focusquest.data.AppDatabase
import com.olesya.focusquest.data.FocusRepository
import com.olesya.focusquest.data.ProfileEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FocusRepository(AppDatabase.getInstance(application))

    val profile = repository.profile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileEntity()
    )

    init {
        viewModelScope.launch { repository.ensureProfile() }
    }

    fun completeSession(minutes: Int) {
        viewModelScope.launch { repository.completeSession(minutes) }
    }

    fun levelFor(xp: Int): Int = (xp / 100) + 1
    fun xpIntoLevel(xp: Int): Int = xp % 100

    fun rankFor(level: Int): String = when {
        level >= 20 -> "Мастер концентрации"
        level >= 10 -> "Фокус-рыцарь"
        level >= 5 -> "Искатель"
        else -> "Новичок"
    }
}
