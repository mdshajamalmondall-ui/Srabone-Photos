package com.example.ui.components

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun ShowcaseAudioEffect(
    enabled: Boolean
) {
    val context = LocalContext.current
    var isLifecycleActive by remember { mutableStateOf(true) }

    val mediaPlayer = remember {
        runCatching {
            MediaPlayer.create(context, R.raw.dilruba_dilnasheen)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                setVolume(0f, 0f)
            }
        }.getOrNull()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isLifecycleActive = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        isLifecycleActive = false
        runCatching {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer.pause()
            }
        }
    }

    LaunchedEffect(enabled, isLifecycleActive, mediaPlayer) {
        val player = mediaPlayer ?: return@LaunchedEffect
        if (enabled && isLifecycleActive) {
            runCatching {
                if (!player.isPlaying) {
                    player.setVolume(0f, 0f)
                    player.start()
                    // Gentle 600ms fade-in
                    val steps = 12
                    for (step in 1..steps) {
                        val vol = (step / steps.toFloat()) * 0.92f
                        player.setVolume(vol, vol)
                        delay(50L)
                    }
                } else {
                    player.setVolume(0.92f, 0.92f)
                }
            }
        } else {
            runCatching {
                if (player.isPlaying) {
                    player.pause()
                }
            }
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            runCatching {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer.stop()
                }
                mediaPlayer?.release()
            }
        }
    }
}
