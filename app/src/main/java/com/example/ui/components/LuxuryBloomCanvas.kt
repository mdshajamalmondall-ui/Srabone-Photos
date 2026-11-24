package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import com.example.data.model.ParticleDensity
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonBloom
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.ObsidianVelvet
import com.example.ui.theme.RoseVelvet
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.WarmGoldGlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class AmbientParticle(
    val xFraction: Float,
    val yOffset: Float,
    val speed: Float,
    val radiusDp: Float,
    val swayAmp: Float,
    val phase: Float,
    val isPetal: Boolean,
    val isGold: Boolean,
    val baseAlpha: Float
)

data class TouchBurstEffect(
    val id: Long,
    val origin: Offset,
    val color: Color,
    val isHeart: Boolean,
    val progress: Animatable<Float, *> = Animatable(0f)
)

@Composable
fun AmbientBloomCanvas(
    density: ParticleDensity,
    accentColor: Color = RoseVelvet,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_bloom")
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_drift"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora_pulse"
    )

    val particleCount = when (density) {
        ParticleDensity.LUSH -> 38
        ParticleDensity.SUBTLE -> 18
        ParticleDensity.OFF -> 0
    }

    val particles = remember(particleCount) {
        val rng = Random(4207)
        List(particleCount) { idx ->
            AmbientParticle(
                xFraction = rng.nextFloat(),
                yOffset = rng.nextFloat(),
                speed = 0.6f + rng.nextFloat() * 0.9f,
                radiusDp = 1.6f + rng.nextFloat() * 3.8f,
                swayAmp = 10f + rng.nextFloat() * 24f,
                phase = rng.nextFloat() * (2f * PI).toFloat(),
                isPetal = idx % 4 == 0,
                isGold = idx % 3 != 0,
                baseAlpha = 0.25f + rng.nextFloat() * 0.55f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep velvet gradient base
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    ObsidianVelvet,
                    DeepBurgundy,
                    Color(0xFF14050D),
                    ObsidianVelvet
                )
            )
        )

        // Drifting radial atmospheric glow blobs
        val topGlowCenter = Offset(
            x = size.width * (0.28f + 0.08f * cos(glowPulse)),
            y = size.height * (0.24f + 0.05f * sin(glowPulse))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = topGlowCenter,
                radius = size.minDimension * 0.85f
            ),
            radius = size.minDimension * 0.85f,
            center = topGlowCenter
        )

        val bottomGlowCenter = Offset(
            x = size.width * (0.74f - 0.09f * sin(glowPulse * 0.8f)),
            y = size.height * (0.78f + 0.04f * cos(glowPulse * 0.8f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ChampagneGold.copy(alpha = 0.13f),
                    Color.Transparent
                ),
                center = bottomGlowCenter,
                radius = size.minDimension * 0.78f
            ),
            radius = size.minDimension * 0.78f,
            center = bottomGlowCenter
        )

        // Floating golden sparks and soft rose petals
        particles.forEach { p ->
            val rawY = (p.yOffset - driftProgress * p.speed)
            val normY = ((rawY % 1f) + 1f) % 1f
            val y = normY * (size.height + 60f) - 30f
            val sway = sin(driftProgress * (2f * PI).toFloat() * 2f + p.phase) * p.swayAmp
            val x = (p.xFraction * size.width + sway).coerceIn(0f, size.width)
            val fadeEdge = when {
                normY < 0.12f -> normY / 0.12f
                normY > 0.88f -> (1f - normY) / 0.12f
                else -> 1f
            }.coerceIn(0f, 1f)
            val color = if (p.isGold) ChampagneGold else SoftBlush
            val alpha = (p.baseAlpha * fadeEdge).coerceIn(0f, 1f)

            if (p.isPetal) {
                val r = p.radiusDp.dp.toPx() * 2.1f
                drawHeartShape(
                    center = Offset(x, y),
                    size = r,
                    color = color.copy(alpha = alpha * 0.78f)
                )
            } else {
                val r = p.radiusDp.dp.toPx()
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = r,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = color.copy(alpha = alpha * 0.28f),
                    radius = r * 2.4f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun InteractiveTouchSparkleOverlay(
    modifier: Modifier = Modifier,
    content: @Composable (triggerBurstAt: (Offset, Boolean) -> Unit) -> Unit
) {
    val bursts = remember { mutableStateListOf<TouchBurstEffect>() }
    val scope = rememberCoroutineScope()

    fun spawnBurst(origin: Offset, isHeart: Boolean) {
        val effect = TouchBurstEffect(
            id = System.nanoTime(),
            origin = origin,
            color = if (isHeart) RoseVelvet else ChampagneGold,
            isHeart = isHeart
        )
        bursts.add(effect)
        scope.launch {
            effect.progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = if (isHeart) 1100 else 750, easing = FastOutSlowInEasing)
            )
            bursts.remove(effect)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val downChange = event.changes.firstOrNull { it.pressed && !it.previousPressed }
                        if (downChange != null) {
                            spawnBurst(downChange.position, false)
                        }
                    }
                }
            }
    ) {
        content { offset, isHeart -> spawnBurst(offset, isHeart) }

        if (bursts.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                bursts.forEach { burst ->
                    val t = burst.progress.value
                    val fade = (1f - t).coerceIn(0f, 1f)
                    if (burst.isHeart) {
                        val spread = 135.dp.toPx() * t
                        for (i in 0 until 10) {
                            val angle = (i / 10f) * (2f * PI).toFloat() - (PI / 2f).toFloat()
                            val dist = spread * (0.55f + (i % 3) * 0.24f)
                            val pos = Offset(
                                x = burst.origin.x + cos(angle) * dist,
                                y = burst.origin.y + sin(angle) * dist - (44.dp.toPx() * t)
                            )
                            val particleColor = if (i % 2 == 0) RoseVelvet else ChampagneGold
                            drawHeartShape(
                                center = pos,
                                size = (10f + (i % 3) * 4f).dp.toPx() * (1f - t * 0.3f),
                                color = particleColor.copy(alpha = fade)
                            )
                        }
                        drawHeartShape(
                            center = burst.origin.copy(y = burst.origin.y - 38.dp.toPx() * t),
                            size = (26.dp.toPx() + 26.dp.toPx() * t),
                            color = RoseVelvet.copy(alpha = fade * 0.92f)
                        )
                    } else {
                        val ringRadius = 42.dp.toPx() * t
                        drawCircle(
                            color = ChampagneGold.copy(alpha = fade * 0.45f),
                            radius = ringRadius,
                            center = burst.origin,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        for (i in 0 until 6) {
                            val angle = (i / 6f) * (2f * PI).toFloat()
                            val pos = Offset(
                                x = burst.origin.x + cos(angle) * ringRadius * 1.15f,
                                y = burst.origin.y + sin(angle) * ringRadius * 1.15f - 14.dp.toPx() * t
                            )
                            drawCircle(
                                color = WarmGoldGlow.copy(alpha = fade * 0.85f),
                                radius = 2.4.dp.toPx() * (1f - t * 0.4f),
                                center = pos
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeartBloomCenterpiece(
    size: Dp = 236.dp,
    contentDescriptionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_bloom_hero")

    // Real subtle lub-dub heartbeat curve
    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1900
                1.00f at 0 using FastOutSlowInEasing
                1.08f at 220 using FastOutSlowInEasing
                0.99f at 420 using FastOutSlowInEasing
                1.05f at 620 using FastOutSlowInEasing
                1.00f at 980 using FastOutSlowInEasing
                1.00f at 1900
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "heartbeat_scale"
    )

    val petalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petal_rotation"
    )

    val rippleWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_wave"
    )

    val tapBloom = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        tapBloom.snapTo(0f)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .semantics { this.contentDescription = contentDescriptionText }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = ChampagneGold),
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val minDim = this.size.minDimension

            // Soft outer aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CrimsonBloom.copy(alpha = 0.34f),
                        RoseVelvet.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = minDim * 0.50f
                ),
                radius = minDim * 0.50f,
                center = center
            )

            // Concentric blooming pulse rings
            val wave1Radius = minDim * (0.28f + 0.21f * rippleWave)
            val wave1Alpha = (1f - rippleWave).coerceIn(0f, 1f) * 0.45f
            drawCircle(
                color = ChampagneGold.copy(alpha = wave1Alpha),
                radius = wave1Radius,
                center = center,
                style = Stroke(width = 1.6.dp.toPx())
            )

            val wave2Progress = (rippleWave + 0.5f) % 1f
            val wave2Radius = minDim * (0.28f + 0.21f * wave2Progress)
            val wave2Alpha = (1f - wave2Progress).coerceIn(0f, 1f) * 0.32f
            drawCircle(
                color = SoftBlush.copy(alpha = wave2Alpha),
                radius = wave2Radius,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Rotating blooming lotus/rose petals behind the crystal heart
            val petalOrbitRadius = minDim * 0.25f * heartbeatScale
            val petalCount = 8
            rotate(degrees = petalRotation, pivot = center) {
                for (i in 0 until petalCount) {
                    val angleDeg = i * (360f / petalCount)
                    rotate(degrees = angleDeg, pivot = center) {
                        val petalPath = Path().apply {
                            val topY = center.y - petalOrbitRadius - minDim * 0.12f
                            val baseY = center.y - minDim * 0.04f
                            val width = minDim * 0.085f
                            moveTo(center.x, baseY)
                            cubicTo(
                                center.x - width, baseY - minDim * 0.08f,
                                center.x - width * 0.72f, topY + minDim * 0.04f,
                                center.x, topY
                            )
                            cubicTo(
                                center.x + width * 0.72f, topY + minDim * 0.04f,
                                center.x + width, baseY - minDim * 0.08f,
                                center.x, baseY
                            )
                            close()
                        }
                        drawPath(
                            path = petalPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ChampagneGold.copy(alpha = 0.42f),
                                    RoseVelvet.copy(alpha = 0.24f),
                                    Color.Transparent
                                ),
                                startY = center.y - petalOrbitRadius - minDim * 0.12f,
                                endY = center.y
                            )
                        )
                        drawPath(
                            path = petalPath,
                            color = ChampagneGold.copy(alpha = 0.36f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }

            // Orbiting stardust gems
            for (i in 0 until 4) {
                val rad = Math.toRadians((petalRotation * 1.6f + i * 90f).toDouble())
                val orbitR = minDim * 0.37f
                val starCenter = Offset(
                    x = center.x + (cos(rad) * orbitR).toFloat(),
                    y = center.y + (sin(rad) * orbitR).toFloat()
                )
                drawCircle(
                    color = WarmGoldGlow.copy(alpha = 0.85f),
                    radius = if (i % 2 == 0) 3.dp.toPx() else 2.2.dp.toPx(),
                    center = starCenter
                )
            }

            // Main layered Heart with crimson-to-gold luminescence
            val heartSize = minDim * 0.34f * heartbeatScale
            val heartPath = buildHeartPath(center, heartSize)

            // Back velvet glow under heart
            drawPath(
                path = buildHeartPath(center, heartSize * 1.12f),
                brush = Brush.radialGradient(
                    colors = listOf(
                        RoseVelvet.copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = heartSize * 1.4f
                )
            )

            // Filled velvet crimson-gold heart
            drawPath(
                path = heartPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SoftBlush,
                        RoseVelvet,
                        CrimsonBloom,
                        Color(0xFF9F1239)
                    ),
                    start = Offset(center.x - heartSize, center.y - heartSize),
                    end = Offset(center.x + heartSize, center.y + heartSize)
                )
            )

            // Champagne gold bevel outline
            drawPath(
                path = heartPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        WarmGoldGlow,
                        ChampagneGold,
                        SoftBlush.copy(alpha = 0.7f)
                    )
                ),
                style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Inner specular silk highlight
            val innerPath = buildHeartPath(
                center = center.copy(y = center.y - heartSize * 0.04f),
                halfSize = heartSize * 0.56f
            )
            drawPath(
                path = innerPath,
                color = Color.White.copy(alpha = 0.14f),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}

fun DrawScope.drawHeartShape(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = buildHeartPath(center = center, halfSize = size * 0.5f)
    drawPath(path = path, color = color)
}

fun buildHeartPath(center: Offset, halfSize: Float): Path {
    val w = halfSize
    val h = halfSize
    return Path().apply {
        val topDipY = center.y - h * 0.25f
        val bottomTipY = center.y + h * 0.95f
        moveTo(center.x, topDipY)
        cubicTo(
            center.x - w * 0.55f, center.y - h * 1.05f,
            center.x - w * 1.35f, center.y - h * 0.15f,
            center.x, bottomTipY
        )
        cubicTo(
            center.x + w * 1.35f, center.y - h * 0.15f,
            center.x + w * 0.55f, center.y - h * 1.05f,
            center.x, topDipY
        )
        close()
    }
}
