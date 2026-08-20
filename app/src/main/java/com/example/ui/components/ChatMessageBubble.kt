package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entity.ChatMessageEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCodeBackground
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceHighlight
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    isCurrentlySpeaking: Boolean,
    onSpeakToggle: (ChatMessageEntity) -> Unit,
    onRegenerate: () -> Unit = {},
    onFollowUpClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUser = message.role == "user"
    var isLiked by remember { mutableStateOf<Boolean?>(null) }
    var copiedCodeBlock by remember { mutableStateOf<String?>(null) }

    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    val followUpsList = remember(message.followUpsJson) {
        if (!message.followUpsJson.isNullOrBlank()) {
            try {
                val array = JSONArray(message.followUpsJson)
                val list = mutableListOf<String>()
                for (i in 0 until array.length()) {
                    list.add(array.getString(i))
                }
                list
            } catch (e: Exception) {
                emptyList<String>()
            }
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(if (isUser) "user_message_${message.id}" else "ai_message_${message.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // AI Avatar (Left side, matches screenshot!)
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22163B))
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(NeonViolet, NeonCyan)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.usman_ai_logo_1787107572616),
                        contentDescription = "Usman AI",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }

            // Message Bubble Card
            Column(
                modifier = Modifier.widthIn(max = 310.dp),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                // Sender label
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                    ) {
                        Text(
                            text = "Usman AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonViolet
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(CyberEmerald)
                        )
                    }
                }

                Surface(
                    shape = if (isUser) {
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 4.dp
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    },
                    color = if (isUser) {
                        Color.Transparent // will have purple-blue gradient background
                    } else {
                        if (message.isError) CyberRose.copy(alpha = 0.12f) else CyberSurfaceVariant
                    },
                    border = if (isUser) {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            NeonViolet.copy(alpha = 0.5f)
                        )
                    } else {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (message.isError) CyberRose.copy(alpha = 0.5f) else CyberBorder
                        )
                    },
                    shadowElevation = if (isUser) 4.dp else 1.dp
                ) {
                    Box(
                        modifier = if (isUser) {
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF7C3AED), // Vibrant violet
                                        Color(0xFF4F46E5)  // Indigo blue
                                    )
                                )
                            )
                        } else {
                            Modifier
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Attached Image Preview
                            if (!message.imageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = message.imageUri,
                                    contentDescription = "Attached photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .padding(bottom = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Message content
                            if (message.content.isBlank() && message.isStreaming) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Thinking", color = TextSecondary, fontSize = 13.sp)
                                    DotAnimation()
                                }
                            } else {
                                FormattedMessageContent(
                                    content = message.content,
                                    isUser = isUser,
                                    isError = message.isError
                                )
                            }

                            // Grounding Sources (Google Search Web Links & Google Maps Place Links)
                            if (!isUser && !message.groundingSourcesJson.isNullOrBlank()) {
                                GroundingSourcesSection(groundingJson = message.groundingSourcesJson)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Timestamp & read ticks (matches screenshot!)
                            Row(
                                modifier = Modifier.align(Alignment.End),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = timeFormatted,
                                    fontSize = 10.sp,
                                    color = if (isUser) Color.White.copy(alpha = 0.7f) else TextMuted
                                )
                                if (isUser) {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Delivered",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Response Action Toolbar (TTS, Copy, Share, Regenerate, Like/Dislike)
                if (!isUser && !message.isStreaming && message.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(start = 2.dp)
                        ) {
                            // Copy Text
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Usman AI Response", message.content))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("copy_button_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy response",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Share Text
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, message.content)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share AI Response"))
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("share_button_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share response",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Regenerate Response
                            IconButton(
                                onClick = onRegenerate,
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("regenerate_button_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Regenerate answer",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Like Button
                            IconButton(
                                onClick = { isLiked = if (isLiked == true) null else true },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like response",
                                    tint = if (isLiked == true) NeonCyan else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Dislike Button
                            IconButton(
                                onClick = { isLiked = if (isLiked == false) null else false },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Dislike response",
                                    tint = if (isLiked == false) CyberRose else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Speak / TTS Toggle
                            IconButton(
                                onClick = { onSpeakToggle(message) },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("tts_button_${message.id}")
                            ) {
                                if (isCurrentlySpeaking) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop speaking",
                                        tint = CyberRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Read answer aloud",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            if (isCurrentlySpeaking) {
                                AudioWaveVisualizer()
                            }
                        }
                    }
                }
            }
        }

        // Suggested Follow-up Questions Chips
        if (!isUser && !message.isStreaming && followUpsList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(start = 44.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Suggested follow-ups",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    followUpsList.forEach { followUp ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onFollowUpClick(followUp) }
                                .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = CyberSurfaceVariant
                        ) {
                            Text(
                                text = followUp,
                                fontSize = 11.sp,
                                color = NeonVioletLight,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormattedMessageContent(
    content: String,
    isUser: Boolean,
    isError: Boolean
) {
    val context = LocalContext.current
    val textColor = when {
        isUser -> Color.White
        isError -> CyberRose
        else -> TextPrimary
    }

    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Split code blocks ```...``` from regular markdown text
            val blocks = remember(content) { parseMarkdownBlocks(content) }

            blocks.forEach { block ->
                when (block) {
                    is MessageBlock.Text -> {
                        Text(
                            text = block.text,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    is MessageBlock.Code -> {
                        // Render styled code card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            color = CyberCodeBackground
                        ) {
                            Column {
                                // Code header (language + copy)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F111E))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = block.language.ifBlank { "code" },
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", block.code))
                                            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy code",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = block.code,
                                    color = Color(0xFFE2E8F0),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class MessageBlock {
    data class Text(val text: String) : MessageBlock()
    data class Code(val code: String, val language: String) : MessageBlock()
}

fun parseMarkdownBlocks(content: String): List<MessageBlock> {
    val blocks = mutableListOf<MessageBlock>()
    val codeFenceRegex = "```([a-zA-Z0-9_-]*)\n([\\s\\S]*?)```".toRegex()

    var lastIndex = 0
    codeFenceRegex.findAll(content).forEach { matchResult ->
        val range = matchResult.range
        if (range.first > lastIndex) {
            val textPart = content.substring(lastIndex, range.first).trim()
            if (textPart.isNotEmpty()) {
                blocks.add(MessageBlock.Text(textPart))
            }
        }
        val language = matchResult.groupValues[1]
        val code = matchResult.groupValues[2].trimEnd()
        blocks.add(MessageBlock.Code(code = code, language = language))
        lastIndex = range.last + 1
    }

    if (lastIndex < content.length) {
        val trailing = content.substring(lastIndex).trim()
        if (trailing.isNotEmpty()) {
            blocks.add(MessageBlock.Text(trailing))
        }
    }

    if (blocks.isEmpty() && content.isNotEmpty()) {
        blocks.add(MessageBlock.Text(content))
    }

    return blocks
}

@Composable
fun DotAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = alpha1))
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonViolet.copy(alpha = 1.2f - alpha1))
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = alpha1))
        )
    }
}

@Composable
fun AudioWaveVisualizer() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.5.dp)
                .height(h1.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(NeonCyan)
        )
        Box(
            modifier = Modifier
                .width(2.5.dp)
                .height(h2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(NeonViolet)
        )
        Box(
            modifier = Modifier
                .width(2.5.dp)
                .height(h3.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(NeonCyan)
        )
    }
}

@Composable
fun GroundingSourcesSection(groundingJson: String) {
    val context = LocalContext.current
    val sources = remember(groundingJson) {
        try {
            val jsonArray = JSONArray(groundingJson)
            val list = mutableListOf<Pair<String, String>>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val title = obj.optString("title").ifBlank { "Source ${i + 1}" }
                val uri = obj.optString("uri").ifBlank { "" }
                list.add(Pair(title, uri))
            }
            list
        } catch (e: Exception) {
            emptyList<Pair<String, String>>()
        }
    }

    if (sources.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyberSurfaceHighlight)
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "🌐 Grounded Sources & Citations",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            sources.forEach { (title, url) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(enabled = url.isNotBlank()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $title",
                        fontSize = 11.sp,
                        color = if (url.isNotBlank()) NeonVioletLight else TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
