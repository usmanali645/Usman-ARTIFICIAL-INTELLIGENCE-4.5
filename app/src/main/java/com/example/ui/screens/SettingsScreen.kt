package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.AiModelOption
import com.example.data.repository.AppSettingsState
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class SettingsSubScreen {
    MAIN,
    AI_MODEL,
    VOICE_SETTINGS,
    PRIVACY_SETTINGS,
    API_KEY,
    ABOUT,
    HELP
}

@Composable
fun SettingsScreen(
    settings: AppSettingsState,
    onModelChange: (AiModelOption) -> Unit,
    onVoiceEnabledChange: (Boolean) -> Unit,
    onVoiceChange: (String) -> Unit,
    onTtsSpeechRateChange: (Float) -> Unit,
    onPlayResponseSoundChange: (Boolean) -> Unit,
    onAutoTtsChange: (Boolean) -> Unit,
    onChatHistoryChange: (Boolean) -> Unit,
    onDataUsageChange: (Boolean) -> Unit,
    onPersonalizationChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
    onCustomApiKeyChange: (String) -> Unit,
    onTestVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subScreen by remember { mutableStateOf(SettingsSubScreen.MAIN) }

    when (subScreen) {
        SettingsSubScreen.MAIN -> {
            MainSettingsList(
                settings = settings,
                onNavigate = { subScreen = it },
                modifier = modifier
            )
        }
        SettingsSubScreen.AI_MODEL -> {
            AiModelSelectionView(
                selectedModel = settings.selectedModel,
                onSelectModel = {
                    onModelChange(it)
                    subScreen = SettingsSubScreen.MAIN
                },
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
        SettingsSubScreen.VOICE_SETTINGS -> {
            VoiceSettingsView(
                settings = settings,
                onVoiceEnabledChange = onVoiceEnabledChange,
                onVoiceChange = onVoiceChange,
                onSpeechRateChange = onTtsSpeechRateChange,
                onPlaySoundChange = onPlayResponseSoundChange,
                onAutoTtsChange = onAutoTtsChange,
                onTestVoice = onTestVoice,
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
        SettingsSubScreen.PRIVACY_SETTINGS -> {
            PrivacySettingsView(
                settings = settings,
                onChatHistoryChange = onChatHistoryChange,
                onDataUsageChange = onDataUsageChange,
                onPersonalizationChange = onPersonalizationChange,
                onIncognitoModeChange = onIncognitoModeChange,
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
        SettingsSubScreen.API_KEY -> {
            ApiKeySettingsView(
                customApiKey = settings.customApiKey,
                onSaveKey = {
                    onCustomApiKeyChange(it)
                    subScreen = SettingsSubScreen.MAIN
                },
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
        SettingsSubScreen.ABOUT -> {
            AboutUsmanAiView(
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
        SettingsSubScreen.HELP -> {
            HelpSupportView(
                onBack = { subScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
    }
}

@Composable
fun MainSettingsList(
    settings: AppSettingsState,
    onNavigate: (SettingsSubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CyberSurface.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Settings items (matching screenshot 5!)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsNavigationRow(
                icon = Icons.Default.SmartToy,
                iconColor = NeonViolet,
                title = "AI Model",
                subtitle = settings.selectedModel.displayName,
                onClick = { onNavigate(SettingsSubScreen.AI_MODEL) },
                testTag = "settings_ai_model"
            )

            SettingsNavigationRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                iconColor = NeonCyan,
                title = "Voice Settings",
                subtitle = if (settings.voiceEnabled) settings.selectedVoice else "Disabled",
                onClick = { onNavigate(SettingsSubScreen.VOICE_SETTINGS) },
                testTag = "settings_voice"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Shield,
                iconColor = Color(0xFF10B981),
                title = "Privacy Settings",
                subtitle = if (settings.incognitoMode) "Incognito Active" else "Standard",
                onClick = { onNavigate(SettingsSubScreen.PRIVACY_SETTINGS) },
                testTag = "settings_privacy"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Key,
                iconColor = Color(0xFFF59E0B),
                title = "Gemini API Key",
                subtitle = if (settings.customApiKey.isNotBlank()) "Custom Key Set" else "Default AI Studio Key",
                onClick = { onNavigate(SettingsSubScreen.API_KEY) },
                testTag = "settings_api_key"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Palette,
                iconColor = Color(0xFFA855F7),
                title = "Appearance",
                subtitle = settings.appearanceTheme,
                onClick = { Toast.makeText(context, "Appearance: Dark Futuristic Active", Toast.LENGTH_SHORT).show() },
                testTag = "settings_appearance"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFFEC4899),
                title = "Notifications",
                subtitle = "Enabled",
                onClick = { Toast.makeText(context, "Notifications configured", Toast.LENGTH_SHORT).show() },
                testTag = "settings_notifications"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Language,
                iconColor = NeonCyan,
                title = "Language",
                subtitle = settings.language,
                onClick = { Toast.makeText(context, "Language: English", Toast.LENGTH_SHORT).show() },
                testTag = "settings_language"
            )

            SettingsNavigationRow(
                icon = Icons.Default.Info,
                iconColor = Color(0xFF38BDF8),
                title = "About Usman AI",
                subtitle = "v2.0.0 Pro Production",
                onClick = { onNavigate(SettingsSubScreen.ABOUT) },
                testTag = "settings_about"
            )

            SettingsNavigationRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconColor = Color(0xFF94A3B8),
                title = "Help & Support",
                subtitle = "Guides & FAQs",
                onClick = { onNavigate(SettingsSubScreen.HELP) },
                testTag = "settings_help"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsNavigationRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF131422)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// Screenshot 7: AI Model Selection View
@Composable
fun AiModelSelectionView(
    selectedModel: AiModelOption,
    onSelectModel: (AiModelOption) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Model", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        AiModelOption.values().forEach { model ->
            val isSelected = model == selectedModel
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectModel(model) }
                    .border(
                        1.dp,
                        if (isSelected) Brush.linearGradient(listOf(NeonViolet, NeonCyan))
                        else Brush.linearGradient(listOf(CyberBorder, CyberBorder)),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("model_select_${model.id}"),
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Color(0xFF1C1935) else Color(0xFF131422)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF22163B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.usman_ai_logo_1787107572616),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = model.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonViolet else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = model.description,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(NeonViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Models are updated regularly. Choose the one that fits your needs.",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Screenshot 8: Voice Settings View
@Composable
fun VoiceSettingsView(
    settings: AppSettingsState,
    onVoiceEnabledChange: (Boolean) -> Unit,
    onVoiceChange: (String) -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onPlaySoundChange: (Boolean) -> Unit,
    onAutoTtsChange: (Boolean) -> Unit,
    onTestVoice: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Voice Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Enabled Toggle
        SettingSwitchRow(
            title = "Voice Enabled",
            subtitle = "Enable speech recognition & TTS answers",
            checked = settings.voiceEnabled,
            onCheckedChange = onVoiceEnabledChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Voice Picker
        val voices = listOf("Usman (Male)", "Aria (Female)", "Nova (Neural)")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131422)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Voice Persona", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                voices.forEach { voice ->
                    val isSelected = voice == settings.selectedVoice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onVoiceChange(voice) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = voice,
                            fontSize = 13.sp,
                            color = if (isSelected) NeonViolet else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Speech Speed Slider
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131422)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Speech Speed", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("${String.format("%.1f", settings.ttsSpeechRate)}x", fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.ttsSpeechRate,
                    onValueChange = onSpeechRateChange,
                    valueRange = 0.5f..2.0f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonViolet,
                        activeTrackColor = NeonViolet,
                        inactiveTrackColor = CyberBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Play response sound
        SettingSwitchRow(
            title = "Play response sound",
            subtitle = "Audio chime when response completes",
            checked = settings.playResponseSound,
            onCheckedChange = onPlaySoundChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Auto read responses
        SettingSwitchRow(
            title = "Auto read responses",
            subtitle = "Automatically speak every AI answer aloud",
            checked = settings.autoTtsEnabled,
            onCheckedChange = onAutoTtsChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onTestVoice,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet.copy(alpha = 0.25f))
        ) {
            Text("Test Voice Audio 🔊", color = NeonCyan, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Screenshot 9: Privacy Settings View
@Composable
fun PrivacySettingsView(
    settings: AppSettingsState,
    onChatHistoryChange: (Boolean) -> Unit,
    onDataUsageChange: (Boolean) -> Unit,
    onPersonalizationChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Privacy Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingSwitchRow(
            title = "Chat History",
            subtitle = "Save chats and conversation history locally",
            checked = settings.chatHistoryEnabled,
            onCheckedChange = onChatHistoryChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingSwitchRow(
            title = "Data Usage",
            subtitle = "Allow anonymized diagnostics to improve AI quality",
            checked = settings.dataUsageEnabled,
            onCheckedChange = onDataUsageChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingSwitchRow(
            title = "Personalization",
            subtitle = "Personalize your experience and tone memory",
            checked = settings.personalizationEnabled,
            onCheckedChange = onPersonalizationChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingSwitchRow(
            title = "Incognito Mode",
            subtitle = "Disable history and personalization during sessions",
            checked = settings.incognitoMode,
            onCheckedChange = onIncognitoModeChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🛡️ We respect your privacy. Your conversation data is encrypted, stored locally on your device, and never sold.",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF131422)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonViolet,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = CyberBorder
                )
            )
        }
    }
}

@Composable
fun ApiKeySettingsView(
    customApiKey: String,
    onSaveKey: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var keyInput by remember { mutableStateOf(customApiKey) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gemini API Key", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Usman AI uses Google Gemini. You can provide your own Gemini API key for direct unlimited access or utilize the built-in system key.",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            placeholder = { Text("AIzaSy...", color = TextMuted) },
            label = { Text("API Key") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("api_key_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onSaveKey(keyInput.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_api_key_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
        ) {
            Text("Save API Key", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AboutUsmanAiView(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("About Usman AI", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, Brush.linearGradient(listOf(NeonViolet, NeonCyan)), RoundedCornerShape(18.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.usman_ai_logo_1787107572616),
                contentDescription = "Usman AI Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Usman AI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Version 2.0.0 Production Edition", fontSize = 12.sp, color = NeonCyan)

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131422)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Usman AI is a cutting-edge, futuristic Android AI assistant engineered for high-performance question answering, code generation, real-time voice interaction, multi-modal image intelligence, and productive workflows.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun HelpSupportView(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Help & Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val faqs = listOf(
            "How do I use voice input?" to "Tap the microphone icon on the right side of the chat input bar and speak your question. Tap stop or finish speaking.",
            "Can I attach photos?" to "Yes! Tap the paperclip icon to attach an image. Usman AI will examine and answer questions about it.",
            "How does streaming work?" to "Responses stream chunk-by-chunk in real time. You can tap 'Stop' at any moment to cancel generation.",
            "How do I switch AI models?" to "Go to Settings > AI Model or tap New Chat to choose between Usman AI Pro, Lite, Code, or Creative."
        )

        faqs.forEach { (q, a) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF131422)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(q, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(a, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }
        }
    }
}
