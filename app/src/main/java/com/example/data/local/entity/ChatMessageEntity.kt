package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId"), Index("timestamp")]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val isSpeaking: Boolean = false,
    val isStreaming: Boolean = false,
    val imageUri: String? = null,
    val followUpsJson: String? = null, // JSON list of suggested follow-up questions
    val groundingSourcesJson: String? = null, // JSON list of grounding source URLs / place cards
    val mediaType: String? = null, // "image", "music", "video", "maps"
    val mediaData: String? = null // Base64 image data or URL / audio data
)
