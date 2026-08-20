package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
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
import com.example.ui.theme.CyberBackground
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

data class ExploreCategory(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val prompts: List<ExplorePrompt>
)

data class ExplorePrompt(
    val title: String,
    val promptText: String
)

@Composable
fun ExploreScreen(
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        ExploreCategory(
            title = "Maps & Real-World Navigation",
            description = "Google Maps Grounding with Gemini 3.5 Flash",
            icon = Icons.Default.Map,
            iconColor = CyberEmerald,
            prompts = listOf(
                ExplorePrompt("Find Top Cafes", "Find the highest-rated specialty coffee shops near downtown with opening hours and reviews."),
                ExplorePrompt("Tokyo 3-Day Itinerary", "Plan a realistic 3-day travel itinerary in Tokyo with specific metro routes and landmarks."),
                ExplorePrompt("National Parks Guide", "What are the must-see trails and viewpoints in Yosemite National Park?")
            )
        ),
        ExploreCategory(
            title = "Search Grounding & Live Facts",
            description = "Google Search Grounding for real-time citations",
            icon = Icons.Default.Search,
            iconColor = NeonCyan,
            prompts = listOf(
                ExplorePrompt("Latest Tech Breakthroughs", "What are the latest verified developments in quantum computing and AI hardware this year?"),
                ExplorePrompt("Financial Market Analysis", "Summarize recent trends in semiconductor manufacturing with sources."),
                ExplorePrompt("Space Exploration News", "What are the upcoming space missions and telescope discoveries scheduled this year?")
            )
        ),
        ExploreCategory(
            title = "Coding & Engineering (Gemini 3.1 Pro)",
            description = "Kotlin, Jetpack Compose, debugging, algorithms & architecture",
            icon = Icons.Default.Code,
            iconColor = NeonViolet,
            prompts = listOf(
                ExplorePrompt("Compose Architecture", "Show me how to structure a clean MVI architecture with StateFlow in Jetpack Compose."),
                ExplorePrompt("Coroutines & Flow", "Explain Kotlin Coroutines SharedFlow vs StateFlow with practical real-world examples."),
                ExplorePrompt("Optimize Performance", "What are the best practices to reduce unnecessary recomposition in Jetpack Compose?")
            )
        ),
        ExploreCategory(
            title = "Education & Mathematics",
            description = "Step-by-step problem solving, science, biology & physics",
            icon = Icons.Default.School,
            iconColor = Color(0xFF38BDF8),
            prompts = listOf(
                ExplorePrompt("Quantum Computing", "Explain quantum superposition and entanglement in simple terms."),
                ExplorePrompt("Step-by-Step Calculus", "Show step-by-step how to find the derivative of f(x) = x^3 * e^(2x)."),
                ExplorePrompt("Human Biology", "Explain how human cellular respiration produces ATP in mitochondria.")
            )
        ),
        ExploreCategory(
            title = "Creative & Multi-Modal Studio",
            description = "Prompts for Gemini 3 Pro Images, Veo Video & Lyria Music",
            icon = Icons.Default.AutoAwesome,
            iconColor = Color(0xFFA855F7),
            prompts = listOf(
                ExplorePrompt("4K Cyberpunk City", "A hyper-detailed 4K isometric cyberpunk metropolis with neon purple rain and flying vehicles."),
                ExplorePrompt("Cinematic Drone Video", "A slow tracking drone shot revealing ancient mystical ruins surrounded by glowing flora."),
                ExplorePrompt("Synthwave Soundtrack", "An energetic 80s synthwave beat with pulsating basslines and retro synth leads.")
            )
        ),
        ExploreCategory(
            title = "Writing & Summarization",
            description = "Essays, professional emails, summaries, and copy",
            icon = Icons.Default.EditNote,
            iconColor = Color(0xFFF59E0B),
            prompts = listOf(
                ExplorePrompt("Executive Summary", "Summarize the key principles of modern clean architecture for Android into 5 concise bullet points."),
                ExplorePrompt("Professional Email", "Draft a polite and persuasive proposal email to a client requesting feedback on a product design."),
                ExplorePrompt("Proofread & Enhance", "Proofread and polish this text to make it articulate, engaging, and professional.")
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CyberSurface.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explore Usman AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Discover intelligent prompts, Grounding tools, and Studio capabilities",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Category Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                ExploreCategoryCard(
                    category = category,
                    onSelectPrompt = onSelectPrompt
                )
            }
        }
    }
}

@Composable
fun ExploreCategoryCard(
    category: ExploreCategory,
    onSelectPrompt: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.linearGradient(listOf(CyberBorder, CyberBorder)),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF131422)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(category.iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = category.iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = category.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = category.description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            category.prompts.forEach { promptItem ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectPrompt(promptItem.promptText) }
                        .border(
                            1.dp,
                            NeonViolet.copy(alpha = 0.25f),
                            RoundedCornerShape(10.dp)
                        )
                        .testTag("explore_prompt_${promptItem.title.replace(" ", "_").lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    color = CyberSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = promptItem.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                        Text(
                            text = "Try →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonViolet
                        )
                    }
                }
            }
        }
    }
}
