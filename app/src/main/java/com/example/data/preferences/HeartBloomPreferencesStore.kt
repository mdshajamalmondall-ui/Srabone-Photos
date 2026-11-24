package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppLanguage
import com.example.data.model.HeartBloomPreferences
import com.example.data.model.ParticleDensity
import com.example.data.model.ShowcaseAnimationMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.heartBloomDataStore by preferencesDataStore(name = "heartbloom_prefs")

class HeartBloomPreferencesStore(private val context: Context) {

    private val fastPrefs by lazy {
        context.getSharedPreferences("heartbloom_fast_v1", Context.MODE_PRIVATE)
    }

    private object Keys {
        val ANIMATION_MODE = stringPreferencesKey("animation_mode")
        val SLIDE_DURATION_MS = longPreferencesKey("slide_duration_ms")
        val PARTICLE_DENSITY = stringPreferencesKey("particle_density")
        val LANGUAGE = stringPreferencesKey("language")
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val BACKGROUND_MUSIC_ENABLED = booleanPreferencesKey("background_music_enabled")
        val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
    }

    fun getFastPreferences(): HeartBloomPreferences {
        val mode = runCatching {
            ShowcaseAnimationMode.valueOf(
                fastPrefs.getString("animation_mode", ShowcaseAnimationMode.HEART_BLOOM.name)
                    ?: ShowcaseAnimationMode.HEART_BLOOM.name
            )
        }.getOrDefault(ShowcaseAnimationMode.HEART_BLOOM)

        val density = runCatching {
            ParticleDensity.valueOf(
                fastPrefs.getString("particle_density", ParticleDensity.LUSH.name)
                    ?: ParticleDensity.LUSH.name
            )
        }.getOrDefault(ParticleDensity.LUSH)

        val language = runCatching {
            AppLanguage.valueOf(
                fastPrefs.getString("language", AppLanguage.EN.name)
                    ?: AppLanguage.EN.name
            )
        }.getOrDefault(AppLanguage.EN)

        val duration = fastPrefs.getLong("slide_duration_ms", 4500L).coerceIn(2500L, 9000L)
        val shuffle = fastPrefs.getBoolean("shuffle_enabled", false)
        val musicEnabled = fastPrefs.getBoolean("background_music_enabled", true)
        val favorites = fastPrefs.getStringSet("favorite_ids", emptySet()).orEmpty().toSet()

        return HeartBloomPreferences(
            animationMode = mode,
            slideDurationMs = duration,
            particleDensity = density,
            language = language,
            shuffleEnabled = shuffle,
            backgroundMusicEnabled = musicEnabled,
            favoriteIds = favorites
        )
    }

    val preferencesFlow: Flow<HeartBloomPreferences> = context.heartBloomDataStore.data.map { prefs ->
        val mode = runCatching {
            ShowcaseAnimationMode.valueOf(
                prefs[Keys.ANIMATION_MODE] ?: ShowcaseAnimationMode.HEART_BLOOM.name
            )
        }.getOrDefault(ShowcaseAnimationMode.HEART_BLOOM)

        val density = runCatching {
            ParticleDensity.valueOf(
                prefs[Keys.PARTICLE_DENSITY] ?: ParticleDensity.LUSH.name
            )
        }.getOrDefault(ParticleDensity.LUSH)

        val language = runCatching {
            AppLanguage.valueOf(
                prefs[Keys.LANGUAGE] ?: AppLanguage.EN.name
            )
        }.getOrDefault(AppLanguage.EN)

        HeartBloomPreferences(
            animationMode = mode,
            slideDurationMs = (prefs[Keys.SLIDE_DURATION_MS] ?: 4500L).coerceIn(2500L, 9000L),
            particleDensity = density,
            language = language,
            shuffleEnabled = prefs[Keys.SHUFFLE_ENABLED] ?: false,
            backgroundMusicEnabled = prefs[Keys.BACKGROUND_MUSIC_ENABLED] ?: true,
            favoriteIds = prefs[Keys.FAVORITE_IDS] ?: emptySet()
        )
    }

    suspend fun setAnimationMode(mode: ShowcaseAnimationMode) {
        fastPrefs.edit().putString("animation_mode", mode.name).apply()
        context.heartBloomDataStore.edit { it[Keys.ANIMATION_MODE] = mode.name }
    }

    suspend fun setSlideDurationMs(durationMs: Long) {
        val clamped = durationMs.coerceIn(2500L, 9000L)
        fastPrefs.edit().putLong("slide_duration_ms", clamped).apply()
        context.heartBloomDataStore.edit { it[Keys.SLIDE_DURATION_MS] = clamped }
    }

    suspend fun setParticleDensity(density: ParticleDensity) {
        fastPrefs.edit().putString("particle_density", density.name).apply()
        context.heartBloomDataStore.edit { it[Keys.PARTICLE_DENSITY] = density.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        fastPrefs.edit().putString("language", language.name).apply()
        context.heartBloomDataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        fastPrefs.edit().putBoolean("shuffle_enabled", enabled).apply()
        context.heartBloomDataStore.edit { it[Keys.SHUFFLE_ENABLED] = enabled }
    }

    suspend fun setBackgroundMusicEnabled(enabled: Boolean) {
        fastPrefs.edit().putBoolean("background_music_enabled", enabled).apply()
        context.heartBloomDataStore.edit { it[Keys.BACKGROUND_MUSIC_ENABLED] = enabled }
    }

    suspend fun updateFavorites(favoriteIds: Set<String>) {
        fastPrefs.edit().putStringSet("favorite_ids", favoriteIds).apply()
        context.heartBloomDataStore.edit { it[Keys.FAVORITE_IDS] = favoriteIds }
    }
}
