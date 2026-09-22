package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NovaCyanGlow
import com.example.ui.theme.NovaDeepTeal
import com.example.ui.theme.NovaMintLight
import com.example.ui.theme.NovaNeonTeal
import kotlin.math.cos
import kotlin.math.sin

/**
 * Nova Orb Core Visualizer:
 * Faithfully reproduces the HTML/CSS glowing energy orb with animated concentric rings,
 * radial gradients (#baffef, #00ffc8, #007a5e), rotating highlight glow, dashed cosmic ring,
 * and audio reactivity when listening.
 */
@Composable
fun NovaOrbCore(
    isListening: Boolean,
    isSpeaking: Boolean = false,
    rmsLevel: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nova_core_anim")

    // Speech wave pulse when assistant is talking back
    val speakPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speak_pulse"
    )

    // Gentle 3s breathing pulse (scale 1.0 to 1.06)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Rotating highlight glow over 6s
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_angle"
    )

    // Outer ring dashed rotation
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_phase"
    )

    // Overall dynamic scale influenced by voice rms volume when listening or TTS pulse when speaking
    val dynamicAudioScale = when {
        isSpeaking -> speakPulse
        isListening -> 1f + (rmsLevel * 0.04f).coerceIn(0f, 0.35f)
        else -> 1f
    }

    val finalOrbScale = pulseScale * dynamicAudioScale

    Box(
        modifier = modifier.size(310.dp),
        contentAlignment = Alignment.Center
    ) {
        // Concentric Rings & Atmosphere Canvas
        Canvas(modifier = Modifier.size(310.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer Dashed Cosmic Ring 2 (320px scaled ~ 145dp radius)
            val ring2Radius = 145.dp.toPx()
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 20f), dashPhase)
            drawCircle(
                color = NovaNeonTeal.copy(alpha = 0.32f),
                radius = ring2Radius,
                center = center,
                style = Stroke(width = 1.5f, pathEffect = dashEffect)
            )

            // Inner Fine Ring 1 (260px scaled ~ 118dp radius)
            val ring1Radius = 118.dp.toPx()
            drawCircle(
                color = NovaNeonTeal.copy(alpha = 0.28f),
                radius = ring1Radius,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // Outer Atmospheric Corona Blur Gradients
            val coronaRadius = 135.dp.toPx() * finalOrbScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NovaNeonTeal.copy(alpha = if (isListening) 0.45f else 0.25f),
                        NovaCyanGlow.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coronaRadius
                ),
                radius = coronaRadius,
                center = center
            )

            // Inner Intense Radial Glow
            val glowRadius = 100.dp.toPx() * finalOrbScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NovaNeonTeal.copy(alpha = if (isListening) 0.65f else 0.40f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = center
            )

            // Orbiting cyber particles when listening or active
            val particleCount = if (isListening) 16 else 8
            for (i in 0 until particleCount) {
                val rad = Math.toRadians((i * (360.0 / particleCount) + dashPhase))
                val dist = ring1Radius + (sin(rad * 3 + dashPhase).toFloat() * 8f)
                val px = center.x + (cos(rad) * dist).toFloat()
                val py = center.y + (sin(rad) * dist).toFloat()
                drawCircle(
                    color = if (isListening) NovaNeonTeal else NovaMintLight.copy(alpha = 0.7f),
                    radius = if (isListening) 3.5f else 2.5f,
                    center = Offset(px, py)
                )
            }
        }

        // Central Orb (190px ~ 170dp)
        Box(
            modifier = Modifier
                .size((170 * finalOrbScale).dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = NovaMintLight)
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val orbCenter = Offset(size.width / 2f, size.height / 2f)
                val orbRadius = size.minDimension / 2f

                // Radial gradient replicating CSS:
                // radial-gradient(circle at 40% 35%, #baffef, #00ffc8 40%, #007a5e 75%, transparent 78%)
                val lightCenter = Offset(size.width * 0.40f, size.height * 0.35f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to NovaMintLight,
                            0.40f to NovaNeonTeal,
                            0.75f to NovaDeepTeal,
                            0.95f to Color(0xFF03382B),
                            1.0f to Color.Transparent
                        ),
                        center = lightCenter,
                        radius = orbRadius * 1.15f
                    ),
                    radius = orbRadius,
                    center = orbCenter
                )

                // Rotating Highlight overlay:
                // radial-gradient(circle, rgba(255,255,255,0.85) 0%, transparent 60%)
                val radAngle = Math.toRadians(rotateAngle.toDouble())
                val highlightOffset = Offset(
                    orbCenter.x + (cos(radAngle) * orbRadius * 0.25f).toFloat(),
                    orbCenter.y + (sin(radAngle) * orbRadius * 0.25f).toFloat()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = highlightOffset,
                        radius = orbRadius * 0.65f
                    ),
                    radius = orbRadius * 0.65f,
                    center = highlightOffset
                )
            }

            // Central optional icon or microphone indicator
            content?.invoke()
        }
    }
}
