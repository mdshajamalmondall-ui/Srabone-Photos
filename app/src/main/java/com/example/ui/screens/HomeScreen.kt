package com.example.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FilterVintage
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoryCatalog
import com.example.data.model.AppLanguage
import com.example.data.model.HeartBloomPreferences
import com.example.data.model.ShowcaseAnimationMode
import com.example.ui.components.AmbientBloomCanvas
import com.example.ui.components.HeartBloomCenterpiece
import com.example.ui.components.InteractiveTouchSparkleOverlay
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonBloom
import com.example.ui.theme.IvorySilk
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MutedRose
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.RoseGlass
import com.example.ui.theme.RoseVelvet
import com.example.ui.theme.SoftBlush
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    preferences: HeartBloomPreferences,
    onOpenHeartBloom: () -> Unit,
    onOpenLoveShowcase: () -> Unit,
    onOpenMagicShuffle: () -> Unit,
    onOpenFavorites: () -> Unit,
    onToggleLanguage: () -> Unit,
    onShareApp: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val strings = LocalAppStrings.current
    val view = LocalView.current

    val whispers = if (preferences.language == AppLanguage.BN) {
        MemoryCatalog.loveWhispersBn
    } else {
        MemoryCatalog.loveWhispersEn
    }
    var whisperIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(whispers) {
        while (true) {
            delay(4200L)
            whisperIndex = (whisperIndex + 1) % whispers.size
        }
    }

    fun performGentleHaptic() {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.VIRTUAL_KEY
        }
        view.performHapticFeedback(flag)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Photo-free atmospheric canvas background
        AmbientBloomCanvas(
            density = preferences.particleDensity,
            accentColor = RoseVelvet
        )

        InteractiveTouchSparkleOverlay { triggerBurstAt ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                val isCompactHeight = maxHeight < 680.dp
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 560.dp)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Editorial Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = RoseGlass.copy(alpha = 0.72f),
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = strings.appTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IvorySilk,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = RoseGlass.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable {
                                        performGentleHaptic()
                                        onToggleLanguage()
                                    }
                                    .testTag("home_language_toggle")
                            ) {
                                Text(
                                    text = if (preferences.language == AppLanguage.EN) "বাংলা" else "EN",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ChampagneGold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = RoseGlass.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                            ) {
                                IconButton(
                                    onClick = {
                                        performGentleHaptic()
                                        onShareApp()
                                    },
                                    modifier = Modifier.testTag("home_share_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Share,
                                        contentDescription = strings.shareAppCd,
                                        tint = ChampagneGold
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = RoseGlass.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                            ) {
                                IconButton(
                                    onClick = {
                                        performGentleHaptic()
                                        onOpenSettings()
                                    },
                                    modifier = Modifier.testTag("home_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tune,
                                        contentDescription = strings.settingsCd,
                                        tint = IvorySilk
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(if (isCompactHeight) 8.dp else 18.dp))

                    // Hero Title + Animated Heart Centerpiece (Strictly NO photos on Home)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 8.dp else 12.dp)
                    ) {
                        Text(
                            text = strings.appTitle,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = if (isCompactHeight) 32.sp else 38.sp
                            ),
                            color = IvorySilk,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = strings.homeSubtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MutedRose,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(Modifier.height(if (isCompactHeight) 2.dp else 6.dp))

                        HeartBloomCenterpiece(
                            size = if (isCompactHeight) 196.dp else 238.dp,
                            contentDescriptionText = strings.tapHeartHint,
                            onClick = {
                                performGentleHaptic()
                                if (preferences.animationMode == ShowcaseAnimationMode.LOVE_SLIDE) {
                                    onOpenLoveShowcase()
                                } else {
                                    onOpenHeartBloom()
                                }
                            },
                            modifier = Modifier.testTag("home_heart_centerpiece")
                        )

                        Text(
                            text = strings.tapHeartHint,
                            style = MaterialTheme.typography.labelMedium,
                            color = ChampagneGold.copy(alpha = 0.88f),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(Modifier.height(if (isCompactHeight) 12.dp else 20.dp))

                    // Primary Options: "Heart Bloom" & "Love" + Premium Quick Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LuxuryOptionCard(
                            title = strings.heartBloomTitle,
                            subtitle = strings.heartBloomSubtitle,
                            icon = Icons.Rounded.FilterVintage,
                            gradientColors = listOf(
                                CrimsonBloom.copy(alpha = 0.42f),
                                RoseGlass.copy(alpha = 0.90f)
                            ),
                            borderColor = ChampagneGold.copy(alpha = 0.55f),
                            accentTint = ChampagneGold,
                            onClick = {
                                performGentleHaptic()
                                onOpenHeartBloom()
                            },
                            modifier = Modifier.testTag("option_heart_bloom")
                        )

                        LuxuryOptionCard(
                            title = strings.loveTitle,
                            subtitle = strings.loveSubtitle,
                            icon = Icons.Rounded.Favorite,
                            gradientColors = listOf(
                                RoseVelvet.copy(alpha = 0.34f),
                                RoseGlass.copy(alpha = 0.88f)
                            ),
                            borderColor = SoftBlush.copy(alpha = 0.45f),
                            accentTint = SoftBlush,
                            onClick = {
                                performGentleHaptic()
                                onOpenLoveShowcase()
                            },
                            modifier = Modifier.testTag("option_love")
                        )

                        // Premium Secondary Strip: Magic Shuffle + Loved Moments counter (still zero photos!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = RoseGlass.copy(alpha = 0.68f),
                                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        performGentleHaptic()
                                        onOpenMagicShuffle()
                                    }
                                    .testTag("option_magic_shuffle")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shuffle,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    Text(
                                        text = strings.magicShuffle,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = IvorySilk,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (preferences.favoriteIds.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = RoseGlass.copy(alpha = 0.68f),
                                    border = BorderStroke(1.dp, RoseVelvet.copy(alpha = 0.35f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            performGentleHaptic()
                                            onOpenFavorites()
                                        }
                                        .testTag("option_loved_moments")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Favorite,
                                            contentDescription = null,
                                            tint = RoseVelvet,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Text(
                                            text = "${strings.savedMoments} (${preferences.favoriteIds.size})",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = IvorySilk,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Bottom poetic whisper & designer credit
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    ) {
                        AnimatedContent(
                            targetState = whispers[whisperIndex],
                            transitionSpec = {
                                fadeIn(tween(700)) togetherWith fadeOut(tween(500))
                            },
                            label = "whisper_fade"
                        ) { whisperText ->
                            Text(
                                text = "“$whisperText”",
                                fontFamily = PlayfairDisplayFontFamily,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.sp,
                                color = MutedRose.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = strings.designCredit,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                letterSpacing = 1.4.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ChampagneGold.copy(alpha = 0.78f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("home_design_credit")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LuxuryOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    borderColor: Color,
    accentTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerAlpha by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = BorderStroke(1.2.dp, borderColor.copy(alpha = borderColor.alpha * shimmerAlpha)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentTint.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, accentTint.copy(alpha = 0.45f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = IvorySilk
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedRose,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = ChampagneGold.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
