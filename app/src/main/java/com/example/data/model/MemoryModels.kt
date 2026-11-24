package com.example.data.model

enum class ShowcaseAnimationMode {
    HEART_BLOOM,
    LOVE_SLIDE,
    VELVET_FADE,
    MEMORY_CARD
}

enum class ParticleDensity {
    LUSH,
    SUBTLE,
    OFF
}

enum class AppLanguage {
    EN,
    BN
}

data class MemoryPhoto(
    val id: String,
    val assetFileName: String,
    val titleEn: String,
    val titleBn: String,
    val moodEn: String,
    val moodBn: String,
    val accentHex: Long
) {
    val assetUri: String
        get() = "file:///android_asset/memories/$assetFileName"
}

data class HeartBloomPreferences(
    val animationMode: ShowcaseAnimationMode = ShowcaseAnimationMode.HEART_BLOOM,
    val slideDurationMs: Long = 4500L,
    val particleDensity: ParticleDensity = ParticleDensity.LUSH,
    val language: AppLanguage = AppLanguage.EN,
    val shuffleEnabled: Boolean = false,
    val backgroundMusicEnabled: Boolean = true,
    val favoriteIds: Set<String> = emptySet()
)
