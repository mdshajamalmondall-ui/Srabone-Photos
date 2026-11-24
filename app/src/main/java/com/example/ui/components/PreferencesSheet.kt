package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.HeartBloomPreferences
import com.example.data.model.ParticleDensity
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.IvorySilk
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MutedRose
import com.example.ui.theme.RoseGlass
import com.example.ui.theme.RoseVelvet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesBottomSheet(
    preferences: HeartBloomPreferences,
    onDismiss: () -> Unit,
    onSelectSpeed: (Long) -> Unit,
    onSelectParticleDensity: (ParticleDensity) -> Unit,
    onSelectBackgroundMusic: (Boolean) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onClearFavorites: () -> Unit
) {
    val strings = LocalAppStrings.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepBurgundy,
        contentColor = IvorySilk,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = strings.preferencesTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = IvorySilk
            )

            // Slide Speed
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Speed,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = strings.slideSpeedLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvorySilk
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val speeds = listOf(
                        3500L to strings.speedFast,
                        4500L to strings.speedBalanced,
                        6500L to strings.speedDreamy
                    )
                    speeds.forEach { (ms, label) ->
                        val selected = preferences.slideDurationMs == ms
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectSpeed(ms) },
                            label = { Text(label, maxLines = 1) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoseVelvet.copy(alpha = 0.28f),
                                selectedLabelColor = IvorySilk,
                                containerColor = RoseGlass,
                                labelColor = MutedRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = ChampagneGold,
                                borderColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("speed_chip_$ms")
                        )
                    }
                }
            }

            // Particle Glow
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = strings.particleGlowLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvorySilk
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val densities = listOf(
                        ParticleDensity.LUSH to strings.particleLush,
                        ParticleDensity.SUBTLE to strings.particleSubtle,
                        ParticleDensity.OFF to strings.particleOff
                    )
                    densities.forEach { (density, label) ->
                        val selected = preferences.particleDensity == density
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectParticleDensity(density) },
                            label = { Text(label, maxLines = 1) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoseVelvet.copy(alpha = 0.28f),
                                selectedLabelColor = IvorySilk,
                                containerColor = RoseGlass,
                                labelColor = MutedRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = ChampagneGold,
                                borderColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("particle_chip_${density.name}")
                        )
                    }
                }
            }

            // Background Song Toggle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = "${strings.backgroundSongLabel} • ${strings.songTrackName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = IvorySilk
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        true to strings.songOn,
                        false to strings.songOff
                    ).forEach { (enabled, label) ->
                        val selected = preferences.backgroundMusicEnabled == enabled
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectBackgroundMusic(enabled) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoseVelvet.copy(alpha = 0.28f),
                                selectedLabelColor = IvorySilk,
                                containerColor = RoseGlass,
                                labelColor = MutedRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = ChampagneGold,
                                borderColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("music_chip_$enabled")
                        )
                    }
                }
            }

            // Language Toggle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = strings.languageLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvorySilk
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        AppLanguage.EN to "English",
                        AppLanguage.BN to "বাংলা"
                    ).forEach { (lang, label) ->
                        val selected = preferences.language == lang
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectLanguage(lang) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChampagneGold.copy(alpha = 0.22f),
                                selectedLabelColor = ChampagneGold,
                                containerColor = RoseGlass,
                                labelColor = MutedRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = ChampagneGold,
                                borderColor = Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lang_chip_${lang.name}")
                        )
                    }
                }
            }

            if (preferences.favoriteIds.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearFavorites,
                    border = BorderStroke(1.dp, RoseVelvet.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_favorites_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = RoseVelvet
                    )
                    Text(
                        text = "  ${strings.clearFavorites} (${preferences.favoriteIds.size})",
                        color = RoseVelvet
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
