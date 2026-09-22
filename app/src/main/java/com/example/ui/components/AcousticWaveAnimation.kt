package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AcousticWaveAnimation(
    isListening: Boolean,
    rmsLevel: Float = 0f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isListening) {
            val dynamicScale = 1f + (rmsLevel * 0.05f).coerceIn(0f, 0.4f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.minDimension / 2.7f

                // Outer ambient glow ring 2
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * pulse2 * dynamicScale
                    ),
                    radius = baseRadius * pulse2 * dynamicScale,
                    center = center
                )

                // Middle ambient glow ring 1
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmeraldGreen.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * pulse1 * dynamicScale
                    ),
                    radius = baseRadius * pulse1 * dynamicScale,
                    center = center
                )

                // Animated frequency particles along the perimeter
                val particleCount = 12
                for (i in 0 until particleCount) {
                    val angle = Math.toRadians((i * (360.0 / particleCount) + rotation))
                    val pRadius = baseRadius * 1.15f + (sin(angle * 2 + rotation).toFloat() * 6f)
                    val px = center.x + (cos(angle) * pRadius).toFloat()
                    val py = center.y + (sin(angle) * pRadius).toFloat()
                    drawCircle(
                        color = EmeraldGreen.copy(alpha = 0.6f),
                        radius = 3.5f,
                        center = Offset(px, py)
                    )
                }
            }
        }
        content()
    }
}
