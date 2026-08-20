package com.example.ui.screens

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiModelNames
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun LiveVoiceScreen(
    onDismiss: () -> Unit,
    onVoiceInputRequested: () -> Unit,
    spokenText: String,
    onSendPrompt: (String) -> Unit,
    aiResponseText: String,
    isAiSpeaking: Boolean,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMicActive by remember { mutableStateOf(true) }
    var selectedPersona by remember { mutableStateOf("Usman Male") }

    BackHandler(onBack = onDismiss)

    val infiniteTransition = rememberInfiniteTransition(label = "live_orb")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Live Mode", tint = TextPrimary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Gemini 3.1 Flash Live",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "Real-Time Bi-Directional Voice API",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = {
                    selectedPersona = if (selectedPersona == "Usman Male") "Aria Female" else "Usman Male"
                    Toast.makeText(context, "Voice: $selectedPersona", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Switch Voice", tint = NeonViolet)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Live Glowing AI Neural Orb
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(if (isAiSpeaking || spokenText.isNotBlank()) pulseScale else 1.0f),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonViolet.copy(alpha = 0.35f),
                            NeonCyan.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 1.8f
                )
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(NeonCyan, NeonViolet, CyberEmerald, NeonCyan)
                    ),
                    radius = size.minDimension / 2.3f,
                    style = Stroke(width = 4f)
                )
            }

            // Inner Orb Sphere
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF6B21A8),
                                Color(0xFF1E1B4B),
                                Color(0xFF090A10)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(NeonCyan, NeonVioletLight)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isAiSpeaking) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isAiSpeaking) NeonCyan else NeonVioletLight,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAiSpeaking) "Speaking..." else "Listening...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAiSpeaking) NeonCyan else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Transcript & Response Feed Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = CyberSurfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (spokenText.isNotBlank()) {
                    Text(
                        text = "You: $spokenText",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (aiResponseText.isNotBlank()) {
                    Text(
                        text = "Usman AI: $aiResponseText",
                        fontSize = 13.sp,
                        color = NeonCyan,
                        lineHeight = 18.sp
                    )
                } else if (spokenText.isBlank()) {
                    Text(
                        text = "Say anything out loud. Usman AI Live will listen, understand, and reply instantly in natural voice.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Voice Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interrupt / Stop Speaking Button
            IconButton(
                onClick = onStopSpeaking,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isAiSpeaking) CyberRose else CyberSurfaceVariant)
                    .testTag("interrupt_voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop AI voice",
                    tint = if (isAiSpeaking) Color.White else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Main Microphone Tap-To-Speak Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (isMicActive) Color(0xFFA855F7) else Color(0xFF4B5563),
                                if (isMicActive) Color(0xFF00E5FF) else Color(0xFF1F2937)
                            )
                        )
                    )
                    .clickable {
                        isMicActive = !isMicActive
                        if (isMicActive) onVoiceInputRequested()
                    }
                    .testTag("live_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMicActive) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Microphone",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Quick Query Preset Button
            IconButton(
                onClick = {
                    val prompt = "Tell me something fascinating about space exploration"
                    onSendPrompt(prompt)
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Quick Topic",
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
