package com.example.model

enum class IntentAction {
    PLAY_SONG,
    SEND_WHATSAPP,
    SEARCH_ONLY
}

data class VoiceIntent(
    val action: IntentAction,
    val query: String,
    val rawText: String = ""
)
