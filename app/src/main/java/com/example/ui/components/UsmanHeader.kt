package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun UsmanHeader(
    conversationTitle: String,
    onOpenProfile: () -> Unit,
    onNewChat: () -> Unit,
    onOpenLiveVoice: () -> Unit,
    onClearCurrentChat: () -> Unit,
    onRenameChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CyberSurface.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(NeonViolet.copy(alpha = 0.25f), NeonCyan.copy(alpha = 0.25f))
            )
        ),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo & App Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(NeonViolet, NeonCyan)),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.usman_ai_logo_1787107572616),
                        contentDescription = "Usman AI Logo",
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Usman AI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberEmerald)
                        )
                    }
                    if (conversationTitle.isNotBlank() && conversationTitle != "New Conversation") {
                        Text(
                            text = conversationTitle,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right: Live Voice Button, New Chat (+), Options, and Profile Avatar Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live Voice Button (Gemini Live API)
                IconButton(
                    onClick = onOpenLiveVoice,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_live_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Live Voice Conversation",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat",
                        tint = NeonViolet,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename Chat", color = TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                menuExpanded = false
                                onRenameChat()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Chat", color = CyberRose, fontSize = 13.sp) },
                            onClick = {
                                menuExpanded = false
                                onClearCurrentChat()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = CyberRose, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }

                // Profile Avatar Icon Button
                IconButton(
                    onClick = onOpenProfile,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_profile_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, NeonViolet.copy(alpha = 0.6f), CircleShape)
                            .background(Color(0xFF2E1065)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = "Profile",
                            tint = Color(0xFFD8B4FE),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
