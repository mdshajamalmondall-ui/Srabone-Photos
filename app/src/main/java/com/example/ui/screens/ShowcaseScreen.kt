package com.example.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FilterVintage
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.example.data.MemoryCatalog
import com.example.data.model.AppLanguage
import com.example.data.model.MemoryPhoto
import com.example.data.model.ShowcaseAnimationMode
import com.example.ui.ShowcaseUiState
import com.example.ui.components.AmbientBloomCanvas
import com.example.ui.components.InteractiveTouchSparkleOverlay
import com.example.ui.components.ShowcaseAudioEffect
import com.example.ui.components.drawHeartShape
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonBloom
import com.example.ui.theme.IvorySilk
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MutedRose
import com.example.ui.theme.ObsidianVelvet
import com.example.ui.theme.RoseGlass
import com.example.ui.theme.RoseVelvet
import com.example.ui.theme.SoftBlush
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShowcaseScreen(
    state: ShowcaseUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleImmersive: () -> Unit,
    onSelectAnimationMode: (ShowcaseAnimationMode) -> Unit,
    onToggleFavorite: (photoId: String, forceFavorite: Boolean) -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleMusic: () -> Unit,
    onOpenSettings: () -> Unit
) {
    BackHandler(onBack = onBack)

    // Automatically plays background song ("Dilruba Dilnasheen") while showcasing photos
    ShowcaseAudioEffect(enabled = state.backgroundMusicEnabled)

    val context = LocalContext.current
    val view = LocalView.current
    val strings = LocalAppStrings.current
    val currentPhoto = state.currentPhoto
    val isFavorite = currentPhoto.id in state.favoriteIds
    val dynamicAccent = Color(currentPhoto.accentHex)

    // Preload upcoming 2 images so slide/bloom transitions never flicker
    LaunchedEffect(state.currentIndex, state.memories) {
        val total = state.memories.size
        if (total > 1) {
            listOf(
                MemoryCatalog.nextIndex(state.currentIndex, total),
                MemoryCatalog.nextIndex(MemoryCatalog.nextIndex(state.currentIndex, total), total)
            ).distinct().forEach { upcomingIdx ->
                val uri = state.memories[upcomingIdx].assetUri
                val preload = ImageRequest.Builder(context)
                    .data(uri)
                    .size(1440)
                    .build()
                context.imageLoader.enqueue(preload)
            }
        }
    }

    // Smooth auto-play progress indicator
    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(state.currentIndex, state.isPlaying, state.slideDurationMs, state.memories.size) {
        progressAnim.snapTo(0f)
        if (state.isPlaying && state.memories.size > 1) {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = state.slideDurationMs.toInt(),
                    easing = LinearEasing
                )
            )
            onNext()
        }
    }

    fun gentleTapHaptic() {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.VIRTUAL_KEY
        }
        view.performHapticFeedback(flag)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBloomCanvas(
            density = state.particleDensity,
            accentColor = dynamicAccent
        )

        InteractiveTouchSparkleOverlay { triggerBurstAt ->
            var horizontalDragAccumulator by remember { mutableFloatStateOf(0f) }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(state.memories.size) {
                        detectHorizontalDragGestures(
                            onDragStart = { horizontalDragAccumulator = 0f },
                            onHorizontalDrag = { _, dragAmount ->
                                horizontalDragAccumulator += dragAmount
                            },
                            onDragEnd = {
                                if (horizontalDragAccumulator < -90f) {
                                    gentleTapHaptic()
                                    onNext()
                                } else if (horizontalDragAccumulator > 90f) {
                                    gentleTapHaptic()
                                    onPrevious()
                                }
                                horizontalDragAccumulator = 0f
                            }
                        )
                    }
            ) {
                val isWide = maxWidth >= 700.dp

                // Main animated photo stage
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (state.isImmersive) {
                                Modifier.padding(horizontal = 10.dp, vertical = 16.dp)
                            } else {
                                Modifier
                                    .statusBarsPadding()
                                    .navigationBarsPadding()
                                    .padding(
                                        top = if (state.backgroundMusicEnabled) 104.dp else 74.dp,
                                        bottom = 148.dp,
                                        start = if (isWide) 64.dp else 16.dp,
                                        end = if (isWide) 64.dp else 16.dp
                                    )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = Triple(currentPhoto.id, state.currentIndex, state.animationMode),
                        transitionSpec = {
                            when (state.animationMode) {
                                ShowcaseAnimationMode.HEART_BLOOM -> {
                                    (fadeIn(tween(620, easing = FastOutSlowInEasing)) +
                                        scaleIn(
                                            initialScale = 0.78f,
                                            animationSpec = spring(
                                                dampingRatio = 0.78f,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )) togetherWith
                                        (fadeOut(tween(440)) + scaleOut(targetScale = 1.08f, animationSpec = tween(440)))
                                }

                                ShowcaseAnimationMode.LOVE_SLIDE -> {
                                    val dir = if (state.isForward) 1 else -1
                                    (slideInHorizontally(
                                        animationSpec = tween(560, easing = FastOutSlowInEasing),
                                        initialOffsetX = { fullWidth -> dir * fullWidth }
                                    ) + fadeIn(tween(480))) togetherWith
                                        (slideOutHorizontally(
                                            animationSpec = tween(520, easing = FastOutSlowInEasing),
                                            targetOffsetX = { fullWidth -> -dir * (fullWidth / 2) }
                                        ) + fadeOut(tween(420)))
                                }

                                ShowcaseAnimationMode.VELVET_FADE -> {
                                    (fadeIn(tween(780, easing = FastOutSlowInEasing)) +
                                        scaleIn(initialScale = 1.04f, animationSpec = tween(780))) togetherWith
                                        fadeOut(tween(640))
                                }

                                ShowcaseAnimationMode.MEMORY_CARD -> {
                                    val dir = if (state.isForward) 1 else -1
                                    (slideInHorizontally(
                                        animationSpec = spring(
                                            dampingRatio = 0.80f,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        initialOffsetX = { fullWidth -> dir * (fullWidth * 0.65f).toInt() }
                                    ) + scaleIn(initialScale = 0.88f, animationSpec = tween(500)) +
                                        fadeIn(tween(420))) togetherWith
                                        (slideOutHorizontally(
                                            animationSpec = tween(420),
                                            targetOffsetX = { fullWidth -> -dir * (fullWidth * 0.45f).toInt() }
                                        ) + scaleOut(targetScale = 0.90f, animationSpec = tween(420)) +
                                            fadeOut(tween(360)))
                                }
                            }
                        },
                        label = "photo_showcase_transition"
                    ) { (photoId, idx, mode) ->
                        val photo = state.memories.getOrElse(idx) { currentPhoto }
                        AnimatedMemoryFrame(
                            photo = photo,
                            frameIndex = idx,
                            mode = mode,
                            language = state.language,
                            isPlaying = state.isPlaying,
                            isFavorite = photoId in state.favoriteIds,
                            onSingleTap = { onToggleImmersive() },
                            onDoubleTap = { tapOffset ->
                                gentleTapHaptic()
                                triggerBurstAt(tapOffset, true)
                                onToggleFavorite(photo.id, true)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 560.dp)
                        )
                    }
                }

                // Top Chrome Bar + Glowing Progress Line
                AnimatedVisibility(
                    visible = !state.isImmersive,
                    enter = fadeIn(tween(240)),
                    exit = fadeOut(tween(220)),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 640.dp)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top Progress Ribbon
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.14f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressAnim.value.coerceIn(0f, 1f))
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(RoseVelvet, ChampagneGold)
                                        )
                                    )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = RoseGlass.copy(alpha = 0.78f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                            ) {
                                IconButton(
                                    onClick = {
                                        gentleTapHaptic()
                                        onBack()
                                    },
                                    modifier = Modifier.testTag("showcase_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = strings.backHomeCd,
                                        tint = IvorySilk
                                    )
                                }
                            }

                            // Counter + Mood Pill
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = RoseGlass.copy(alpha = 0.80f),
                                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val countText = String.format(
                                        "%02d / %02d",
                                        state.currentIndex + 1,
                                        state.memories.size
                                    )
                                    Text(
                                        text = countText,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = ChampagneGold
                                    )
                                    Text(
                                        text = "•",
                                        color = MutedRose
                                    )
                                    Text(
                                        text = if (state.language == AppLanguage.BN) currentPhoto.moodBn else currentPhoto.moodEn,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = IvorySilk,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (state.favoriteIds.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (state.favoritesOnly) {
                                            RoseVelvet.copy(alpha = 0.35f)
                                        } else {
                                            RoseGlass.copy(alpha = 0.78f)
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (state.favoritesOnly) RoseVelvet else Color.White.copy(alpha = 0.14f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable {
                                                gentleTapHaptic()
                                                onToggleFavoritesFilter()
                                            }
                                            .testTag("showcase_favorites_filter")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Favorite,
                                                contentDescription = null,
                                                tint = RoseVelvet,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (state.favoritesOnly) strings.savedMoments else strings.allMoments,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = IvorySilk
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = if (state.backgroundMusicEnabled) {
                                        ChampagneGold.copy(alpha = 0.22f)
                                    } else {
                                        RoseGlass.copy(alpha = 0.78f)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (state.backgroundMusicEnabled) {
                                            ChampagneGold.copy(alpha = 0.5f)
                                        } else {
                                            Color.White.copy(alpha = 0.14f)
                                        }
                                    )
                                ) {
                                    IconButton(
                                        onClick = {
                                            gentleTapHaptic()
                                            onToggleMusic()
                                        },
                                        modifier = Modifier.testTag("showcase_music_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (state.backgroundMusicEnabled) {
                                                Icons.AutoMirrored.Rounded.VolumeUp
                                            } else {
                                                Icons.AutoMirrored.Rounded.VolumeOff
                                            },
                                            contentDescription = if (state.backgroundMusicEnabled) {
                                                strings.muteAudioCd
                                            } else {
                                                strings.unmuteAudioCd
                                            },
                                            tint = if (state.backgroundMusicEnabled) ChampagneGold else MutedRose
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = RoseGlass.copy(alpha = 0.78f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                                ) {
                                    IconButton(
                                        onClick = onOpenSettings,
                                        modifier = Modifier.testTag("showcase_settings_button")
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

                        AnimatedVisibility(visible = state.backgroundMusicEnabled) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = RoseGlass.copy(alpha = 0.70f),
                                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.25f)),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.MusicNote,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = strings.songTrackName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ChampagneGold.copy(alpha = 0.92f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Control Dock: Animation Style Selector + Transport Controls
                AnimatedVisibility(
                    visible = !state.isImmersive,
                    enter = fadeIn(tween(240)),
                    exit = fadeOut(tween(220)),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .navigationBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Animation mode pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            val modeItems = listOf(
                                ShowcaseAnimationMode.HEART_BLOOM to strings.modeHeartBloom,
                                ShowcaseAnimationMode.LOVE_SLIDE to strings.modeLoveSlide,
                                ShowcaseAnimationMode.VELVET_FADE to strings.modeVelvetFade,
                                ShowcaseAnimationMode.MEMORY_CARD to strings.modeMemoryCard
                            )
                            modeItems.forEach { (mode, label) ->
                                val selected = state.animationMode == mode
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) {
                                        ChampagneGold.copy(alpha = 0.22f)
                                    } else {
                                        RoseGlass.copy(alpha = 0.76f)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) ChampagneGold else Color.White.copy(alpha = 0.14f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            gentleTapHaptic()
                                            onSelectAnimationMode(mode)
                                        }
                                        .testTag("mode_pill_${mode.name}")
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) ChampagneGold else IvorySilk.copy(alpha = 0.85f),
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // Playback Glass Bar
                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = RoseGlass.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.30f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    onClick = {
                                        gentleTapHaptic()
                                        onToggleShuffle()
                                    },
                                    modifier = Modifier.testTag("showcase_shuffle_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shuffle,
                                        contentDescription = strings.shuffleCd,
                                        tint = if (state.shuffleEnabled) ChampagneGold else MutedRose
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        gentleTapHaptic()
                                        onPrevious()
                                    },
                                    modifier = Modifier.testTag("showcase_previous_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SkipPrevious,
                                        contentDescription = strings.previousCd,
                                        tint = IvorySilk,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                // Play / Pause Champagne Gold Pill Button
                                Surface(
                                    shape = CircleShape,
                                    color = ChampagneGold,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            gentleTapHaptic()
                                            onTogglePlayPause()
                                        }
                                        .testTag("showcase_play_pause_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (state.isPlaying) {
                                                Icons.Rounded.Pause
                                            } else {
                                                Icons.Rounded.PlayArrow
                                            },
                                            contentDescription = if (state.isPlaying) strings.pauseCd else strings.playCd,
                                            tint = ObsidianVelvet,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        gentleTapHaptic()
                                        onNext()
                                    },
                                    modifier = Modifier.testTag("showcase_next_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SkipNext,
                                        contentDescription = strings.nextCd,
                                        tint = IvorySilk,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                val favScale by animateFloatAsState(
                                    targetValue = if (isFavorite) 1.18f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = 0.55f,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "fav_heart_scale"
                                )
                                IconButton(
                                    onClick = {
                                        gentleTapHaptic()
                                        if (!isFavorite) {
                                            triggerBurstAt(
                                                Offset(view.width * 0.5f, view.height * 0.48f),
                                                true
                                            )
                                        }
                                        onToggleFavorite(currentPhoto.id, false)
                                    },
                                    modifier = Modifier
                                        .graphicsLayer(scaleX = favScale, scaleY = favScale)
                                        .testTag("showcase_favorite_button")
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) {
                                            Icons.Rounded.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        contentDescription = strings.favoriteCd,
                                        tint = if (isFavorite) RoseVelvet else MutedRose
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedMemoryFrame(
    photo: MemoryPhoto,
    frameIndex: Int,
    mode: ShowcaseAnimationMode,
    language: AppLanguage,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onSingleTap: () -> Unit,
    onDoubleTap: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current

    // Ken Burns slow cinematic zoom
    val kenBurnsScale = remember(photo.id) { Animatable(1.0f) }
    LaunchedEffect(photo.id, isPlaying) {
        if (isPlaying) {
            kenBurnsScale.animateTo(
                targetValue = 1.08f,
                animationSpec = tween(durationMillis = 6500, easing = LinearEasing)
            )
        }
    }

    // Memory Card gentle tilt settle
    val cardTilt = remember(photo.id, mode) {
        if (mode == ShowcaseAnimationMode.MEMORY_CARD) {
            if (frameIndex % 2 == 0) -2.4f else 2.4f
        } else {
            0f
        }
    }
    val animatedTilt by animateFloatAsState(
        targetValue = cardTilt,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
        label = "card_tilt"
    )

    // Bloom petal float animation around the frame in HEART_BLOOM mode
    val bloomReveal = remember(photo.id, mode) { Animatable(0f) }
    LaunchedEffect(photo.id, mode) {
        if (mode == ShowcaseAnimationMode.HEART_BLOOM) {
            bloomReveal.snapTo(0f)
            bloomReveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
            )
        }
    }

    val shape = RoundedCornerShape(if (mode == ShowcaseAnimationMode.MEMORY_CARD) 22.dp else 28.dp)
    val titleText = if (language == AppLanguage.BN) photo.titleBn else photo.titleEn

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = animatedTilt
            }
            .shadow(
                elevation = 24.dp,
                shape = shape,
                ambientColor = CrimsonBloom.copy(alpha = 0.5f),
                spotColor = ChampagneGold.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(ObsidianVelvet)
            .border(
                width = 1.3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ChampagneGold.copy(alpha = 0.65f),
                        RoseVelvet.copy(alpha = 0.45f),
                        ChampagneGold.copy(alpha = 0.35f)
                    )
                ),
                shape = shape
            )
            .pointerInput(photo.id) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = { offset -> onDoubleTap(offset) }
                )
            }
    ) {
        val imageRequest = remember(photo.assetUri) {
            ImageRequest.Builder(context)
                .data(photo.assetUri)
                .size(1440)
                .crossfade(false)
                .build()
        }

        AsyncImage(
            model = imageRequest,
            contentDescription = titleText,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = kenBurnsScale.value
                    scaleY = kenBurnsScale.value
                }
        )

        // Subtle atmospheric velvet vignette so overlay text stays effortlessly readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            ObsidianVelvet.copy(alpha = 0.35f),
                            ObsidianVelvet.copy(alpha = 0.86f)
                        )
                    )
                )
        )

        // Blooming floating hearts overlay during HEART_BLOOM entry
        if (mode == ShowcaseAnimationMode.HEART_BLOOM && bloomReveal.value < 0.99f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val t = bloomReveal.value
                val fade = (1f - t).coerceIn(0f, 1f)
                val center = Offset(size.width / 2f, size.height / 2f)
                for (i in 0 until 8) {
                    val angle = (i / 8f) * (2f * PI).toFloat()
                    val radius = size.minDimension * (0.14f + 0.42f * t)
                    val pos = Offset(
                        x = center.x + cos(angle) * radius,
                        y = center.y + sin(angle) * radius - (28.dp.toPx() * t)
                    )
                    drawHeartShape(
                        center = pos,
                        size = (16.dp.toPx() * (1f - t * 0.35f)),
                        color = (if (i % 2 == 0) ChampagneGold else SoftBlush).copy(alpha = fade * 0.88f)
                    )
                }
            }
        }

        // Caption Plaque at Bottom of Photo Frame
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (mode == ShowcaseAnimationMode.HEART_BLOOM) {
                        Icons.Rounded.FilterVintage
                    } else {
                        Icons.Rounded.AutoAwesome
                    },
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = IvorySilk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = RoseVelvet,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = strings.doubleTapHint,
                style = MaterialTheme.typography.labelMedium,
                color = MutedRose.copy(alpha = 0.9f)
            )
        }
    }
}
