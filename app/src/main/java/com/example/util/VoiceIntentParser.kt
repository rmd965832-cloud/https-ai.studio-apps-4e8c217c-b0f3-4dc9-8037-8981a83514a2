package com.example.util

import com.example.model.IntentAction
import com.example.model.VoiceIntent

object VoiceIntentParser {

    private val playKeywords = listOf(
        // Assamese
        "প্লে কৰা", "প্লে কর", "বজোৱা", "শুনা", "গান",
        // Bengali
        "প্লে করো", "প্লে কর", "বাজাও", "শোনাও",
        // Hindi
        "बजाओ", "चलाओ", "सुनाओ", "गाना",
        // English
        "play", "listen to", "stream"
    )

    private val whatsappKeywords = listOf(
        // Assamese
        "whatsapp", "ৱাটছএপ", "হোৱাটছএপ", "পঠাই দিয়া", "পঠোৱা", "শ্বেয়াৰ কৰা", "শ্বেয়াৰ",
        // Bengali
        "হোয়াটসঅ্যাপ", "পাঠাও", "পাঠিয়ে দাও", "শেয়ার",
        // Hindi
        "व्हाट्सएप", "भेजो", "शेयर करो", "सेंड करो",
        // English
        "share on whatsapp", "send to whatsapp", "whatsapp this", "share"
    )

    /**
     * Parses the spoken text to detect intent (Play, Send WhatsApp, or Search Only)
     * and extracts the song query by stripping out action trigger phrases.
     */
    fun parse(text: String): VoiceIntent {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return VoiceIntent(IntentAction.SEARCH_ONLY, "", text)
        }

        val lower = trimmed.lowercase()

        val isWhatsapp = whatsappKeywords.any { kw -> lower.contains(kw.lowercase()) }
        val isPlay = playKeywords.any { kw -> lower.contains(kw.lowercase()) }

        // Strip action keywords to isolate the song title/artist query
        var query = trimmed
        val allKeywords = (whatsappKeywords + playKeywords).sortedByDescending { it.length }
        for (kw in allKeywords) {
            val regex = Regex("(?i)\\b" + Regex.escape(kw) + "\\b|" + Regex.escape(kw))
            query = query.replace(regex, "")
        }

        // Clean up punctuation, extraneous spaces, and leading/trailing quotes
        query = query.replace(Regex("[\"\'!?,.-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Fallback to original text if stripping removed everything
        if (query.isEmpty()) {
            query = trimmed
        }

        val action = when {
            isWhatsapp -> IntentAction.SEND_WHATSAPP
            isPlay -> IntentAction.PLAY_SONG
            else -> IntentAction.SEARCH_ONLY
        }

        return VoiceIntent(action = action, query = query, rawText = trimmed)
    }
}
