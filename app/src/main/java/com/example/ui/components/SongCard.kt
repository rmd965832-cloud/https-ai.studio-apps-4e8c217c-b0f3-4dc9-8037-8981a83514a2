package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.SongItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun SongCard(
    song: SongItem,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("song_item_${song.videoId}")
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ObsidianCard
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail with play overlay
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 75.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    AsyncImage(
                        model = song.thumbnailUrl,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Subtle play icon badge on thumbnail
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Channel metadata
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = song.channelTitle.ifBlank { "YouTube Music" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Favorite toggle
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("fav_btn_${song.videoId}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color(0xFFF43F5E) else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row (Play vs WhatsApp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play Button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(EmeraldGreen, Color(0xFF059669))
                            )
                        )
                        .clickable { onPlayClick() }
                        .testTag("play_button_${song.videoId}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Play Song",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // WhatsApp Share Button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WhatsAppGreen)
                        .clickable { onWhatsAppClick() }
                        .testTag("whatsapp_button_${song.videoId}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
