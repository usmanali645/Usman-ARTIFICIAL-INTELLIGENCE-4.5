package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.GeminiModelNames
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AiModelOption(
    val id: String,
    val displayName: String,
    val description: String,
    val geminiModelCode: String,
    val isRecommended: Boolean = false
) {
    FLASH(
        "flash",
        "Usman AI Flash",
        "General tasks, fast reasoning, vision & search grounding",
        GeminiModelNames.GEMINI_3_5_FLASH,
        isRecommended = true
    ),
    PRO(
        "pro",
        "Usman AI Pro",
        "Complex STEM tasks, deep reasoning, coding & analysis",
        GeminiModelNames.GEMINI_3_1_PRO
    ),
    LITE(
        "lite",
        "Usman AI Lite",
        "Ultra-fast lightning response generation for quick queries",
        GeminiModelNames.GEMINI_3_1_FLASH_LITE
    )
}

enum class AiTone(val displayName: String, val promptModifier: String) {
    DEFAULT("Default", "Maintain a balanced, helpful, knowledgeable, and polite tone."),
    PROFESSIONAL("Professional", "Use an executive, crisp, well-structured, formal, and authoritative tone."),
    FRIENDLY("Friendly", "Use an enthusiastic, warm, approachable, empathetic, and encouraging tone with tasteful emojis."),
    CREATIVE("Creative", "Use an expressive, imaginative, visionary, poetic, and engaging tone.")
}

enum class BotRole(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val systemPrompt: String
) {
    GENERAL(
        "general",
        "General Assistant",
        "🤖",
        "You are Usman AI, a versatile smart assistant helping with any question."
    ),
    CODING(
        "coding",
        "Code Architect",
        "💻",
        "You are an expert Principal Software Engineer. Provide robust, clean Kotlin, Python, and system designs with syntax highlighting."
    ),
    MATH(
        "math",
        "Math Professor",
        "📐",
        "You are a patient Mathematics Professor. Break down formulas, equations, calculus, and logic problems step-by-step."
    ),
    WRITER(
        "writer",
        "Creative Writer",
        "✍️",
        "You are an award-winning creative copywriter and novelist. Write engaging prose, scripts, and poetry."
    ),
    MAPS_GUIDE(
        "maps",
        "Maps & Travel Guide",
        "📍",
        "You are a local tour guide and navigation expert. Provide addresses, ratings, local spots, and travel directions."
    ),
    RESEARCHER(
        "researcher",
        "Search Researcher",
        "🌐",
        "You are an investigative research analyst. Provide factual, up-to-date data with search citations."
    )
}

data class AppSettingsState(
    val customApiKey: String = "",
    val selectedModel: AiModelOption = AiModelOption.FLASH,
    val defaultTone: AiTone = AiTone.DEFAULT,
    val activeBotRole: BotRole = BotRole.GENERAL,
    val temperature: Float = 0.7f,
    // Grounding Tools
    val searchGroundingEnabled: Boolean = false,
    val mapsGroundingEnabled: Boolean = false,
    // Voice Settings
    val voiceEnabled: Boolean = true,
    val selectedVoice: String = "Usman (Male)",
    val ttsSpeechRate: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val voiceResponseStyle: String = "Brief",
    val playResponseSound: Boolean = true,
    val autoTtsEnabled: Boolean = false,
    // Privacy Settings
    val chatHistoryEnabled: Boolean = true,
    val dataUsageEnabled: Boolean = false,
    val personalizationEnabled: Boolean = true,
    val incognitoMode: Boolean = false,
    // Appearance & Language
    val appearanceTheme: String = "Dark Futuristic",
    val language: String = "English"
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("usman_ai_settings_v3", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettingsState> = _settings.asStateFlow()

    private fun loadSettings(): AppSettingsState {
        val apiKey = prefs.getString("custom_api_key", "") ?: ""
        val modelId = prefs.getString("selected_model", AiModelOption.FLASH.id) ?: AiModelOption.FLASH.id
        val model = AiModelOption.values().find { it.id == modelId } ?: AiModelOption.FLASH
        val toneName = prefs.getString("default_tone", AiTone.DEFAULT.name) ?: AiTone.DEFAULT.name
        val tone = try { AiTone.valueOf(toneName) } catch (e: Exception) { AiTone.DEFAULT }
        val roleId = prefs.getString("active_bot_role", BotRole.GENERAL.id) ?: BotRole.GENERAL.id
        val role = BotRole.values().find { it.id == roleId } ?: BotRole.GENERAL
        val temp = prefs.getFloat("temperature", 0.7f)

        val searchGrounding = prefs.getBoolean("search_grounding", false)
        val mapsGrounding = prefs.getBoolean("maps_grounding", false)

        val voiceEnabled = prefs.getBoolean("voice_enabled", true)
        val selectedVoice = prefs.getString("selected_voice", "Usman (Male)") ?: "Usman (Male)"
        val rate = prefs.getFloat("tts_rate", 1.0f)
        val pitch = prefs.getFloat("tts_pitch", 1.0f)
        val voiceResp = prefs.getString("voice_resp_style", "Brief") ?: "Brief"
        val playSound = prefs.getBoolean("play_sound", true)
        val autoTts = prefs.getBoolean("auto_tts", false)

        val chatHist = prefs.getBoolean("chat_history", true)
        val dataUsage = prefs.getBoolean("data_usage", false)
        val personalization = prefs.getBoolean("personalization", true)
        val incognito = prefs.getBoolean("incognito_mode", false)

        val theme = prefs.getString("appearance_theme", "Dark Futuristic") ?: "Dark Futuristic"
        val lang = prefs.getString("language", "English") ?: "English"

        return AppSettingsState(
            customApiKey = apiKey,
            selectedModel = model,
            defaultTone = tone,
            activeBotRole = role,
            temperature = temp,
            searchGroundingEnabled = searchGrounding,
            mapsGroundingEnabled = mapsGrounding,
            voiceEnabled = voiceEnabled,
            selectedVoice = selectedVoice,
            ttsSpeechRate = rate,
            ttsPitch = pitch,
            voiceResponseStyle = voiceResp,
            playResponseSound = playSound,
            autoTtsEnabled = autoTts,
            chatHistoryEnabled = chatHist,
            dataUsageEnabled = dataUsage,
            personalizationEnabled = personalization,
            incognitoMode = incognito,
            appearanceTheme = theme,
            language = lang
        )
    }

    fun updateCustomApiKey(key: String) {
        prefs.edit().putString("custom_api_key", key.trim()).apply()
        _settings.value = _settings.value.copy(customApiKey = key.trim())
    }

    fun setSelectedModel(model: AiModelOption) {
        prefs.edit().putString("selected_model", model.id).apply()
        _settings.value = _settings.value.copy(selectedModel = model)
    }

    fun setDefaultTone(tone: AiTone) {
        prefs.edit().putString("default_tone", tone.name).apply()
        _settings.value = _settings.value.copy(defaultTone = tone)
    }

    fun setActiveBotRole(role: BotRole) {
        prefs.edit().putString("active_bot_role", role.id).apply()
        _settings.value = _settings.value.copy(activeBotRole = role)
    }

    fun setSearchGroundingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("search_grounding", enabled).apply()
        _settings.value = _settings.value.copy(searchGroundingEnabled = enabled)
    }

    fun setMapsGroundingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("maps_grounding", enabled).apply()
        _settings.value = _settings.value.copy(mapsGroundingEnabled = enabled)
    }

    fun setTemperature(temp: Float) {
        prefs.edit().putFloat("temperature", temp).apply()
        _settings.value = _settings.value.copy(temperature = temp)
    }

    fun setVoiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("voice_enabled", enabled).apply()
        _settings.value = _settings.value.copy(voiceEnabled = enabled)
    }

    fun setSelectedVoice(voice: String) {
        prefs.edit().putString("selected_voice", voice).apply()
        _settings.value = _settings.value.copy(selectedVoice = voice)
    }

    fun setTtsSpeechRate(rate: Float) {
        prefs.edit().putFloat("tts_rate", rate).apply()
        _settings.value = _settings.value.copy(ttsSpeechRate = rate)
    }

    fun setTtsPitch(pitch: Float) {
        prefs.edit().putFloat("tts_pitch", pitch).apply()
        _settings.value = _settings.value.copy(ttsPitch = pitch)
    }

    fun setVoiceResponseStyle(style: String) {
        prefs.edit().putString("voice_resp_style", style).apply()
        _settings.value = _settings.value.copy(voiceResponseStyle = style)
    }

    fun setPlayResponseSound(play: Boolean) {
        prefs.edit().putBoolean("play_sound", play).apply()
        _settings.value = _settings.value.copy(playResponseSound = play)
    }

    fun setAutoTtsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_tts", enabled).apply()
        _settings.value = _settings.value.copy(autoTtsEnabled = enabled)
    }

    fun setChatHistoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("chat_history", enabled).apply()
        _settings.value = _settings.value.copy(chatHistoryEnabled = enabled)
    }

    fun setDataUsageEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("data_usage", enabled).apply()
        _settings.value = _settings.value.copy(dataUsageEnabled = enabled)
    }

    fun setPersonalizationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("personalization", enabled).apply()
        _settings.value = _settings.value.copy(personalizationEnabled = enabled)
    }

    fun setIncognitoMode(enabled: Boolean) {
        prefs.edit().putBoolean("incognito_mode", enabled).apply()
        _settings.value = _settings.value.copy(incognitoMode = enabled)
    }

    fun setAppearanceTheme(theme: String) {
        prefs.edit().putString("appearance_theme", theme).apply()
        _settings.value = _settings.value.copy(appearanceTheme = theme)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _settings.value = _settings.value.copy(language = lang)
    }

    fun buildSystemPrompt(
        model: AiModelOption = _settings.value.selectedModel,
        tone: AiTone = _settings.value.defaultTone,
        role: BotRole = _settings.value.activeBotRole
    ): String {
        return buildString {
            append("You are Usman AI, a premium, fast, highly intelligent, and versatile Android AI assistant.\n\n")
            append("ROLE: ${role.title}\n")
            append("${role.systemPrompt}\n\n")
            
            append("CAPABILITIES:\n")
            append("- Help the user with complex reasoning, STEM & math step-by-step, coding, summaries, translations, brainstorms, search grounding, and image understanding.\n")
            append("- Format output cleanly using Markdown, bold highlights, clear bullet points, or syntax-highlighted code blocks (```kotlin, ```python, etc.) where appropriate.\n\n")
            
            append("TONE & PERSONALITY:\n")
            append("- ${tone.promptModifier}\n\n")

            append("SPECIALIZATION:\n")
            when (model) {
                AiModelOption.PRO -> append("- You are operating in High-Reasoning Pro Mode (gemini-3.1-pro-preview). Provide in-depth analysis, comprehensive STEM problem-solving, and architectural precision.\n")
                AiModelOption.FLASH -> append("- You are operating in Flash Mode (gemini-3.5-flash). Provide balanced, rapid, high-quality answers.\n")
                AiModelOption.LITE -> append("- You are operating in Fast Lite Mode (gemini-3.1-flash-lite). Be ultra-concise, rapid, direct, and swift.\n")
            }

            append("\nRESPONSIBLE AI & SAFETY RULES:\n")
            append("- Do not generate sexually explicit, pornographic, or non-consensual content.\n")
            append("- Do not provide instructions for dangerous, illegal, weapons, self-harm, or harmful activities.\n")
            append("- Do not assist in bypassing security or safety mechanisms.\n")
            append("- For questions regarding puberty, relationships, reproductive health, anatomy, and human biology, provide factual, objective, scientific, respectful, and age-appropriate information.\n")
            append("- Protect user privacy and maintain confidentiality.\n")
        }
    }
}
