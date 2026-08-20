package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class FeatureCardItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val prompt: String,
    val iconColor: Color
)

@Composable
fun HomeFeatureGrid(
    onSelectFeature: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val features = listOf(
        FeatureCardItem(
            id = "ask_anything",
            icon = Icons.Default.ChatBubbleOutline,
            title = "Ask anything",
            subtitle = "Get answers to any question",
            prompt = "What are the most exciting breakthroughs in AI and technology this year?",
            iconColor = NeonCyan
        ),
        FeatureCardItem(
            id = "help_me_code",
            icon = Icons.Default.Code,
            title = "Help me code",
            subtitle = "Write, debug, and explain code",
            prompt = "Write a clean Kotlin Jetpack Compose composable with smooth animated state transitions.",
            iconColor = NeonViolet
        ),
        FeatureCardItem(
            id = "explain_something",
            icon = Icons.Default.Description,
            title = "Explain something",
            subtitle = "Understand any topic in simple terms",
            prompt = "Explain quantum computing in simple terms with an intuitive analogy.",
            iconColor = Color(0xFFF59E0B) // Warm Amber
        ),
        FeatureCardItem(
            id = "create_idea",
            icon = Icons.Default.Lightbulb,
            title = "Create an idea",
            subtitle = "Brainstorm and get creative",
            prompt = "Brainstorm 5 innovative startup ideas that combine mobile apps with edge AI.",
            iconColor = Color(0xFF10B981) // Emerald
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Greeting Header (matches screenshot 1!)
        Text(
            text = "Hello, I'm Usman AI 👋",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD8B4FE), // Soft bright violet
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "How can I help you today?",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(26.dp))

        // 2x2 Grid Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeatureCard(
                item = features[0],
                onClick = { onSelectFeature(features[0].prompt) },
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                item = features[1],
                onClick = { onSelectFeature(features[1].prompt) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeatureCard(
                item = features[2],
                onClick = { onSelectFeature(features[2].prompt) },
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                item = features[3],
                onClick = { onSelectFeature(features[3].prompt) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(
    item: FeatureCardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2E1065).copy(alpha = 0.6f),
                        CyberBorder
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .testTag("feature_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF131422) // Deep dark card fill
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon in rounded container
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1F35)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
