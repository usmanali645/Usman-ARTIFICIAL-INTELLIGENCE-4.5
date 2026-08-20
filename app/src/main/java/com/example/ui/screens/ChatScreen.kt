package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ui.components.AppNavTab
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatMessageBubble
import com.example.ui.components.HomeFeatureGrid
import com.example.ui.components.NewChatDialog
import com.example.ui.components.ProfileDialog
import com.example.ui.components.TypingIndicator
import com.example.ui.components.UsmanBottomNavBar
import com.example.ui.components.UsmanHeader
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ChatViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var renameInputText by remember { mutableStateOf("") }

    // Intercept back events cleanly to manage modal state and keyboard focus
    val hasModalOpen = uiState.showLiveVoiceScreen ||
            uiState.showNewChatDialog ||
            uiState.showProfileDialog ||
            uiState.showRenameDialog ||
            uiState.showClearAllDialog

    BackHandler(enabled = hasModalOpen || uiState.currentTab != AppNavTab.HOME) {
        focusManager.clearFocus()
        when {
            uiState.showLiveVoiceScreen -> viewModel.setShowLiveVoiceScreen(false)
            uiState.showNewChatDialog -> viewModel.setShowNewChatDialog(false)
            uiState.showProfileDialog -> viewModel.setShowProfileDialog(false)
            uiState.showRenameDialog -> viewModel.setShowRenameDialog(false)
            uiState.showClearAllDialog -> viewModel.setShowClearAllDialog(false)
            uiState.currentTab != AppNavTab.HOME -> viewModel.setNavTab(AppNavTab.HOME)
        }
    }

    // Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.setListening(false)
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spoken?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                viewModel.onVoiceResult(recognizedText)
            }
        }
    }

    // Photo / Image Picker Launcher for Multimodal Vision
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Decode bounds first to prevent huge allocations
                var inputStream = context.contentResolver.openInputStream(uri)
                val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, boundsOptions)
                inputStream?.close()

                val maxDimension = 1024
                var sampleSize = 1
                val rawWidth = boundsOptions.outWidth
                val rawHeight = boundsOptions.outHeight
                if (rawHeight > maxDimension || rawWidth > maxDimension) {
                    val halfHeight = rawHeight / 2
                    val halfWidth = rawWidth / 2
                    while ((halfHeight / sampleSize) >= maxDimension && (halfWidth / sampleSize) >= maxDimension) {
                        sampleSize *= 2
                    }
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
                inputStream?.close()

                if (bitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val bytes = outputStream.toByteArray()
                    val base64Str = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    viewModel.attachImage(uri, base64Str, "image/jpeg")
                    Toast.makeText(context, "Image attached! Ask anything about it.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Could not process image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchVoiceRecognition() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Usman AI...")
            }
            viewModel.setListening(true)
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            viewModel.setListening(false)
            Toast.makeText(context, "Voice recognition unavailable on this device", Toast.LENGTH_SHORT).show()
        }
    }

    // Scroll to bottom on new messages or loading change
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground),
        containerColor = CyberBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                // Chat input bar only visible on HOME tab
                if (uiState.currentTab == AppNavTab.HOME) {
                    ChatInputBar(
                        inputQuery = uiState.inputQuery,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        onSend = { viewModel.sendMessage() },
                        onStop = { viewModel.stopGeneration() },
                        onMicClick = {
                            if (uiState.isListening) {
                                viewModel.setListening(false)
                            } else {
                                launchVoiceRecognition()
                            }
                        },
                        onAttachClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        attachedImageUri = uiState.attachedImageUri,
                        onRemoveAttachedImage = { viewModel.removeAttachedImage() },
                        isListening = uiState.isListening,
                        isLoading = uiState.isLoading,
                        selectedModel = settings.selectedModel,
                        onModelClick = { viewModel.setShowNewChatDialog(true) },
                        searchGroundingEnabled = settings.searchGroundingEnabled,
                        onToggleSearchGrounding = { viewModel.toggleSearchGrounding() },
                        mapsGroundingEnabled = settings.mapsGroundingEnabled,
                        onToggleMapsGrounding = { viewModel.toggleMapsGrounding() },
                        activeRole = settings.activeBotRole
                    )
                }

                // Global Bottom Navigation Bar (Home, Studio, Chats, Explore, Settings)
                UsmanBottomNavBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.setNavTab(it) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                AppNavTab.HOME -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Header
                        UsmanHeader(
                            conversationTitle = uiState.activeConversation?.title ?: "New Conversation",
                            onOpenProfile = { viewModel.setShowProfileDialog(true) },
                            onNewChat = { viewModel.setShowNewChatDialog(true) },
                            onOpenLiveVoice = { viewModel.setShowLiveVoiceScreen(true) },
                            onClearCurrentChat = { viewModel.clearCurrentConversation() },
                            onRenameChat = {
                                renameInputText = uiState.activeConversation?.title ?: ""
                                viewModel.setShowRenameDialog(true)
                            },
                            modifier = Modifier.statusBarsPadding()
                        )

                        if (uiState.messages.isEmpty()) {
                            // Home Screen Feature Grid
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                item {
                                    HomeFeatureGrid(
                                        onSelectFeature = { prompt ->
                                            viewModel.sendMessage(prompt)
                                        }
                                    )
                                }
                            }
                        } else {
                            // Chat Messages List
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = uiState.messages,
                                    key = { it.id }
                                ) { message ->
                                    ChatMessageBubble(
                                        message = message,
                                        isCurrentlySpeaking = uiState.speakingMessageId == message.id,
                                        onSpeakToggle = { viewModel.toggleSpeak(it) },
                                        onRegenerate = { viewModel.regenerateLastResponse() },
                                        onFollowUpClick = { followUp ->
                                            viewModel.sendMessage(followUp)
                                        }
                                    )
                                }

                                if (uiState.isLoading) {
                                    item {
                                        TypingIndicator()
                                    }
                                }
                            }
                        }
                    }
                }

                AppNavTab.STUDIO -> {
                    // Studio Hub Screen (Gemini 3 Pro Image, Flash Image, Veo Video, Lyria Music, Grounding)
                    StudioScreen(
                        aiStudioRepository = viewModel.aiStudioRepository,
                        onBack = { viewModel.setNavTab(AppNavTab.HOME) }
                    )
                }

                AppNavTab.CHATS -> {
                    // Chats List Screen
                    ChatsListScreen(
                        conversations = uiState.conversations,
                        activeConversationId = uiState.activeConversation?.id,
                        onSelectConversation = { id ->
                            viewModel.selectConversation(id)
                        },
                        onNewChatClick = {
                            viewModel.setShowNewChatDialog(true)
                        },
                        onTogglePin = { id, pin -> viewModel.togglePin(id, pin) },
                        onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                        onClearAll = { viewModel.setShowClearAllDialog(true) }
                    )
                }

                AppNavTab.EXPLORE -> {
                    // Explore Screen with prompt library & templates
                    ExploreScreen(
                        onSelectPrompt = { prompt ->
                            viewModel.setNavTab(AppNavTab.HOME)
                            viewModel.sendMessage(prompt)
                        }
                    )
                }

                AppNavTab.SETTINGS -> {
                    // Settings Screen
                    SettingsScreen(
                        settings = settings,
                        onModelChange = { viewModel.setModel(it) },
                        onVoiceEnabledChange = { viewModel.setVoiceEnabled(it) },
                        onVoiceChange = { viewModel.setVoice(it) },
                        onTtsSpeechRateChange = { viewModel.setTtsSpeechRate(it) },
                        onPlayResponseSoundChange = { viewModel.setPlayResponseSound(it) },
                        onAutoTtsChange = { viewModel.setAutoTtsEnabled(it) },
                        onChatHistoryChange = { viewModel.setChatHistoryEnabled(it) },
                        onDataUsageChange = { viewModel.setDataUsageEnabled(it) },
                        onPersonalizationChange = { viewModel.setPersonalizationEnabled(it) },
                        onIncognitoModeChange = { viewModel.setIncognitoMode(it) },
                        onCustomApiKeyChange = { viewModel.updateCustomApiKey(it) },
                        onTestVoice = { viewModel.testVoiceOutput() }
                    )
                }
            }
        }
    }

    // Modal Live Voice Screen (Gemini 3.1 Flash Live API)
    if (uiState.showLiveVoiceScreen) {
        LiveVoiceScreen(
            onDismiss = { viewModel.setShowLiveVoiceScreen(false) },
            onVoiceInputRequested = { launchVoiceRecognition() },
            spokenText = uiState.liveVoiceSpokenText,
            onSendPrompt = { prompt -> viewModel.sendLiveVoiceMessage(prompt) },
            aiResponseText = uiState.liveVoiceAiResponse,
            isAiSpeaking = uiState.speakingMessageId != null,
            onStopSpeaking = { viewModel.stopSpeaking() }
        )
    }

    // Modal Dialog: New Chat
    if (uiState.showNewChatDialog) {
        NewChatDialog(
            initialModel = settings.selectedModel,
            initialTone = settings.defaultTone,
            onDismiss = { viewModel.setShowNewChatDialog(false) },
            onStartChat = { title, model, tone ->
                viewModel.startNewChat(title, model, tone)
            }
        )
    }

    // Modal Dialog: Profile
    if (uiState.showProfileDialog) {
        ProfileDialog(
            onDismiss = { viewModel.setShowProfileDialog(false) }
        )
    }

    // Dialog: Rename Conversation
    if (uiState.showRenameDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowRenameDialog(false) },
            title = { Text("Rename Conversation", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInputText,
                    onValueChange = { renameInputText = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CyberSurfaceVariant,
                        unfocusedContainerColor = CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.renameActiveConversation(renameInputText) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowRenameDialog(false) }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog: Clear All Conversations
    if (uiState.showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowClearAllDialog(false) },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = CyberRose) },
            title = { Text("Clear All Conversations?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will permanently delete all conversation history and messages.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAllConversations() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRose)
                ) {
                    Text("Delete All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowClearAllDialog(false) }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
