package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.api.GeminiModelNames
import com.example.data.repository.AiStudioRepository
import com.example.data.repository.GeneratedImageResult
import com.example.data.repository.GeneratedMusicResult
import com.example.data.repository.GeneratedVideoResult
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
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
import kotlinx.coroutines.launch
import java.io.InputStream

enum class StudioTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    IMAGE("Image Studio", Icons.Default.Image),
    VIDEO("Veo Video", Icons.Default.Movie),
    MUSIC("Lyria Music", Icons.Default.MusicNote),
    GROUNDING("Maps & Search", Icons.Default.Map)
}

@Composable
fun StudioScreen(
    aiStudioRepository: AiStudioRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(StudioTab.IMAGE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
    ) {
        // Studio Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Usman AI Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Gemini 3 Pro, Veo & Lyria Engine",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }
            }
        }

        // Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StudioTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { currentTab = tab }
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else CyberBorder,
                            RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CyberSurfaceHighlight else CyberSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) NeonCyan else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                StudioTab.IMAGE -> ImageStudioSection(aiStudioRepository)
                StudioTab.VIDEO -> VeoVideoSection(aiStudioRepository)
                StudioTab.MUSIC -> LyriaMusicSection(aiStudioRepository)
                StudioTab.GROUNDING -> GroundingExplorerSection(aiStudioRepository)
            }
        }
    }
}

// ----------------------------------------------------
// 1. IMAGE STUDIO (gemini-3-pro-image-preview & gemini-3.1-flash-image-preview)
// ----------------------------------------------------
@Composable
fun ImageStudioSection(repository: AiStudioRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf(GeminiModelNames.GEMINI_3_PRO_IMAGE) }
    var selectedSize by remember { mutableStateOf("1K") } // 1K, 2K, 4K
    var selectedRatio by remember { mutableStateOf("1:1") } // 1:1, 16:9, 9:16, 4:3, 3:4
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedBase64 by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<GeneratedImageResult?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uploadedImageUri = uri
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    uploadedBase64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Model Selection Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Model", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    GeminiModelNames.GEMINI_3_PRO_IMAGE to "Gemini 3 Pro",
                    GeminiModelNames.GEMINI_3_1_FLASH_IMAGE to "Flash 3.1"
                ).forEach { (model, label) ->
                    val isSel = selectedModel == model
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedModel = model }
                            .border(1.dp, if (isSel) NeonViolet else CyberBorder, RoundedCornerShape(12.dp)),
                        color = if (isSel) NeonViolet.copy(alpha = 0.2f) else CyberSurfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (isSel) NeonVioletLight else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image Size Selector (1K, 2K, 4K)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Resolution", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("1K", "2K", "4K").forEach { size ->
                    val isSel = selectedSize == size
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedSize = size }
                            .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(8.dp)),
                        color = if (isSel) NeonCyan.copy(alpha = 0.18f) else CyberSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = size,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) NeonCyan else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Aspect Ratio Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aspect Ratio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("1:1", "16:9", "9:16", "4:3").forEach { ratio ->
                    val isSel = selectedRatio == ratio
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedRatio = ratio }
                            .border(1.dp, if (isSel) NeonViolet else CyberBorder, RoundedCornerShape(8.dp)),
                        color = if (isSel) NeonViolet.copy(alpha = 0.2f) else CyberSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ratio,
                            fontSize = 11.sp,
                            color = if (isSel) NeonVioletLight else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Image Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("A futuristic cyber city illuminated with neon purple aurora...", color = TextMuted, fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CyberSurfaceVariant,
                unfocusedContainerColor = CyberSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Optional Uploaded Image for Editing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberSurfaceVariant, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add image to edit", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uploadedImageUri != null) "Photo attached (Editing mode)" else "Upload photo to edit / restyle",
                    fontSize = 12.sp,
                    color = if (uploadedImageUri != null) NeonCyan else TextSecondary
                )
            }

            if (uploadedImageUri != null) {
                IconButton(
                    onClick = {
                        uploadedImageUri = null
                        uploadedBase64 = null
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = CyberRose, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generate / Edit Button
        Button(
            onClick = {
                if (prompt.isBlank()) {
                    Toast.makeText(context, "Please enter an image prompt", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isGenerating = true
                scope.launch {
                    val result = repository.generateOrEditImage(
                        prompt = prompt,
                        modelName = selectedModel,
                        imageSize = selectedSize,
                        aspectRatio = selectedRatio,
                        sourceImageBase64 = uploadedBase64
                    )
                    isGenerating = false
                    if (result.isSuccess) {
                        generatedResult = result.getOrNull()
                        Toast.makeText(context, "Image generated ($selectedSize)", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_image_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing $selectedSize Image...", color = Color.White)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uploadedImageUri != null) "Transform & Edit Image" else "Generate $selectedSize Image",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Card Preview
        if (generatedResult != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = CyberSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (generatedResult?.localUri != null) {
                        AsyncImage(
                            model = generatedResult?.localUri,
                            contentDescription = "Generated AI Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Brush.linearGradient(listOf(Color(0xFF221340), Color(0xFF0D1B36)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("High-Quality Image Synthesized", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${generatedResult?.resolution} • ${generatedResult?.aspectRatio}", color = NeonVioletLight, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = generatedResult?.prompt ?: "",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Save", tint = NeonCyan)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// 2. VEO VIDEO SECTION (veo-3.1-fast-generate-preview)
// ----------------------------------------------------
@Composable
fun VeoVideoSection(repository: AiStudioRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var selectedRatio by remember { mutableStateOf("16:9") } // 16:9 landscape or 9:16 portrait
    var uploadedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedPhotoBase64 by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var videoResult by remember { mutableStateOf<GeneratedVideoResult?>(null) }
    var isPlaying by remember { mutableStateOf(true) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uploadedPhotoUri = uri
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    uploadedPhotoBase64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Veo Model Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF19142E),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = NeonVioletLight, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Veo 3.1 Fast Video", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Text("Animate photos into cinematic video clips (16:9 & 9:16)", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Aspect Ratio Selector (16:9 Landscape vs 9:16 Portrait)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Video Aspect Ratio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("16:9" to "Landscape (16:9)", "9:16" to "Portrait (9:16)").forEach { (ratio, label) ->
                    val isSel = selectedRatio == ratio
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedRatio = ratio }
                            .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(8.dp)),
                        color = if (isSel) NeonCyan.copy(alpha = 0.18f) else CyberSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) NeonCyan else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prompt input
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("A camera tracking shot of a robotic eagle soaring over cyberpunk mountains...", color = TextMuted, fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonViolet,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CyberSurfaceVariant,
                unfocusedContainerColor = CyberSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Upload Photo for Photo-to-Video
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { photoPickerLauncher.launch("image/*") }
                .border(1.dp, if (uploadedPhotoUri != null) NeonCyan else CyberBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = CyberSurfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = if (uploadedPhotoUri != null) NeonCyan else TextSecondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (uploadedPhotoUri != null) "Photo attached for Veo Animation" else "Upload photo to animate (Optional)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uploadedPhotoUri != null) NeonCyan else TextPrimary
                        )
                        Text("Veo turns still portraits and landscapes into moving scenes", fontSize = 10.sp, color = TextMuted)
                    }
                }
                if (uploadedPhotoUri != null) {
                    Icon(Icons.Default.Check, contentDescription = "Attached", tint = CyberEmerald, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generate Video Button
        Button(
            onClick = {
                if (prompt.isBlank() && uploadedPhotoUri == null) {
                    Toast.makeText(context, "Please enter a prompt or attach a photo", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isGenerating = true
                scope.launch {
                    val result = repository.generateVideo(
                        prompt = prompt.ifBlank { "Cinematic animation of attached photo" },
                        aspectRatio = selectedRatio,
                        sourcePhotoBase64 = uploadedPhotoBase64
                    )
                    isGenerating = false
                    if (result.isSuccess) {
                        videoResult = result.getOrNull()
                        Toast.makeText(context, "Veo Video generated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_video_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rendering Veo Video ($selectedRatio)...", color = Color.White)
            } else {
                Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Veo Video", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Video Player Preview
        if (videoResult != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonViolet, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1122)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (selectedRatio == "9:16") 320.dp else 190.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF1E1038), Color(0xFF0B1933)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = NeonCyan,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { isPlaying = !isPlaying }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Veo 3.1 Cinematic Preview ($selectedRatio)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("0:06 / 0:06 • 1080p HD", color = NeonCyan, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(videoResult?.videoTitle ?: "", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Veo Fast Video Engine", fontSize = 11.sp, color = TextSecondary)
                        }
                        IconButton(onClick = { Toast.makeText(context, "Video exported to device", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = NeonCyan)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// 3. LYRIA MUSIC STUDIO (lyria-3-clip-preview & lyria-3-pro-preview)
// ----------------------------------------------------
@Composable
fun LyriaMusicSection(repository: AiStudioRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Cyberpunk Synthwave") }
    var selectedDuration by remember { mutableIntStateOf(30) } // 10s, 20s, 30s
    var isProTrack by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var musicResult by remember { mutableStateOf<GeneratedMusicResult?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val genres = listOf("Cyberpunk Synthwave", "Lo-Fi Chill Beat", "Cinematic Orchestral", "Futuristic Bass", "Ambient Meditation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131E2E),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Lyria 3 Music Studio", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                    Text("Synthesize custom AI music tracks & short clips up to 30s", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Genre Selector Chips
        Text("Music Genre / Vibe", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                val isSel = selectedGenre == genre
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedGenre = genre }
                        .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) NeonCyan.copy(alpha = 0.18f) else CyberSurfaceVariant
                ) {
                    Text(
                        text = genre,
                        fontSize = 11.sp,
                        color = if (isSel) NeonCyan else TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Duration Slider (10s to 30s)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Clip Duration", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("${selectedDuration}s (Lyria 3 Clip)", fontSize = 12.sp, color = CyberEmerald, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = selectedDuration.toFloat(),
            onValueChange = { selectedDuration = it.toInt() },
            valueRange = 10f..30f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = CyberEmerald,
                activeTrackColor = CyberEmerald,
                inactiveTrackColor = CyberBorder
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Heavy retro synth lead with atmospheric neon bassline...", color = TextMuted, fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberEmerald,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CyberSurfaceVariant,
                unfocusedContainerColor = CyberSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Generate Music Button
        Button(
            onClick = {
                if (prompt.isBlank()) {
                    prompt = "$selectedGenre background soundtrack"
                }
                isGenerating = true
                scope.launch {
                    val result = repository.generateMusic(
                        prompt = prompt,
                        isProFullLength = isProTrack,
                        durationSeconds = selectedDuration,
                        genre = selectedGenre
                    )
                    isGenerating = false
                    if (result.isSuccess) {
                        musicResult = result.getOrNull()
                        isPlaying = true
                        Toast.makeText(context, "Lyria 3 Track synthesized!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_music_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing Lyria Audio (${selectedDuration}s)...", color = Color.White)
            } else {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Music Track", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Audio Visualizer Card
        if (musicResult != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberEmerald, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0D1C18)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(musicResult?.trackTitle ?: "Usman AI Track", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${musicResult?.genre} • ${musicResult?.audioDurationSeconds}s Clip", fontSize = 11.sp, color = CyberEmerald)
                        }
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyberEmerald)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Waveform Bar Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val samples = musicResult?.waveformSamples ?: List(30) { 0.5f }
                        samples.forEach { sample ->
                            val heightRatio = if (isPlaying) sample else sample * 0.3f
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height((heightRatio * 44).coerceAtLeast(4f).dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isPlaying) NeonCyan else TextMuted)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// 4. GROUNDING EXPLORER (Google Maps & Search Grounding)
// ----------------------------------------------------
@Composable
fun GroundingExplorerSection(repository: AiStudioRepository) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var groundingMode by remember { mutableStateOf("Maps") } // "Maps" or "Search"

    val sampleMapsPlaces = listOf(
        Triple("Golden Gate Bridge", "San Francisco, CA 94129", "4.8 ★ (120,400 reviews)"),
        Triple("The Louvre Museum", "Rue de Rivoli, 75001 Paris, France", "4.7 ★ (310,200 reviews)"),
        Triple("Tokyo Skytree", "1 Chome-1-2 Oshiage, Sumida City, Tokyo", "4.5 ★ (88,300 reviews)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Grounding Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Maps" to "📍 Google Maps Grounding", "Search" to "🌐 Google Search Grounding").forEach { (mode, title) ->
                val isSel = groundingMode == mode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { groundingMode = mode }
                        .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(12.dp)),
                    color = if (isSel) NeonCyan.copy(alpha = 0.18f) else CyberSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) NeonCyan else TextSecondary,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (groundingMode == "Maps") "Maps Grounding delivers live place data, accurate coordinates, ratings, and directions powered by Gemini 3.5 Flash."
            else "Search Grounding connects Usman AI directly to live Google Search indices for real-time web citations and news.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Featured Grounded Places", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        sampleMapsPlaces.forEach { (name, address, rating) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(name)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        context.startActivity(mapIntent)
                    }
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Text(address, fontSize = 11.sp, color = TextSecondary)
                        Text(rating, fontSize = 10.sp, color = CyberEmerald, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(Icons.Default.Map, contentDescription = "Open Map", tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
