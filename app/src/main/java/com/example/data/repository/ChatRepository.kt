package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.ApiContent
import com.example.data.api.ApiInlineData
import com.example.data.api.ApiPart
import com.example.data.api.ApiTool
import com.example.data.api.GeminiApiService
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.RetrofitClient
import com.example.data.api.SafetySetting
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.ConversationDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

class ChatRepository(
    private val context: Context,
    private val conversationDao: ConversationDao,
    private val chatMessageDao: ChatMessageDao,
    private val settingsRepository: SettingsRepository,
    private val geminiService: GeminiApiService = RetrofitClient.geminiService
) {

    fun getConversations(): Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getMessagesForConversation(conversationId)

    suspend fun getOrCreateCurrentConversation(): ConversationEntity = withContext(Dispatchers.IO) {
        val conversations = conversationDao.getConversationsList()
        if (conversations.isNotEmpty()) {
            conversations.first()
        } else {
            val newId = UUID.randomUUID().toString()
            val newConv = ConversationEntity(
                id = newId,
                title = "New Conversation",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            conversationDao.insertConversation(newConv)
            newConv
        }
    }

    suspend fun createNewConversation(title: String = "New Conversation"): ConversationEntity = withContext(Dispatchers.IO) {
        val newId = UUID.randomUUID().toString()
        val newConv = ConversationEntity(
            id = newId,
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        conversationDao.insertConversation(newConv)
        newConv
    }

    suspend fun renameConversation(conversationId: String, newTitle: String) = withContext(Dispatchers.IO) {
        conversationDao.updateTitle(conversationId, newTitle.trim())
    }

    suspend fun togglePinConversation(conversationId: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.setPinned(conversationId, isPinned)
    }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversation(conversationId)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.deleteAllMessages()
        conversationDao.deleteAllConversations()
    }

    suspend fun clearMessagesInConversation(conversationId: String) = withContext(Dispatchers.IO) {
        chatMessageDao.deleteMessagesForConversation(conversationId)
    }

    suspend fun deleteMessage(messageId: String) = withContext(Dispatchers.IO) {
        chatMessageDao.deleteMessage(messageId)
    }

    suspend fun streamUserMessage(
        conversationId: String,
        userPrompt: String,
        imageBase64: String? = null,
        imageMimeType: String? = null,
        imageUri: String? = null,
        enableSearchGrounding: Boolean = false,
        enableMapsGrounding: Boolean = false,
        onChunk: (accumulatedText: String) -> Unit
    ): Result<ChatMessageEntity> = withContext(Dispatchers.IO) {
        val settings = settingsRepository.settings.value

        // If chat history is disabled (Incognito Mode or Privacy option), we handle accordingly
        val isHistoryEnabled = settings.chatHistoryEnabled && !settings.incognitoMode

        // 1. Insert user message into local database
        val userMsgId = UUID.randomUUID().toString()
        val userMsg = ChatMessageEntity(
            id = userMsgId,
            conversationId = conversationId,
            role = "user",
            content = userPrompt.trim(),
            timestamp = System.currentTimeMillis(),
            imageUri = imageUri
        )
        if (isHistoryEnabled) {
            chatMessageDao.insertMessage(userMsg)
            conversationDao.updateTimestamp(conversationId, System.currentTimeMillis())
        }

        // Update auto-title if placeholder
        val currentConv = conversationDao.getConversationById(conversationId)
        if (currentConv != null && (currentConv.title == "New Conversation" || currentConv.title.isBlank())) {
            val autoTitle = if (userPrompt.length > 28) {
                userPrompt.take(28).trim() + "…"
            } else {
                userPrompt.trim()
            }
            if (isHistoryEnabled) {
                conversationDao.updateTitle(conversationId, autoTitle)
            }
        }

        // Resolve API key
        val apiKey = if (settings.customApiKey.isNotBlank()) {
            settings.customApiKey
        } else {
            BuildConfig.GEMINI_API_KEY
        }

        // 2. Prepare Placeholder AI message
        val aiMsgId = UUID.randomUUID().toString()
        var aiMsg = ChatMessageEntity(
            id = aiMsgId,
            conversationId = conversationId,
            role = "model",
            content = "",
            timestamp = System.currentTimeMillis(),
            isStreaming = true
        )
        if (isHistoryEnabled) {
            chatMessageDao.insertMessage(aiMsg)
        }

        // Check if API key is present
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val fallbackResponse = buildString {
                append("👋 **Hello! I'm Usman AI, your intelligent assistant.**\n\n")
                append("To connect to the live Gemini AI engine:\n")
                append("1. Add your `GEMINI_API_KEY` to the **Secrets panel in AI Studio**, or\n")
                append("2. Open **Settings ⚙️** right here in Usman AI and enter your custom Gemini API key.\n\n")
                append("*You can test voice input, text-to-speech, models, tones, and history management right now!*")
            }
            aiMsg = aiMsg.copy(
                content = fallbackResponse,
                isStreaming = false
            )
            if (isHistoryEnabled) {
                chatMessageDao.insertMessage(aiMsg)
            }
            onChunk(fallbackResponse)
            return@withContext Result.success(aiMsg)
        }

        // 3. Build multi-turn context
        val existingMessages = if (isHistoryEnabled) {
            chatMessageDao.getMessagesList(conversationId)
        } else {
            listOf(userMsg)
        }

        val contentsList = mutableListOf<ApiContent>()
        for (msg in existingMessages) {
            if (msg.isError || msg.id == aiMsgId) continue
            if (msg.role == "user") {
                val parts = mutableListOf<ApiPart>()
                if (msg.id == userMsgId && !imageBase64.isNullOrBlank() && !imageMimeType.isNullOrBlank()) {
                    parts.add(ApiPart(inlineData = ApiInlineData(mimeType = imageMimeType, data = imageBase64)))
                }
                if (msg.content.isNotBlank()) {
                    parts.add(ApiPart(text = msg.content))
                }
                if (parts.isNotEmpty()) {
                    contentsList.add(ApiContent(role = "user", parts = parts))
                }
            } else if (msg.role == "model" && msg.content.isNotBlank()) {
                contentsList.add(ApiContent(role = "model", parts = listOf(ApiPart(text = msg.content))))
            }
        }

        // Limit multi-turn context window to last 16 turns to avoid latency
        val trimmedContents = if (contentsList.size > 16) {
            contentsList.takeLast(16)
        } else {
            contentsList
        }

        val targetModel = settings.selectedModel.geminiModelCode
        val systemPrompt = settingsRepository.buildSystemPrompt(
            model = settings.selectedModel,
            tone = settings.defaultTone,
            role = settings.activeBotRole
        )

        val safetySettings = listOf(
            SafetySetting(category = "HARM_CATEGORY_HARASSMENT", threshold = "BLOCK_MEDIUM_AND_ABOVE"),
            SafetySetting(category = "HARM_CATEGORY_HATE_SPEECH", threshold = "BLOCK_MEDIUM_AND_ABOVE"),
            SafetySetting(category = "HARM_CATEGORY_SEXUALLY_EXPLICIT", threshold = "BLOCK_MEDIUM_AND_ABOVE"),
            SafetySetting(category = "HARM_CATEGORY_DANGEROUS_CONTENT", threshold = "BLOCK_MEDIUM_AND_ABOVE")
        )

        // Tools for Search & Maps Grounding
        val toolsList = mutableListOf<ApiTool>()
        if (enableSearchGrounding || settings.searchGroundingEnabled) {
            toolsList.add(ApiTool(googleSearch = emptyMap()))
        }
        if (enableMapsGrounding || settings.mapsGroundingEnabled) {
            toolsList.add(ApiTool(googleMaps = emptyMap()))
        }

        val request = GenerateContentRequest(
            contents = trimmedContents,
            generationConfig = GenerationConfig(
                temperature = settings.temperature
            ),
            safetySettings = safetySettings,
            systemInstruction = ApiContent(
                parts = listOf(ApiPart(text = systemPrompt))
            ),
            tools = if (toolsList.isNotEmpty()) toolsList else null
        )

        // 4. Stream response with retry logic (up to 2 retries)
        var attempts = 0
        val maxAttempts = 2
        val stringBuffer = StringBuilder()
        val groundingChunksList = mutableListOf<String>()

        while (attempts < maxAttempts) {
            attempts++
            try {
                val streamResponse = geminiService.streamGenerateContent(
                    model = targetModel,
                    apiKey = apiKey,
                    request = request
                )

                if (streamResponse.isSuccessful) {
                    val responseBody = streamResponse.body()
                    if (responseBody != null) {
                        val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                            val currentLine = line?.trim() ?: continue
                            if (currentLine.startsWith("data: ")) {
                                val jsonStr = currentLine.removePrefix("data: ").trim()
                                if (jsonStr.isNotBlank() && jsonStr != "[DONE]") {
                                    try {
                                        val jsonObj = JSONObject(jsonStr)
                                        val candidates = jsonObj.optJSONArray("candidates")
                                        if (candidates != null && candidates.length() > 0) {
                                            val firstCand = candidates.getJSONObject(0)
                                            val contentObj = firstCand.optJSONObject("content")
                                            val partsArr = contentObj?.optJSONArray("parts")
                                            if (partsArr != null && partsArr.length() > 0) {
                                                val textPart = partsArr.getJSONObject(0).optString("text")
                                                if (!textPart.isNullOrEmpty()) {
                                                    stringBuffer.append(textPart)
                                                    onChunk(stringBuffer.toString())
                                                }
                                            }

                                            // Check for Grounding metadata
                                            val grounding = firstCand.optJSONObject("groundingMetadata")
                                                ?: jsonObj.optJSONObject("groundingMetadata")
                                            if (grounding != null) {
                                                val chunks = grounding.optJSONArray("groundingChunks")
                                                if (chunks != null) {
                                                    for (i in 0 until chunks.length()) {
                                                        val chunkObj = chunks.getJSONObject(i)
                                                        val web = chunkObj.optJSONObject("web")
                                                        if (web != null) {
                                                            val title = web.optString("title")
                                                            val uri = web.optString("uri")
                                                            if (uri.isNotBlank()) {
                                                                groundingChunksList.add("$title|$uri")
                                                            }
                                                        }
                                                        val map = chunkObj.optJSONObject("maps")
                                                        if (map != null) {
                                                            val title = map.optString("title")
                                                            val address = map.optString("address")
                                                            groundingChunksList.add("📍 $title - $address")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (jsonEx: Exception) {
                                        Log.w("ChatRepository", "Chunk parse exception: ${jsonEx.message}")
                                    }
                                }
                            }
                        }
                    }

                    // Done streaming successfully
                    val finalResponse = stringBuffer.toString()
                    if (finalResponse.isNotBlank()) {
                        val followUps = generateSuggestedFollowUps(userPrompt, finalResponse)
                        val followUpsJson = if (followUps.isNotEmpty()) {
                            JSONArray(followUps).toString()
                        } else null

                        val groundingJson = if (groundingChunksList.isNotEmpty()) {
                            JSONArray(groundingChunksList.distinct()).toString()
                        } else null

                        aiMsg = aiMsg.copy(
                            content = finalResponse,
                            isStreaming = false,
                            followUpsJson = followUpsJson,
                            groundingSourcesJson = groundingJson
                        )
                        if (isHistoryEnabled) {
                            chatMessageDao.insertMessage(aiMsg)
                            conversationDao.updateTimestamp(conversationId, System.currentTimeMillis())
                        }
                        return@withContext Result.success(aiMsg)
                    } else {
                        // If stream finished without text, try non-streaming fallback
                        return@withContext fetchNonStreamingFallback(
                            conversationId = conversationId,
                            aiMsgId = aiMsgId,
                            targetModel = targetModel,
                            apiKey = apiKey,
                            request = request,
                            userPrompt = userPrompt,
                            isHistoryEnabled = isHistoryEnabled,
                            onChunk = onChunk
                        )
                    }
                } else {
                    // Non-successful HTTP response
                    val errorBody = streamResponse.errorBody()?.string() ?: ""
                    val code = streamResponse.code()
                    if (code == 429 || code >= 500) {
                        // Transient server or rate limit error -> wait and retry once
                        if (attempts < maxAttempts) {
                            delay(1200L)
                            continue
                        }
                    }

                    val friendlyError = when (code) {
                        400 -> "Invalid request or API key format. Please check your Gemini API key."
                        403 -> "Access denied. Ensure your Gemini API Key is authorized."
                        429 -> "Rate limit reached. Please pause a moment before retrying."
                        else -> "Server response ($code): $errorBody"
                    }

                    aiMsg = aiMsg.copy(
                        content = "⚠️ $friendlyError",
                        isError = true,
                        isStreaming = false
                    )
                    if (isHistoryEnabled) {
                        chatMessageDao.insertMessage(aiMsg)
                    }
                    onChunk("⚠️ $friendlyError")
                    return@withContext Result.failure(Exception(friendlyError))
                }
            } catch (ce: CancellationException) {
                // User pressed STOP
                val currentText = stringBuffer.toString()
                val finalContent = if (currentText.isNotBlank()) {
                    "$currentText\n\n*(Generation stopped by user)*"
                } else {
                    "*(Generation stopped)*"
                }
                aiMsg = aiMsg.copy(
                    content = finalContent,
                    isStreaming = false
                )
                if (isHistoryEnabled) {
                    chatMessageDao.insertMessage(aiMsg)
                }
                onChunk(finalContent)
                return@withContext Result.success(aiMsg)
            } catch (e: Exception) {
                if (attempts < maxAttempts) {
                    delay(1000L)
                    continue
                }
                val networkErrorMsg = "⚠️ Connection error: ${e.localizedMessage ?: "Unable to reach Gemini API. Please check your internet."}"
                aiMsg = aiMsg.copy(
                    content = networkErrorMsg,
                    isError = true,
                    isStreaming = false
                )
                if (isHistoryEnabled) {
                    chatMessageDao.insertMessage(aiMsg)
                }
                onChunk(networkErrorMsg)
                return@withContext Result.failure(e)
            }
        }

        Result.failure(Exception("Unknown network error occurred"))
    }

    private suspend fun fetchNonStreamingFallback(
        conversationId: String,
        aiMsgId: String,
        targetModel: String,
        apiKey: String,
        request: GenerateContentRequest,
        userPrompt: String,
        isHistoryEnabled: Boolean,
        onChunk: (String) -> Unit
    ): Result<ChatMessageEntity> {
        return try {
            val nonStreamResp = geminiService.generateContent(
                model = targetModel,
                apiKey = apiKey,
                request = request
            )

            if (nonStreamResp.isSuccessful) {
                val candidate = nonStreamResp.body()?.candidates?.firstOrNull()
                val text = candidate?.content?.parts?.firstOrNull()?.text ?: "No text generated"

                val followUps = generateSuggestedFollowUps(userPrompt, text)
                val followUpsJson = if (followUps.isNotEmpty()) JSONArray(followUps).toString() else null

                val aiMsg = ChatMessageEntity(
                    id = aiMsgId,
                    conversationId = conversationId,
                    role = "model",
                    content = text,
                    timestamp = System.currentTimeMillis(),
                    isStreaming = false,
                    followUpsJson = followUpsJson
                )
                if (isHistoryEnabled) {
                    chatMessageDao.insertMessage(aiMsg)
                    conversationDao.updateTimestamp(conversationId, System.currentTimeMillis())
                }
                onChunk(text)
                Result.success(aiMsg)
            } else {
                val errorText = "⚠️ Unable to generate response. (${nonStreamResp.code()})"
                val aiMsg = ChatMessageEntity(
                    id = aiMsgId,
                    conversationId = conversationId,
                    role = "model",
                    content = errorText,
                    timestamp = System.currentTimeMillis(),
                    isError = true,
                    isStreaming = false
                )
                if (isHistoryEnabled) {
                    chatMessageDao.insertMessage(aiMsg)
                }
                onChunk(errorText)
                Result.failure(Exception(errorText))
            }
        } catch (e: Exception) {
            val errorText = "⚠️ Error: ${e.message}"
            val aiMsg = ChatMessageEntity(
                id = aiMsgId,
                conversationId = conversationId,
                role = "model",
                content = errorText,
                timestamp = System.currentTimeMillis(),
                isError = true,
                isStreaming = false
            )
            if (isHistoryEnabled) {
                chatMessageDao.insertMessage(aiMsg)
            }
            onChunk(errorText)
            Result.failure(e)
        }
    }

    private fun generateSuggestedFollowUps(userQuery: String, aiAnswer: String): List<String> {
        val lowerQuery = userQuery.lowercase()
        return when {
            lowerQuery.contains("code") || lowerQuery.contains("kotlin") || lowerQuery.contains("compose") || lowerQuery.contains("function") -> {
                listOf(
                    "Can you add unit tests for this?",
                    "How can we optimize performance?",
                    "Show an example with error handling"
                )
            }
            lowerQuery.contains("explain") || lowerQuery.contains("what is") || lowerQuery.contains("how does") -> {
                listOf(
                    "Can you explain it more simply?",
                    "Give a real-world analogy",
                    "What are the pros and cons?"
                )
            }
            lowerQuery.contains("math") || lowerQuery.contains("calculate") || lowerQuery.contains("solve") -> {
                listOf(
                    "Show the step-by-step breakdown",
                    "Can you solve an alternative method?",
                    "What is the mathematical proof?"
                )
            }
            lowerQuery.contains("where") || lowerQuery.contains("restaurant") || lowerQuery.contains("travel") || lowerQuery.contains("place") -> {
                listOf(
                    "Show opening hours and ratings",
                    "What are top nearby attractions?",
                    "Provide directions and travel tips"
                )
            }
            else -> {
                listOf(
                    "Tell me more about this",
                    "Summarize key takeaways",
                    "Give me actionable tips"
                )
            }
        }
    }
}
