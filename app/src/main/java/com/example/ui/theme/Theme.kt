package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage

private val HeartBloomColorScheme = darkColorScheme(
    primary = RoseVelvet,
    onPrimary = IvorySilk,
    primaryContainer = CrimsonBloom,
    onPrimaryContainer = IvorySilk,
    secondary = ChampagneGold,
    onSecondary = ObsidianVelvet,
    secondaryContainer = RoseGlass,
    onSecondaryContainer = ChampagneGold,
    tertiary = SoftBlush,
    onTertiary = ObsidianVelvet,
    background = ObsidianVelvet,
    onBackground = IvorySilk,
    surface = DeepBurgundy,
    onSurface = IvorySilk,
    surfaceVariant = PlumSurfaceVariant,
    onSurfaceVariant = MutedRose,
    outline = RoseGlassBorder
)

private val HeartBloomShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun HeartBloomTheme(
    language: AppLanguage = AppLanguage.EN,
    content: @Composable () -> Unit
) {
    val strings = stringsForLanguage(language)
    CompositionLocalProvider(LocalAppStrings provides strings) {
        MaterialTheme(
            colorScheme = HeartBloomColorScheme,
            typography = HeartBloomTypography,
            shapes = HeartBloomShapes,
            content = content
        )
    }
}
