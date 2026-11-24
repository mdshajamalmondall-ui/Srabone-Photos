package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MemoryCatalog
import com.example.data.model.AppLanguage
import com.example.data.model.HeartBloomPreferences
import com.example.data.model.MemoryPhoto
import com.example.data.model.ParticleDensity
import com.example.data.model.ShowcaseAnimationMode
import com.example.data.preferences.HeartBloomPreferencesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShowcaseUiState(
    val memories: List<MemoryPhoto> = MemoryCatalog.allMemories,
    val currentIndex: Int = 0,
    val isForward: Boolean = true,
    val isPlaying: Boolean = true,
    val isImmersive: Boolean = false,
    val favoritesOnly: Boolean = false,
    val animationMode: ShowcaseAnimationMode = ShowcaseAnimationMode.HEART_BLOOM,
    val slideDurationMs: Long = 4500L,
    val particleDensity: ParticleDensity = ParticleDensity.LUSH,
    val shuffleEnabled: Boolean = false,
    val backgroundMusicEnabled: Boolean = true,
    val favoriteIds: Set<String> = emptySet(),
    val language: AppLanguage = AppLanguage.EN
) {
    val currentPhoto: MemoryPhoto
        get() = memories.getOrElse(currentIndex) { MemoryCatalog.allMemories.first() }
}

class HeartBloomViewModel(application: Application) : AndroidViewModel(application) {

    private val store = HeartBloomPreferencesStore(application.applicationContext)
    private val initialPrefs = store.getFastPreferences()

    val preferences: StateFlow<HeartBloomPreferences> = store.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = initialPrefs
        )

    private val _currentIndex = MutableStateFlow(0)
    private val _isForward = MutableStateFlow(true)
    private val _isPlaying = MutableStateFlow(true)
    private val _isImmersive = MutableStateFlow(false)
    private val _favoritesOnly = MutableStateFlow(false)
    private val _shuffleSeed = MutableStateFlow(System.currentTimeMillis())

    val showcaseState: StateFlow<ShowcaseUiState> = combine(
        preferences,
        _currentIndex,
        _isForward,
        _isPlaying,
        _isImmersive,
        _favoritesOnly,
        _shuffleSeed
    ) { values ->
        val prefs = values[0] as HeartBloomPreferences
        val idx = values[1] as Int
        val forward = values[2] as Boolean
        val playing = values[3] as Boolean
        val immersive = values[4] as Boolean
        val favOnly = values[5] as Boolean
        val seed = values[6] as Long

        val ordered = MemoryCatalog.orderedMemories(
            favoritesOnly = favOnly,
            favoriteIds = prefs.favoriteIds,
            shuffleEnabled = prefs.shuffleEnabled,
            shuffleSeed = seed
        )
        val safeIdx = idx.coerceIn(0, (ordered.size - 1).coerceAtLeast(0))

        ShowcaseUiState(
            memories = ordered,
            currentIndex = safeIdx,
            isForward = forward,
            isPlaying = playing,
            isImmersive = immersive,
            favoritesOnly = favOnly && prefs.favoriteIds.isNotEmpty(),
            animationMode = prefs.animationMode,
            slideDurationMs = prefs.slideDurationMs,
            particleDensity = prefs.particleDensity,
            shuffleEnabled = prefs.shuffleEnabled,
            backgroundMusicEnabled = prefs.backgroundMusicEnabled,
            favoriteIds = prefs.favoriteIds,
            language = prefs.language
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ShowcaseUiState(
            animationMode = initialPrefs.animationMode,
            slideDurationMs = initialPrefs.slideDurationMs,
            particleDensity = initialPrefs.particleDensity,
            shuffleEnabled = initialPrefs.shuffleEnabled,
            backgroundMusicEnabled = initialPrefs.backgroundMusicEnabled,
            favoriteIds = initialPrefs.favoriteIds,
            language = initialPrefs.language
        )
    )

    fun startShowcase(
        mode: ShowcaseAnimationMode,
        shuffle: Boolean = false,
        favoritesOnly: Boolean = false
    ) {
        _favoritesOnly.value = favoritesOnly
        _isForward.value = true
        _isPlaying.value = true
        _isImmersive.value = false
        _currentIndex.value = 0
        if (shuffle) {
            _shuffleSeed.value = System.nanoTime()
        }
        viewModelScope.launch {
            store.setAnimationMode(mode)
            store.setShuffleEnabled(shuffle)
        }
    }

    fun setAnimationMode(mode: ShowcaseAnimationMode) {
        viewModelScope.launch {
            store.setAnimationMode(mode)
        }
    }

    fun nextPhoto() {
        val total = showcaseState.value.memories.size
        if (total <= 0) return
        _isForward.value = true
        _currentIndex.update { MemoryCatalog.nextIndex(it, total) }
    }

    fun previousPhoto() {
        val total = showcaseState.value.memories.size
        if (total <= 0) return
        _isForward.value = false
        _currentIndex.update { MemoryCatalog.previousIndex(it, total) }
    }

    fun togglePlayPause() {
        _isPlaying.update { !it }
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun toggleImmersive() {
        _isImmersive.update { !it }
    }

    fun toggleFavoritesFilter() {
        if (preferences.value.favoriteIds.isEmpty()) return
        _favoritesOnly.update { !it }
        _currentIndex.value = 0
    }

    fun toggleFavorite(photoId: String, forceFavorite: Boolean = false) {
        val current = preferences.value.favoriteIds
        val updated = if (forceFavorite) {
            current + photoId
        } else if (photoId in current) {
            current - photoId
        } else {
            current + photoId
        }
        viewModelScope.launch {
            store.updateFavorites(updated)
            if (updated.isEmpty()) {
                _favoritesOnly.value = false
            }
        }
    }

    fun toggleShuffle() {
        val nextShuffle = !preferences.value.shuffleEnabled
        _shuffleSeed.value = System.nanoTime()
        _currentIndex.value = 0
        viewModelScope.launch {
            store.setShuffleEnabled(nextShuffle)
        }
    }

    fun toggleBackgroundMusic() {
        setBackgroundMusicEnabled(!preferences.value.backgroundMusicEnabled)
    }

    fun setBackgroundMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            store.setBackgroundMusicEnabled(enabled)
        }
    }

    fun setSlideDurationMs(durationMs: Long) {
        viewModelScope.launch {
            store.setSlideDurationMs(durationMs)
        }
    }

    fun setParticleDensity(density: ParticleDensity) {
        viewModelScope.launch {
            store.setParticleDensity(density)
        }
    }

    fun toggleLanguage() {
        val next = if (preferences.value.language == AppLanguage.EN) {
            AppLanguage.BN
        } else {
            AppLanguage.EN
        }
        setLanguage(next)
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            store.setLanguage(language)
        }
    }

    fun clearAllFavorites() {
        _favoritesOnly.value = false
        viewModelScope.launch {
            store.updateFavorites(emptySet())
        }
    }
}
