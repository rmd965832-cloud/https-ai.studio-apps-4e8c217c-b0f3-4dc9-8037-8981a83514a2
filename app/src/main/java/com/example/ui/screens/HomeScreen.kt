package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.IntentAction
import com.example.ui.VoiceAssistantViewModel
import com.example.ui.components.LanguageSelectorRow
import com.example.ui.components.NovaControlGrid
import com.example.ui.components.NovaOrbCore
import com.example.ui.components.NovaStatusDisplay
import com.example.ui.components.NovaTopBar
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaCyanGlow
import com.example.ui.theme.NovaDeepSpace
import com.example.ui.theme.NovaMintLight
import com.example.ui.theme.NovaMutedTeal
import com.example.ui.theme.NovaNeonTeal
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun HomeScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigateToSearch: (shareMode: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isListening by viewModel.isListening.collectAsState()
    val transcript by viewModel.transcript.collectAsState()
    val rmsLevel by viewModel.rmsLevel.collectAsState()
    val voiceError by viewModel.voiceError.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val detectedIntent by viewModel.detectedIntent.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsState()
    val lastSpokenText by viewModel.lastSpokenText.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    // Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            Toast.makeText(context, "Microphone permission required for voice interaction", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleMicClick() {
        if (isListening) {
            viewModel.stopListening()
        } else {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPerm) {
                viewModel.startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1F1A),
                        NovaDeepSpace,
                        Color(0xFF020403)
                    )
                )
            )
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nova Top Bar matching HTML
        item {
            NovaTopBar(
                onSettingsClick = { showSettingsDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Language Selector Row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "VOICE INPUT LOCALE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NovaMutedTeal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LanguageSelectorRow(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Center Cybernetic Glowing Nova Orb Visualizer
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                NovaOrbCore(
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    rmsLevel = rmsLevel,
                    onClick = { handleMicClick() },
                    modifier = Modifier.testTag("mic_button")
                ) {
                    // Center Icon / Status inside the glowing Orb
                    val orbIconBg = when {
                        isListening -> CrimsonAlert.copy(alpha = 0.85f)
                        isSpeaking -> NovaCyanGlow.copy(alpha = 0.85f)
                        else -> Color.Black.copy(alpha = 0.25f)
                    }
                    val orbIcon = when {
                        isSpeaking -> Icons.Default.VolumeUp
                        else -> Icons.Default.Mic
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(orbIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = orbIcon,
                            contentDescription = if (isListening) "Listening" else if (isSpeaking) "Speaking" else "Speak",
                            tint = if (isListening || isSpeaking) Color.White else Color(0xFF03261D),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Status Display (Headline + Spoken Response + Live Transcription)
        item {
            NovaStatusDisplay(
                isListening = isListening,
                isSpeaking = isSpeaking,
                lastSpokenText = lastSpokenText,
                onStopSpeaking = { viewModel.stopSpeaking() },
                transcript = transcript,
                detectedIntent = detectedIntent,
                voiceError = voiceError
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Speech Voice Feedback Controls (Take to Speech Voice)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Speech Toggle Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x14FFFFFF))
                        .border(
                            1.dp,
                            if (isTtsEnabled) NovaNeonTeal.copy(alpha = 0.4f) else NovaBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.setTtsEnabled(!isTtsEnabled) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("tts_toggle_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = if (isTtsEnabled) NovaNeonTeal else NovaMutedTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTtsEnabled) "Speech Voice: ON" else "Voice: MUTED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTtsEnabled) NovaNeonTeal else NovaMutedTeal
                        )
                    }
                }

                // Test Voice Speak Greeting Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1F00A3FF))
                        .border(1.dp, NovaCyanGlow.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.speakGreeting() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("tts_greeting_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = NovaCyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🗣️ Speak / মাতক",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NovaMintLight
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Nova 4-Control Grid Cards from HTML template
        item {
            NovaControlGrid(
                onMusicClick = { onNavigateToSearch(false) },
                onWhatsAppClick = { onNavigateToSearch(true) },
                onPromptsClick = {
                    // Quick test with first sample query
                    selectedLanguage.sampleQueries.firstOrNull()?.let { query ->
                        viewModel.handleVoiceTranscript(query)
                    }
                },
                onSystemStatusClick = { showSettingsDialog = true }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Voice Suggestions Pills (Direct 1-tap testing)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK COMMANDS (TAP TO TEST)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NovaMutedTeal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedLanguage.sampleQueries) { sample ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x1AFFFFFF))
                                .border(1.dp, NovaNeonTeal.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.handleVoiceTranscript(sample)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("sample_query_pill")
                        ) {
                            Text(
                                text = "💬 \"$sample\"",
                                fontSize = 12.sp,
                                color = NovaMintLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons for YouTube Music & WhatsApp
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Search Songs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(NovaNeonTeal, Color(0xFF00BFA5))
                            )
                        )
                        .clickable { onNavigateToSearch(false) }
                        .padding(horizontal = 18.dp)
                        .testTag("search_songs_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎵 গান সাৰ্চ কৰক (Search Songs)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Button 2: WhatsApp Share Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(WhatsAppGreen)
                        .clickable { onNavigateToSearch(true) }
                        .padding(horizontal = 18.dp)
                        .testTag("whatsapp_picker_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📤 WhatsApp-ত পঠাব লগীয়া গান বাছক",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Voice Searches
        if (recentSearches.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = NovaNeonTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "পূৰ্বৰ সাৰ্চ (Recent Searches)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextPrimary
                        )
                    }

                    TextButton(onClick = { viewModel.clearAllSearches() }) {
                        Text("Clear All", fontSize = 11.sp, color = NovaMutedTeal)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            items(recentSearches.take(5)) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x14FFFFFF))
                        .border(1.dp, Color(0x1A00FFC8), RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.updateSearchQuery(item.query)
                            viewModel.searchSongs(item.query, shareMode = item.intentAction == IntentAction.SEND_WHATSAPP.name)
                            onNavigateToSearch(item.intentAction == IntentAction.SEND_WHATSAPP.name)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        val icon = if (item.intentAction == IntentAction.SEND_WHATSAPP.name) {
                            Icons.Default.Send
                        } else {
                            Icons.Default.PlayArrow
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (item.intentAction == IntentAction.SEND_WHATSAPP.name) WhatsAppGreen else NovaNeonTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.query,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = item.intentAction,
                        fontSize = 10.sp,
                        color = NovaMutedTeal
                    )
                }
            }
        }
    }

    // Settings Modal
    if (showSettingsDialog) {
        SettingsDialog(
            currentApiKey = viewModel.getCustomApiKey(),
            onSaveApiKey = {
                viewModel.saveCustomApiKey(it)
                showSettingsDialog = false
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}
