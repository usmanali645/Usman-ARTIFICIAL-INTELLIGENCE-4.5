package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.repository.AiModelOption
import com.example.data.repository.AiStudioRepository
import com.example.data.repository.AiTone
import com.example.data.repository.AppSettingsState
import com.example.data.repository.BotRole
import com.example.data.repository.ChatRepository
import com.example.data.repository.SettingsRepository
import com.example.speech.TtsManager
import com.example.ui.components.AppNavTab
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val currentTab: AppNavTab = AppNavTab.HOME,
    val conversations: List<ConversationEntity> = emptyList(),
    val activeConversation: ConversationEntity? = null,
    val messages: List<ChatMessageEntity> = emptyList(),
    val inputQuery: String = "",
    val attachedImageUri: Uri? = null,
    val attachedImageBase64: String? = null,
    val attachedImageMimeType: String? = null,
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val speakingMessageId: String? = null,
    val showNewChatDialog: Boolean = false,
    val showProfileDialog: Boolean = false,
    val showRenameDialog: Boolean = false,
    val showClearAllDialog: Boolean = false,
    val showLiveVoiceScreen: Boolean = false,
    val liveVoiceSpokenText: String = "",
    val liveVoiceAiResponse: String = "",
    val errorMessage: String? = null
)

class ChatViewModel(
    val chatRepository: ChatRepository,
    val settingsRepository: SettingsRepository,
    val aiStudioRepository: AiStudioRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val settings: StateFlow<AppSettingsState> = settingsRepository.settings

    private var messagesJob: Job? = null
    private var streamingJob: Job? = null

    init {
        // Observe conversations from database
        viewModelScope.launch {
            chatRepository.getConversations().collectLatest { convs ->
                _uiState.update { it.copy(conversations = convs) }
                if (_uiState.value.activeConversation == null) {
                    if (convs.isNotEmpty()) {
                        selectConversation(convs.first().id)
                    } else {
                        startNewChat()
                    }
                }
            }
        }

        // Observe TTS speaking state
        viewModelScope.launch {
            ttsManager.speakingMessageId.collectLatest { id ->
                _uiState.update { it.copy(speakingMessageId = id) }
            }
        }
    }

    fun setNavTab(tab: AppNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(inputQuery = newQuery) }
    }

    fun attachImage(uri: Uri, base64: String, mimeType: String) {
        _uiState.update {
            it.copy(
                attachedImageUri = uri,
                attachedImageBase64 = base64,
                attachedImageMimeType = mimeType
            )
        }
    }

    fun removeAttachedImage() {
        _uiState.update {
            it.copy(
                attachedImageUri = null,
                attachedImageBase64 = null,
                attachedImageMimeType = null
            )
        }
    }

    fun startNewChat(
        title: String = "New Conversation",
        model: AiModelOption = settings.value.selectedModel,
        tone: AiTone = settings.value.defaultTone
    ) {
        viewModelScope.launch {
            stopGeneration()
            ttsManager.stop()
            val newConv = chatRepository.createNewConversation(
                title = if (title.isNotBlank()) title else "New Conversation"
            )
            selectConversation(newConv.id)
            _uiState.update { it.copy(showNewChatDialog = false, currentTab = AppNavTab.HOME) }
        }
    }

    fun selectConversation(conversationId: String) {
        viewModelScope.launch {
            ttsManager.stop()
            val conv = _uiState.value.conversations.find { it.id == conversationId }
            _uiState.update { it.copy(activeConversation = conv, currentTab = AppNavTab.HOME) }

            messagesJob?.cancel()
            messagesJob = viewModelScope.launch {
                chatRepository.getMessagesForConversation(conversationId).collectLatest { msgs ->
                    _uiState.update { it.copy(messages = msgs) }
                }
            }
        }
    }

    fun sendMessage(customPrompt: String? = null) {
        val prompt = (customPrompt ?: _uiState.value.inputQuery).trim()
        val hasImage = _uiState.value.attachedImageBase64 != null
        if ((prompt.isBlank() && !hasImage) || _uiState.value.isLoading) return

        val activeConv = _uiState.value.activeConversation
        val imgUri = _uiState.value.attachedImageUri?.toString()
        val imgBase64 = _uiState.value.attachedImageBase64
        val imgMime = _uiState.value.attachedImageMimeType

        // Reset inputs
        _uiState.update {
            it.copy(
                inputQuery = "",
                attachedImageUri = null,
                attachedImageBase64 = null,
                attachedImageMimeType = null
            )
        }

        if (activeConv == null) {
            viewModelScope.launch {
                val newConv = chatRepository.createNewConversation(
                    title = if (prompt.length > 25) prompt.take(25) + "…" else prompt.ifBlank { "Image Analysis" }
                )
                selectConversation(newConv.id)
                performStreamMessage(newConv.id, prompt, imgBase64, imgMime, imgUri)
            }
        } else {
            performStreamMessage(activeConv.id, prompt, imgBase64, imgMime, imgUri)
        }
    }

    private fun performStreamMessage(
        conversationId: String,
        prompt: String,
        imageBase64: String?,
        imageMimeType: String?,
        imageUri: String?
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        ttsManager.stop()

        streamingJob?.cancel()
        streamingJob = viewModelScope.launch {
            val result = chatRepository.streamUserMessage(
                conversationId = conversationId,
                userPrompt = prompt,
                imageBase64 = imageBase64,
                imageMimeType = imageMimeType,
                imageUri = imageUri,
                enableSearchGrounding = settings.value.searchGroundingEnabled,
                enableMapsGrounding = settings.value.mapsGroundingEnabled,
                onChunk = {
                    // Handled reactively through Room Flow
                }
            )

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess { aiMessage ->
                if (settings.value.autoTtsEnabled && !aiMessage.isError && settings.value.voiceEnabled) {
                    ttsManager.speak(
                        text = aiMessage.content,
                        messageId = aiMessage.id,
                        speechRate = settings.value.ttsSpeechRate,
                        pitch = settings.value.ttsPitch
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    fun stopGeneration() {
        streamingJob?.cancel()
        streamingJob = null
        _uiState.update { it.copy(isLoading = false) }
    }

    fun regenerateLastResponse() {
        val msgs = _uiState.value.messages
        if (msgs.isEmpty() || _uiState.value.isLoading) return

        val lastMsg = msgs.lastOrNull()
        val lastUserMsg = msgs.lastOrNull { it.role == "user" } ?: return

        viewModelScope.launch {
            if (lastMsg != null && lastMsg.role == "model") {
                chatRepository.deleteMessage(lastMsg.id)
            }
            performStreamMessage(
                conversationId = lastUserMsg.conversationId,
                prompt = lastUserMsg.content,
                imageBase64 = null,
                imageMimeType = null,
                imageUri = lastUserMsg.imageUri
            )
        }
    }

    fun toggleSpeak(message: ChatMessageEntity) {
        if (_uiState.value.speakingMessageId == message.id) {
            ttsManager.stop()
        } else {
            ttsManager.speak(
                text = message.content,
                messageId = message.id,
                speechRate = settings.value.ttsSpeechRate,
                pitch = settings.value.ttsPitch
            )
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun onVoiceResult(recognizedText: String) {
        _uiState.update {
            val current = it.inputQuery
            val updated = if (current.isBlank()) recognizedText else "$current $recognizedText"
            it.copy(inputQuery = updated, isListening = false, liveVoiceSpokenText = recognizedText)
        }
        if (_uiState.value.showLiveVoiceScreen) {
            sendLiveVoiceMessage(recognizedText)
        }
    }

    fun sendLiveVoiceMessage(voicePrompt: String) {
        if (voicePrompt.isBlank()) return
        _uiState.update { it.copy(liveVoiceSpokenText = voicePrompt, liveVoiceAiResponse = "Thinking...") }
        viewModelScope.launch {
            val activeConv = _uiState.value.activeConversation ?: chatRepository.getOrCreateCurrentConversation()
            val result = chatRepository.streamUserMessage(
                conversationId = activeConv.id,
                userPrompt = voicePrompt,
                onChunk = { chunk ->
                    _uiState.update { it.copy(liveVoiceAiResponse = chunk) }
                }
            )
            result.onSuccess { msg ->
                _uiState.update { it.copy(liveVoiceAiResponse = msg.content) }
                ttsManager.speak(
                    text = msg.content,
                    messageId = msg.id,
                    speechRate = settings.value.ttsSpeechRate,
                    pitch = settings.value.ttsPitch
                )
            }.onFailure { err ->
                _uiState.update { it.copy(liveVoiceAiResponse = "Sorry, I couldn't reach the live AI: ${err.message}") }
            }
        }
    }

    fun setListening(isListening: Boolean) {
        _uiState.update { it.copy(isListening = isListening) }
    }

    fun togglePin(conversationId: String, isPinned: Boolean) {
        viewModelScope.launch {
            chatRepository.togglePinConversation(conversationId, isPinned)
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            ttsManager.stop()
            chatRepository.deleteConversation(conversationId)
            if (_uiState.value.activeConversation?.id == conversationId) {
                _uiState.update { it.copy(activeConversation = null, messages = emptyList()) }
            }
        }
    }

    fun renameActiveConversation(newTitle: String) {
        val active = _uiState.value.activeConversation ?: return
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            chatRepository.renameConversation(active.id, newTitle.trim())
            _uiState.update {
                it.copy(
                    activeConversation = it.activeConversation?.copy(title = newTitle.trim()),
                    showRenameDialog = false
                )
            }
        }
    }

    fun clearCurrentConversation() {
        val active = _uiState.value.activeConversation ?: return
        viewModelScope.launch {
            stopGeneration()
            ttsManager.stop()
            chatRepository.clearMessagesInConversation(active.id)
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            stopGeneration()
            ttsManager.stop()
            chatRepository.clearAllHistory()
            _uiState.update {
                it.copy(
                    activeConversation = null,
                    messages = emptyList(),
                    showClearAllDialog = false
                )
            }
            startNewChat()
        }
    }

    // Grounding & Persona toggles
    fun toggleSearchGrounding() {
        val current = settings.value.searchGroundingEnabled
        settingsRepository.setSearchGroundingEnabled(!current)
    }

    fun toggleMapsGrounding() {
        val current = settings.value.mapsGroundingEnabled
        settingsRepository.setMapsGroundingEnabled(!current)
    }

    fun setBotRole(role: BotRole) {
        settingsRepository.setActiveBotRole(role)
    }

    // Dialog & Screen Visibility Toggles
    fun setShowNewChatDialog(show: Boolean) {
        _uiState.update { it.copy(showNewChatDialog = show) }
    }

    fun setShowProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showProfileDialog = show) }
    }

    fun setShowRenameDialog(show: Boolean) {
        _uiState.update { it.copy(showRenameDialog = show) }
    }

    fun setShowClearAllDialog(show: Boolean) {
        _uiState.update { it.copy(showClearAllDialog = show) }
    }

    fun setShowLiveVoiceScreen(show: Boolean) {
        _uiState.update { it.copy(showLiveVoiceScreen = show) }
    }

    // Settings actions
    fun setModel(model: AiModelOption) {
        settingsRepository.setSelectedModel(model)
    }

    fun setVoiceEnabled(enabled: Boolean) {
        settingsRepository.setVoiceEnabled(enabled)
    }

    fun setVoice(voice: String) {
        settingsRepository.setSelectedVoice(voice)
    }

    fun setTtsSpeechRate(rate: Float) {
        settingsRepository.setTtsSpeechRate(rate)
    }

    fun setPlayResponseSound(play: Boolean) {
        settingsRepository.setPlayResponseSound(play)
    }

    fun setAutoTtsEnabled(enabled: Boolean) {
        settingsRepository.setAutoTtsEnabled(enabled)
    }

    fun setChatHistoryEnabled(enabled: Boolean) {
        settingsRepository.setChatHistoryEnabled(enabled)
    }

    fun setDataUsageEnabled(enabled: Boolean) {
        settingsRepository.setDataUsageEnabled(enabled)
    }

    fun setPersonalizationEnabled(enabled: Boolean) {
        settingsRepository.setPersonalizationEnabled(enabled)
    }

    fun setIncognitoMode(enabled: Boolean) {
        settingsRepository.setIncognitoMode(enabled)
    }

    fun updateCustomApiKey(key: String) {
        settingsRepository.updateCustomApiKey(key)
    }

    fun testVoiceOutput() {
        val testMessage = "Hello! I am Usman AI, your futuristic intelligent voice assistant."
        ttsManager.speak(
            text = testMessage,
            messageId = "test_tts_preview",
            speechRate = settings.value.ttsSpeechRate,
            pitch = settings.value.ttsPitch
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
