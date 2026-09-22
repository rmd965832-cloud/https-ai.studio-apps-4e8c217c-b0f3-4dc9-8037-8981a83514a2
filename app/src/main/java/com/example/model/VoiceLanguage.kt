package com.example.model

enum class VoiceLanguage(
    val localeTag: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val placeholderPrompt: String,
    val listeningText: String,
    val micPromptText: String,
    val sampleQueries: List<String>
) {
    ASSAMESE(
        localeTag = "as-IN",
        displayName = "Assamese",
        nativeName = "অসমীয়া",
        flag = "🇮🇳",
        placeholderPrompt = "গানৰ নাম কওক বা লিখক...",
        listeningText = "🎙️ শুনি আছোঁ...",
        micPromptText = "🎤 কথা কওক",
        sampleQueries = listOf(
            "জুবিন গাৰ্গৰ মায়াবিনী প্লে কৰা",
            "ভূপেন হাজৰিকাৰ গান বজোৱা",
            "বিহু গান WhatsApp-ত পঠাই দিয়া",
            "মায়াবিনী ৰাতিত জোনাক"
        )
    ),
    BENGALI(
        localeTag = "bn-IN",
        displayName = "Bengali",
        nativeName = "বাংলা",
        flag = "🇮🇳",
        placeholderPrompt = "গানের নাম বলুন বা লিখুন...",
        listeningText = "🎙️ শুনছি...",
        micPromptText = "🎤 কথা বলুন",
        sampleQueries = listOf(
            "অরিজিৎ সিং এর গান বাজাও",
            "হেমন্ত মুখোপাধ্যায়ের গান",
            "রবীন্দ্র সংগীত প্লে করো",
            "হোয়াটসঅ্যাপে গান পাঠাও"
        )
    ),
    HINDI(
        localeTag = "hi-IN",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        flag = "🇮🇳",
        placeholderPrompt = "गाने का नाम बोलें या लिखें...",
        listeningText = "🎙️ सुन रहे हैं...",
        micPromptText = "🎤 बोलिए",
        sampleQueries = listOf(
            "अरिजीत सिंह के गाने बजाओ",
            "किशोर कुमार के पुराने गाने",
            "लेटेस्ट बॉलीवुड गाने प्ले करो",
            "व्हाट्सएप पर शेयर करो"
        )
    ),
    ENGLISH(
        localeTag = "en-IN",
        displayName = "English",
        nativeName = "English",
        flag = "🌐",
        placeholderPrompt = "Speak or type song name...",
        listeningText = "🎙️ Listening...",
        micPromptText = "🎤 Tap to speak",
        sampleQueries = listOf(
            "Play Shape of You",
            "Top acoustic songs",
            "Coldplay music",
            "Share to WhatsApp"
        )
    );

    companion object {
        fun fromLocale(code: String): VoiceLanguage {
            return entries.firstOrNull { it.localeTag.equals(code, ignoreCase = true) } ?: ASSAMESE
        }
    }
}
