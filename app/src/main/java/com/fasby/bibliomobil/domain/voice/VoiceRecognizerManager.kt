package com.fasby.bibliomobil.domain.voice

import kotlinx.coroutines.flow.StateFlow

sealed interface VoiceRecognizerState {
    data object Idle : VoiceRecognizerState
    data object Listening : VoiceRecognizerState
    data class Success(val text: String) : VoiceRecognizerState
    data class Error(val message: String) : VoiceRecognizerState
}

interface VoiceRecognizerManager {
    val state: StateFlow<VoiceRecognizerState>
    fun startListening()
    fun stopListening()
    fun destroy()
}
