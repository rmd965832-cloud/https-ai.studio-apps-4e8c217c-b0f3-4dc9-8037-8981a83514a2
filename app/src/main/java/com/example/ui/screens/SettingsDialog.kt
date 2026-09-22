package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "YouTube API ছেটিংছ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "YouTube Data API v3 Key (ঐচ্ছিক / Optional):",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "API Key নাথাকিলেও এপটোৱে সম্পূৰ্ণভাৱে গান বিচৰা আৰু প্লে কৰাৰ সুবিধা দিয়ে। নিজৰ Key দিলে বিশ্বৰ যিকোনো YouTube গান সাৰ্চ কৰিব পাৰিব।",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    placeholder = { Text("AIzaSy...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_text_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = EmeraldGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianCardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "ℹ️ কেনেকৈ Key পাব:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Text(
                            text = "Google Cloud Console-ত যাওক > 'YouTube Data API v3' Enable কৰক > Credentials-ত API Key বনাব।",
                            fontSize = 10.sp,
                            color = TextMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveApiKey(apiKeyInput.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.testTag("save_api_key_btn")
            ) {
                Text("সংৰক্ষণ কৰক (Save)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("বাতিল (Cancel)")
            }
        }
    )
}
