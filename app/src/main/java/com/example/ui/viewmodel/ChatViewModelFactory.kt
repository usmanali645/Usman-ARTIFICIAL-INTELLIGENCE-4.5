package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.AiStudioRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.SettingsRepository
import com.example.speech.TtsManager

class ChatViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val settingsRepo = SettingsRepository(context)
            val chatRepo = ChatRepository(
                context = context,
                conversationDao = database.conversationDao(),
                chatMessageDao = database.chatMessageDao(),
                settingsRepository = settingsRepo
            )
            val aiStudioRepo = AiStudioRepository(
                context = context,
                settingsRepository = settingsRepo
            )
            val tts = TtsManager(context)
            return ChatViewModel(chatRepo, settingsRepo, aiStudioRepo, tts) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
