package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuickPrompt(
    val icon: ImageVector,
    val title: String,
    val prompt: String,
    val accentColor: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickPromptSuggestions(
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        QuickPrompt(
            icon = Icons.Default.Lightbulb,
            title = "Explain Quantum Computing",
            prompt = "Explain quantum computing in simple terms with a real-world analogy.",
            accentColor = NeonCyan
        ),
        QuickPrompt(
            icon = Icons.Default.Code,
            title = "Write Kotlin Compose Code",
            prompt = "Write a modern Jetpack Compose glowing futuristic card component in Kotlin.",
            accentColor = NeonViolet
        ),
        QuickPrompt(
            icon = Icons.Default.Create,
            title = "Draft Product Launch Post",
            prompt = "Draft an engaging LinkedIn announcement for launching an intelligent AI assistant app.",
            accentColor = CyberEmerald
        ),
        QuickPrompt(
            icon = Icons.Default.Psychology,
            title = "Brainstorm 5 App Ideas",
            prompt = "Brainstorm 5 high-impact, futuristic mobile application ideas powered by modern AI.",
            accentColor = CyberRose
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SUGGESTED CAPABILITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            prompts.forEachIndexed { index, item ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectPrompt(item.prompt) }
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(item.accentColor.copy(alpha = 0.4f), CyberBorder)
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .testTag("prompt_suggestion_$index"),
                    shape = RoundedCornerShape(14.dp),
                    color = CyberSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(item.accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
