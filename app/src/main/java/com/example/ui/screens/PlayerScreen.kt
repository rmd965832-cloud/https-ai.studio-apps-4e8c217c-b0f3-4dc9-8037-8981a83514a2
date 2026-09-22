package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.model.SongItem
import com.example.ui.VoiceAssistantViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlayerScreen(
    song: SongItem,
    viewModel: VoiceAssistantViewModel,
    onNavigateBack: () -> Unit,
    onShareToWhatsApp: (SongItem) -> Unit,
    onPlaySong: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val allResults by viewModel.searchResults.collectAsState()
    val isFav = favoriteSongs.any { it.videoId == song.videoId }

    val upNextSongs = remember(song, allResults) {
        allResults.filter { it.videoId != song.videoId }.take(8)
    }

    // Embed HTML to play YouTube video cleanly in full width
    val embedHtml = remember(song.videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    background-color: #000;
                    overflow: hidden;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                }
                iframe {
                    width: 100%;
                    height: 100%;
                    border: 0;
                }
            </style>
        </head>
        <body>
            <iframe 
                src="https://www.youtube.com/embed/${song.videoId}?autoplay=1&playsinline=1&rel=0&modestbranding=1" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
        """.trimIndent()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("player_back_btn")
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
                    text = "গান প্লে হৈ আছে (Now Playing)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = song.channelTitle.ifBlank { "YouTube Music" },
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { viewModel.toggleFavorite(song) },
                modifier = Modifier.testTag("player_fav_btn")
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFav) Color(0xFFF43F5E) else TextMuted
                )
            }
        }

        // In-App YouTube WebView Player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
                .testTag("youtube_webview_player")
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.loadsImagesAutomatically = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()

                        loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Track Information & Action Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = song.channelTitle.ifBlank { "YouTube Music" },
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (WhatsApp Share + Open in YouTube App)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WhatsAppGreen)
                        .clickable { onShareToWhatsApp(song) }
                        .padding(horizontal = 12.dp)
                        .testTag("player_whatsapp_share_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp-ত পঠাওক",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Open in External YouTube Button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .clickable {
                            val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse(song.youtubeWatchUrl))
                            context.startActivity(ytIntent)
                        }
                        .padding(horizontal = 12.dp)
                        .testTag("open_external_yt_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YouTube App",
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Up Next / Related Songs
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "পৰৱৰ্তী গান (Up Next):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            items(upNextSongs) { nextSong ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .clickable { onPlaySong(nextSong) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = nextSong.thumbnailUrl,
                            contentDescription = nextSong.title,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nextSong.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = nextSong.channelTitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = { onShareToWhatsApp(nextSong) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = WhatsAppGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
