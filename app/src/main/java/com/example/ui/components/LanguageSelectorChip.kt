package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceLanguage
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun LanguageSelectorRow(
    selectedLanguage: VoiceLanguage,
    onLanguageSelected: (VoiceLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VoiceLanguage.entries.forEach { lang ->
            val isSelected = lang == selectedLanguage
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) EmeraldGreen.copy(alpha = 0.2f) else ObsidianCard)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) EmeraldGreen else ObsidianCardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onLanguageSelected(lang) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("lang_chip_${lang.localeTag}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = lang.flag, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lang.nativeName,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) EmeraldGreen else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${lang.displayName})",
                        fontSize = 11.sp,
                        color = if (isSelected) EmeraldGreen.copy(alpha = 0.8f) else TextMuted
                    )
                }
            }
        }
    }
}
