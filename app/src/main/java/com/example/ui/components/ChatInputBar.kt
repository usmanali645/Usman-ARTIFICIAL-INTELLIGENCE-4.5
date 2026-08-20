package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.repository.AiModelOption
import com.example.data.repository.BotRole
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceHighlight
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChatInputBar(
    inputQuery: String,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    attachedImageUri: Uri?,
    onRemoveAttachedImage: () -> Unit,
    isListening: Boolean,
    isLoading: Boolean,
    selectedModel: AiModelOption = AiModelOption.FLASH,
    onModelClick: () -> Unit = {},
    searchGroundingEnabled: Boolean = false,
    onToggleSearchGrounding: () -> Unit = {},
    mapsGroundingEnabled: Boolean = false,
    onToggleMapsGrounding: () -> Unit = {},
    activeRole: BotRole = BotRole.GENERAL,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // Quick Tool / Grounding / Model bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Selector Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onModelClick() }
                    .border(1.dp, NeonViolet.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "⚡ ${selectedModel.displayName}", fontSize = 10.sp, color = NeonViolet, fontWeight = FontWeight.Bold)
                }
            }

            // Google Search Grounding Toggle
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleSearchGrounding() }
                    .border(
                        1.dp,
                        if (searchGroundingEnabled) NeonCyan else CyberBorder,
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                color = if (searchGroundingEnabled) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (searchGroundingEnabled) NeonCyan else TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Search Grounding",
                        fontSize = 10.sp,
                        color = if (searchGroundingEnabled) NeonCyan else TextSecondary,
                        fontWeight = if (searchGroundingEnabled) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Google Maps Grounding Toggle
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleMapsGrounding() }
                    .border(
                        1.dp,
                        if (mapsGroundingEnabled) CyberEmerald else CyberBorder,
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                color = if (mapsGroundingEnabled) CyberEmerald.copy(alpha = 0.2f) else CyberSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = if (mapsGroundingEnabled) CyberEmerald else TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Maps Grounding",
                        fontSize = 10.sp,
                        color = if (mapsGroundingEnabled) CyberEmerald else TextSecondary,
                        fontWeight = if (mapsGroundingEnabled) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Active Bot Role Tag
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Text(
                    text = "${activeRole.iconEmoji} ${activeRole.title}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Attached Image Preview Card
        AnimatedVisibility(
            visible = attachedImageUri != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            if (attachedImageUri != null) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    color = CyberSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = attachedImageUri),
                            contentDescription = "Attached image",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Image attached",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Usman AI will analyze this image",
                                fontSize = 10.sp,
                                color = NeonCyan
                            )
                        }
                        IconButton(
                            onClick = onRemoveAttachedImage,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Input pill container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = CyberSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    colors = if (isListening) listOf(CyberRose, NeonCyan)
                    else listOf(NeonViolet.copy(alpha = 0.4f), NeonCyan.copy(alpha = 0.3f))
                )
            ),
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paperclip Attachment Button
                IconButton(
                    onClick = onAttachClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("attach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach image",
                        tint = if (attachedImageUri != null) NeonCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Text Input Field
                TextField(
                    value = inputQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("message_input"),
                    placeholder = {
                        Text(
                            text = if (isListening) "Listening to your voice..." else "Ask anything to Usman AI...",
                            color = if (isListening) NeonCyan else TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = NeonCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = if (inputQuery.isNotBlank() && !isLoading) ImeAction.Send else ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if ((inputQuery.isNotBlank() || attachedImageUri != null) && !isLoading) {
                                onSend()
                            }
                        }
                    )
                )

                // Voice / Mic Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp)
                ) {
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(CyberRose.copy(alpha = 0.35f))
                        )
                    }
                    IconButton(
                        onClick = onMicClick,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("mic_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop voice input" else "Voice input",
                            tint = if (isListening) CyberRose else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send or Stop Button
                if (isLoading) {
                    Button(
                        onClick = onStop,
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .testTag("stop_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonViolet
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Stop",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    val canSend = (inputQuery.isNotBlank() || attachedImageUri != null)
                    IconButton(
                        onClick = {
                            if (canSend) onSend()
                        },
                        enabled = canSend,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (canSend) Brush.linearGradient(listOf(NeonViolet, NeonCyan))
                                else Brush.linearGradient(listOf(CyberSurface, CyberBorder))
                            )
                            .testTag("send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (canSend) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
