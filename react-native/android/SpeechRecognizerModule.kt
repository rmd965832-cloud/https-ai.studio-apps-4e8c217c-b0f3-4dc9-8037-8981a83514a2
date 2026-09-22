package com.example.speech

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class SpeechRecognizerModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isListening = false

    override fun getName(): String = "SpeechRecognizerModule"

    private fun sendEvent(eventName: String, params: WritableMap?) {
        if (reactContext.hasActiveReactInstance()) {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
        }
    }

    private fun ensureRecognizerInitialized() {
        if (speechRecognizer == null) {
            mainHandler.post {
                if (SpeechRecognizer.isRecognitionAvailable(reactContext)) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(reactContext)
                    speechRecognizer?.setRecognitionListener(this)
                }
            }
        }
    }

    @ReactMethod
    fun isAvailable(promise: Promise) {
        val available = SpeechRecognizer.isRecognitionAvailable(reactContext)
        promise.resolve(available)
    }

    @ReactMethod
    fun startListening(locale: String?, promise: Promise) {
        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(reactContext)) {
                    promise.reject("UNAVAILABLE", "Speech recognition is not available on this device")
                    return@post
                }

                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(reactContext)
                    speechRecognizer?.setRecognitionListener(this)
                } else {
                    speechRecognizer?.cancel()
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, reactContext.packageName)
                    if (!locale.isNullOrEmpty()) {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, locale)
                    }
                }

                speechRecognizer?.startListening(intent)
                isListening = true
                promise.resolve(true)
            } catch (e: Exception) {
                promise.reject("START_ERROR", e.localizedMessage, e)
            }
        }
    }

    @ReactMethod
    fun stopListening(promise: Promise) {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                isListening = false
                promise.resolve(true)
            } catch (e: Exception) {
                promise.reject("STOP_ERROR", e.localizedMessage, e)
            }
        }
    }

    @ReactMethod
    fun cancel(promise: Promise) {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                isListening = false
                promise.resolve(true)
            } catch (e: Exception) {
                promise.reject("CANCEL_ERROR", e.localizedMessage, e)
            }
        }
    }

    @ReactMethod
    fun destroy(promise: Promise?) {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
                isListening = false
                promise?.resolve(true)
            } catch (e: Exception) {
                promise?.reject("DESTROY_ERROR", e.localizedMessage, e)
            }
        }
    }

    // Required for React Native event emitters
    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        sendEvent("onSpeechReady", null)
    }

    override fun onBeginningOfSpeech() {
        sendEvent("onSpeechStart", null)
    }

    override fun onRmsChanged(rmsdB: Float) {
        val map = Arguments.createMap()
        map.putDouble("rmsDb", rmsdB.toDouble())
        sendEvent("onSpeechRmsChanged", map)
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        isListening = false
        sendEvent("onSpeechEnd", null)
    }

    override fun onError(error: Int) {
        isListening = false
        val map = Arguments.createMap()
        map.putInt("code", error)
        map.putString("message", getErrorMessage(error))
        sendEvent("onSpeechError", map)
    }

    override fun onResults(results: Bundle?) {
        isListening = false
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val transcript = matches?.firstOrNull() ?: ""

        val map = Arguments.createMap()
        map.putString("transcript", transcript)

        val array = Arguments.createArray()
        matches?.forEach { array.pushString(it) }
        map.putArray("matches", array)

        sendEvent("onSpeechResults", map)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partialText = matches?.firstOrNull() ?: ""

        val map = Arguments.createMap()
        map.putString("partialText", partialText)
        sendEvent("onSpeechPartialResults", map)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient microphone permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network operation timed out"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy"
            SpeechRecognizer.ERROR_SERVER -> "Recognition server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected (timeout)"
            else -> "Speech recognition error ($errorCode)"
        }
    }
}
