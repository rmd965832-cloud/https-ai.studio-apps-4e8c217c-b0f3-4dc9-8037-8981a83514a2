package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.IntentAction
import com.example.model.VoiceIntent
import com.example.model.VoiceLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

/**
 * Text-to-Speech (TTS) Manager for Nova AI.
 * Handles multilingual spoken responses in Assamese, Bengali, Hindi, and English.
 * Provides active speaking state for UI animations and voice feedback.
 */
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isTtsEnabled = MutableStateFlow(true)
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _speechPitch = MutableStateFlow(1.05f)
    val speechPitch: StateFlow<Float> = _speechPitch.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    init {
        initializeTts()
    }

    private fun initializeTts() {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            isInitialized = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            tts?.setSpeechRate(_speechRate.value)
            tts?.setPitch(_speechPitch.value)
        } else {
            isInitialized = false
        }
    }

    fun setTtsEnabled(enabled: Boolean) {
        _isTtsEnabled.value = enabled
        if (!enabled) {
            stop()
        }
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(_speechRate.value)
    }

    fun setSpeechPitch(pitch: Float) {
        _speechPitch.value = pitch.coerceIn(0.5f, 2.0f)
        tts?.setPitch(_speechPitch.value)
    }

    fun speak(text: String, language: VoiceLanguage = VoiceLanguage.ENGLISH) {
        if (!_isTtsEnabled.value || text.isBlank()) return
        if (!isInitialized || tts == null) {
            initializeTts()
            return
        }

        val targetLocale = when (language) {
            VoiceLanguage.ASSAMESE -> Locale("as", "IN")
            VoiceLanguage.BENGALI -> Locale("bn", "IN")
            VoiceLanguage.HINDI -> Locale("hi", "IN")
            VoiceLanguage.ENGLISH -> Locale.US
        }

        // Check if language is available, fallback to Indian English or US English
        val langResult = tts?.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("en", "IN"))
        }

        tts?.setSpeechRate(_speechRate.value)
        tts?.setPitch(_speechPitch.value)

        _lastSpokenText.value = text
        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    /**
     * Speaks the conversational response based on recognized intent & language
     */
    fun speakIntentResponse(intent: VoiceIntent, language: VoiceLanguage) {
        val message = when (intent.action) {
            IntentAction.PLAY_SONG -> {
                when (language) {
                    VoiceLanguage.ASSAMESE -> "চলোৱা হৈছে: ${intent.query}"
                    VoiceLanguage.BENGALI -> "চালানো হচ্ছে: ${intent.query}"
                    VoiceLanguage.HINDI -> "बजाया जा रहा है: ${intent.query}"
                    VoiceLanguage.ENGLISH -> "Playing ${intent.query}"
                }
            }
            IntentAction.SEND_WHATSAPP -> {
                when (language) {
                    VoiceLanguage.ASSAMESE -> "হোৱাটছএপত শ্বেয়াৰ কৰিবলৈ সাজু: ${intent.query}"
                    VoiceLanguage.BENGALI -> "হোয়াটসঅ্যাপে শেয়ার করতে গানটি বেছে নিন"
                    VoiceLanguage.HINDI -> "व्हाट्सएप पर शेयर करने के लिए गाना चुनिए"
                    VoiceLanguage.ENGLISH -> "Selecting ${intent.query} to share on WhatsApp"
                }
            }
            IntentAction.SEARCH_ONLY -> {
                when (language) {
                    VoiceLanguage.ASSAMESE -> "সাৰ্চ কৰা হৈছে: ${intent.query}"
                    VoiceLanguage.BENGALI -> "অনুসন্ধান করা হচ্ছে: ${intent.query}"
                    VoiceLanguage.HINDI -> "खोजा जा रहा है: ${intent.query}"
                    VoiceLanguage.ENGLISH -> "Searching for ${intent.query}"
                }
            }
        }
        speak(message, language)
    }

    fun speakGreeting(language: VoiceLanguage) {
        val greeting = when (language) {
            VoiceLanguage.ASSAMESE -> "নমস্কাৰ! মই নোভা। কি গান শুনিব?"
            VoiceLanguage.BENGALI -> "নমস্কার! আমি নোভা। কি গান শুনতে চান?"
            VoiceLanguage.HINDI -> "नमस्ते! मैं नोवा हूँ। आप क्या सुनना चाहेंगे?"
            VoiceLanguage.ENGLISH -> "Hello! I am Nova, your voice assistant. What music would you like to hear?"
        }
        speak(greeting, language)
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        } finally {
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {
        } finally {
            tts = null
            isInitialized = false
        }
    }
}
