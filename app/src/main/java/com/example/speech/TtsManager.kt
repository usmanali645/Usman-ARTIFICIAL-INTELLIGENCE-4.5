package com.example.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeakRequest: (() -> Unit)? = null

    private val _speakingMessageId = MutableStateFlow<String?>(null)
    val speakingMessageId: StateFlow<String?> = _speakingMessageId.asStateFlow()

    private fun ensureInitialized(onReady: () -> Unit) {
        if (isInitialized && tts != null) {
            onReady()
            return
        }

        pendingSpeakRequest = onReady
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        isInitialized = true
                        setupUtteranceListener()
                        pendingSpeakRequest?.invoke()
                        pendingSpeakRequest = null
                    }
                }
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _speakingMessageId.value = utteranceId
            }

            override fun onDone(utteranceId: String?) {
                if (_speakingMessageId.value == utteranceId) {
                    _speakingMessageId.value = null
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (_speakingMessageId.value == utteranceId) {
                    _speakingMessageId.value = null
                }
            }
        })
    }

    fun speak(text: String, messageId: String, speechRate: Float = 1.0f, pitch: Float = 1.0f) {
        if (_speakingMessageId.value == messageId) {
            stop()
            return
        }
        stop()

        ensureInitialized {
            tts?.setSpeechRate(speechRate)
            tts?.setPitch(pitch)

            // Clean markdown formatting characters before speaking for natural voice
            val cleanedText = text
                .replace(Regex("```[a-zA-Z]*"), "")
                .replace(Regex("[*#_`~>]"), "")
                .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
                .trim()

            val params = Bundle()
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, messageId)
            _speakingMessageId.value = messageId
        }
    }

    fun stop() {
        tts?.stop()
        _speakingMessageId.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        pendingSpeakRequest = null
    }
}
