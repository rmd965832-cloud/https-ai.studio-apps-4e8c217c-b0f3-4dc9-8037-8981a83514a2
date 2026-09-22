package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IntentAction
import com.example.model.VoiceIntent
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaChatGold
import com.example.ui.theme.NovaCyanGlow
import com.example.ui.theme.NovaLightMintText
import com.example.ui.theme.NovaMemoryBlue
import com.example.ui.theme.NovaMintLight
import com.example.ui.theme.NovaMutedTeal
import com.example.ui.theme.NovaNeonTeal
import com.example.ui.theme.NovaSoulSilver
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WhatsAppGreen

/**
 * Nova Top Bar matching HTML:
 * [Profile/Settings]  NOVA (AI ASSISTANT)  [Status Indicator]
 */
@Composable
fun NovaTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Icon: Settings / Config
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x1A00FFC8))
                .border(1.dp, NovaBorder, CircleShape)
                .clickable { onSettingsClick() }
                .testTag("settings_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = NovaNeonTeal,
                modifier = Modifier.size(20.dp)
            )
        }

        // Center Brand
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NOVA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    fontSize = 22.sp
                ),
                color = NovaNeonTeal
            )
            Text(
                text = "PERSONAL AI ASSISTANT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    fontSize = 9.sp
                ),
                color = NovaMutedTeal
            )
        }

        // Right Status Pill (Online Neon Dot)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x1A00FFC8))
                .border(1.dp, NovaNeonTeal.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NovaNeonTeal)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = NovaNeonTeal
                )
            }
        }
    }
}

/**
 * Assistant State Display matching HTML:
 * "Always listening, ready to respond" or "Listening to your voice..."
 * Shows speech voice status when Nova speaks back via Text-to-Speech.
 */
@Composable
fun NovaStatusDisplay(
    isListening: Boolean,
    isSpeaking: Boolean = false,
    lastSpokenText: String = "",
    onStopSpeaking: () -> Unit = {},
    transcript: String,
    detectedIntent: VoiceIntent?,
    voiceError: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status headline
        val statusHeadline = when {
            isListening -> "LISTENING TO VOICE..."
            isSpeaking -> "🔊 NOVA IS SPEAKING..."
            else -> "NOVA NEURAL ENGINE"
        }
        val headlineColor = when {
            isListening -> CrimsonAlert
            isSpeaking -> NovaCyanGlow
            else -> NovaNeonTeal
        }

        Text(
            text = statusHeadline,
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = headlineColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        val subHeadline = when {
            isListening -> "বলক, মই শুনি আছো... (Speak now)"
            isSpeaking -> if (lastSpokenText.isNotBlank()) "\"$lastSpokenText\"" else "Responding in audio voice..."
            else -> "Always listening, ready to respond"
        }

        Text(
            text = subHeadline,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isListening || isSpeaking) NovaLightMintText else NovaMutedTeal,
            textAlign = TextAlign.Center
        )

        // Active Speaking Banner with Stop Button
        if (isSpeaking) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x2B00A3FF))
                    .border(1.dp, NovaCyanGlow.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { onStopSpeaking() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NovaCyanGlow)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Speaking Aloud • Tap to Mute",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NovaMintLight
                )
            }
        }

        // Error message if any
        if (voiceError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CrimsonAlert.copy(alpha = 0.15f))
                    .border(1.dp, CrimsonAlert.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = voiceError,
                    color = CrimsonAlert,
                    fontSize = 12.sp
                )
            }
        }

        // Live Transcript Box if text was spoken
        AnimatedVisibility(
            visible = transcript.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x2B00FFC8))
                    .border(1.dp, NovaNeonTeal.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRANSCRIPTION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = NovaNeonTeal
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        detectedIntent?.let { intent ->
                            val (badgeColor, badgeText) = when (intent.action) {
                                IntentAction.SEND_WHATSAPP -> WhatsAppGreen to "📤 WHATSAPP"
                                IntentAction.PLAY_SONG -> NovaCyanGlow to "🎵 PLAY MUSIC"
                                IntentAction.SEARCH_ONLY -> NovaNeonTeal to "🔍 SEARCH"
                            }
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"$transcript\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * 4 Bottom Interactive Cards matching the HTML Design mockup:
 * 1. 🎵 YouTube Music (Search & stream instantly)
 * 2. 📤 WhatsApp Share (Share song links with 1-tap)
 * 3. 💬 Voice Prompts (Quick command queries)
 * 4. 🧭 Neural Status (Latency, Mode & Core)
 */
@Composable
fun NovaControlGrid(
    onMusicClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onPromptsClick: () -> Unit,
    onSystemStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NovaFeatureCard(
                icon = Icons.Default.MusicNote,
                title = "YouTube Music",
                subtitle = "Search & Play",
                accentColor = NovaNeonTeal,
                modifier = Modifier.weight(1f),
                onClick = onMusicClick,
                testTag = "card_youtube_music"
            )

            NovaFeatureCard(
                icon = Icons.Default.Share,
                title = "WhatsApp",
                subtitle = "Direct Share",
                accentColor = WhatsAppGreen,
                modifier = Modifier.weight(1f),
                onClick = onWhatsAppClick,
                testTag = "card_whatsapp_share"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NovaFeatureCard(
                icon = Icons.Default.ChatBubbleOutline,
                title = "Voice Commands",
                subtitle = "Sample Phrases",
                accentColor = NovaChatGold,
                modifier = Modifier.weight(1f),
                onClick = onPromptsClick,
                testTag = "card_voice_commands"
            )

            NovaFeatureCard(
                icon = Icons.Default.AutoAwesome,
                title = "Assistant Core",
                subtitle = "Active & Ready",
                accentColor = NovaMemoryBlue,
                modifier = Modifier.weight(1f),
                onClick = onSystemStatusClick,
                testTag = "card_assistant_core"
            )
        }
    }
}

@Composable
fun NovaFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x14FFFFFF))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = NovaMutedTeal
                )
            }
        }
    }
}
