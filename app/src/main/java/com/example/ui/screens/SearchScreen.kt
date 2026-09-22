package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SongItem
import com.example.ui.VoiceAssistantViewModel
import com.example.ui.components.SongCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: VoiceAssistantViewModel,
    initialShareMode: Boolean,
    onNavigateBack: () -> Unit,
    onPlaySong: (SongItem) -> Unit,
    onShareSong: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isShareMode by viewModel.isShareMode.collectAsState()
    val isFromLiveApi by viewModel.isFromLiveApi.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val isListening by viewModel.isListening.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(initialShareMode) {
        viewModel.setShareMode(initialShareMode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 16.dp)
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isShareMode) "WhatsApp Share বাছক" else "গান সাৰ্চ কৰক",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = if (isShareMode) "যিকোনো গান নিৰ্বাচন কৰক WhatsApp-ত পঠাবলৈ" else "গান বা শিল্পীৰ নাম বিচাৰক",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isShareMode) WhatsAppGreen else TextMuted
                )
            }

            // Mode indicator tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isShareMode) WhatsAppGreen.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f))
                    .border(
                        1.dp,
                        if (isShareMode) WhatsAppGreen else CyanAccent,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isShareMode) "📤 Share" else "🎵 Play",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isShareMode) WhatsAppGreen else CyanAccent
                )
            }
        }

        // Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("গানৰ নাম লিখক বা কথা কওক...", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_field"),
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EmeraldGreen)
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                    IconButton(
                        onClick = {
                            if (isListening) viewModel.stopListening() else viewModel.startListening()
                        },
                        modifier = Modifier.testTag("search_voice_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) Color(0xFFEF4444) else EmeraldGreen
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                viewModel.searchSongs(query)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldGreen,
                unfocusedBorderColor = ObsidianCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = EmeraldGreen,
                focusedContainerColor = ObsidianCard,
                unfocusedContainerColor = ObsidianCard
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Switching Pills (Play Mode vs WhatsApp Share Mode)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play Mode Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isShareMode) CyanAccent.copy(alpha = 0.2f) else ObsidianCard)
                    .border(
                        1.dp,
                        if (!isShareMode) CyanAccent else ObsidianCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.setShareMode(false) }
                    .testTag("mode_play_pill"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (!isShareMode) CyanAccent else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "গান বজাওক (Play)",
                        fontSize = 12.sp,
                        fontWeight = if (!isShareMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isShareMode) CyanAccent else TextMuted
                    )
                }
            }

            // WhatsApp Share Mode Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isShareMode) WhatsAppGreen.copy(alpha = 0.2f) else ObsidianCard)
                    .border(
                        1.dp,
                        if (isShareMode) WhatsAppGreen else ObsidianCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.setShareMode(true) }
                    .testTag("mode_whatsapp_pill"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = if (isShareMode) WhatsAppGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp-ত পঠাওক",
                        fontSize = 12.sp,
                        fontWeight = if (isShareMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (isShareMode) WhatsAppGreen else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // API Status Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${results.size} টা গান পোৱা গ'ল",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isFromLiveApi) EmeraldGreen else CyanAccent)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isFromLiveApi) "Live YouTube API" else "Curated Showcase",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Loading or List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = EmeraldGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "গান বিচাৰি আছোঁ...", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(results, key = { it.videoId }) { song ->
                    val isFav = favoriteSongs.any { it.videoId == song.videoId }
                    SongCard(
                        song = song,
                        isFavorite = isFav,
                        onPlayClick = { onPlaySong(song) },
                        onWhatsAppClick = { onShareSong(song) },
                        onToggleFavorite = { viewModel.toggleFavorite(song) }
                    )
                }

                if (results.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "কোনো গান পোৱা নগ'ল",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "অন্য গানৰ নাম লিখি সাৰ্চ কৰক বা কথা কওক",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
