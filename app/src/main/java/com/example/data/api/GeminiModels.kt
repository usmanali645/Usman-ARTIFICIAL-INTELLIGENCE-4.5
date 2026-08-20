package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

object GeminiModelNames {
    // Text & Reasoning Models
    const val GEMINI_3_1_PRO = "gemini-3.1-pro-preview"
    const val GEMINI_3_5_FLASH = "gemini-3.5-flash"
    const val GEMINI_3_1_FLASH_LITE = "gemini-3.1-flash-lite"

    // Image Generation Models
    const val GEMINI_3_PRO_IMAGE = "gemini-3-pro-image-preview"
    const val GEMINI_3_1_FLASH_IMAGE = "gemini-3.1-flash-image-preview"

    // Audio & Music Models
    const val LYRIA_3_CLIP = "lyria-3-clip-preview"
    const val LYRIA_3_PRO = "lyria-3-pro-preview"

    // Video Generation
    const val VEO_3_1_FAST = "veo-3.1-fast-generate-preview"

    // Real-Time Voice Conversation
    const val GEMINI_3_1_FLASH_LIVE = "gemini-3.1-flash-live-preview"
}

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @field:Json(name = "contents") val contents: List<ApiContent>,
    @field:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @field:Json(name = "safetySettings") val safetySettings: List<SafetySetting>? = null,
    @field:Json(name = "systemInstruction") val systemInstruction: ApiContent? = null,
    @field:Json(name = "tools") val tools: List<ApiTool>? = null
)

@JsonClass(generateAdapter = true)
data class ApiTool(
    @field:Json(name = "googleSearch") val googleSearch: Map<String, String>? = null,
    @field:Json(name = "googleMaps") val googleMaps: Map<String, String>? = null,
    @field:Json(name = "codeExecution") val codeExecution: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SafetySetting(
    @field:Json(name = "category") val category: String,
    @field:Json(name = "threshold") val threshold: String
)

@JsonClass(generateAdapter = true)
data class ApiContent(
    @field:Json(name = "role") val role: String? = null, // "user" or "model"
    @field:Json(name = "parts") val parts: List<ApiPart>
)

@JsonClass(generateAdapter = true)
data class ApiPart(
    @field:Json(name = "text") val text: String? = null,
    @field:Json(name = "inlineData") val inlineData: ApiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class ApiInlineData(
    @field:Json(name = "mimeType") val mimeType: String,
    @field:Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @field:Json(name = "temperature") val temperature: Float? = null,
    @field:Json(name = "topP") val topP: Float? = null,
    @field:Json(name = "topK") val topK: Int? = null,
    @field:Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null,
    @field:Json(name = "responseModalities") val responseModalities: List<String>? = null,
    @field:Json(name = "imageConfig") val imageConfig: ApiImageConfig? = null
)

@JsonClass(generateAdapter = true)
data class ApiImageConfig(
    @field:Json(name = "aspectRatio") val aspectRatio: String? = "1:1",
    @field:Json(name = "imageSize") val imageSize: String? = "1K"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @field:Json(name = "candidates") val candidates: List<ApiCandidate>? = null,
    @field:Json(name = "groundingMetadata") val groundingMetadata: ApiGroundingMetadata? = null,
    @field:Json(name = "error") val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class ApiCandidate(
    @field:Json(name = "content") val content: ApiContent? = null,
    @field:Json(name = "finishReason") val finishReason: String? = null,
    @field:Json(name = "groundingMetadata") val groundingMetadata: ApiGroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class ApiGroundingMetadata(
    @field:Json(name = "webSearchQueries") val webSearchQueries: List<String>? = null,
    @field:Json(name = "groundingChunks") val groundingChunks: List<ApiGroundingChunk>? = null,
    @field:Json(name = "searchEntryPoint") val searchEntryPoint: ApiSearchEntryPoint? = null
)

@JsonClass(generateAdapter = true)
data class ApiGroundingChunk(
    @field:Json(name = "web") val web: ApiWebSource? = null,
    @field:Json(name = "maps") val maps: ApiMapSource? = null
)

@JsonClass(generateAdapter = true)
data class ApiWebSource(
    @field:Json(name = "uri") val uri: String? = null,
    @field:Json(name = "title") val title: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiMapSource(
    @field:Json(name = "placeId") val placeId: String? = null,
    @field:Json(name = "title") val title: String? = null,
    @field:Json(name = "address") val address: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiSearchEntryPoint(
    @field:Json(name = "renderedContent") val renderedContent: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiError(
    @field:Json(name = "code") val code: Int? = null,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "status") val status: String? = null
)
