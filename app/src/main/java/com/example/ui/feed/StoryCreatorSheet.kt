package com.example.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryCreatorSheet(
    onDismiss: () -> Unit,
    onPostStory: (imageDrawable: String, caption: String, filterName: String, musicTrack: String?, stickerText: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val backgroundOptions = listOf(
        "post_crystal_city_1790200645179" to "Crystal City",
        "post_aurora_vibes_1790200680892" to "Aurora Oasis",
        "reel_cyber_synth_1790201507030" to "Cyber Synth",
        "reel_neon_oasis_1790201522014" to "Neon Oasis",
        "post_cyber_creator_1790200662289" to "Neural Avatar"
    )

    val filterOptions = listOf("Normal", "Neon Glitch", "Prism Glow", "Cyber Noir", "Matrix")
    val stickerPresets = listOf("✦ QUANTUM VIBE", "432Hz HARMONIC", "🔴 LIVE DROP", "⚡ CYPHER VAULT", "NEURAL ART")
    val musicTracks = listOf(
        "Obsidian Skyline Resonance",
        "432Hz Bioluminescent Dreams",
        "Cyber Kinetic Pulse (Spatial Mix)",
        "Ambient Sub-bass Frequency"
    )

    var selectedBg by remember { mutableStateOf(backgroundOptions.first().first) }
    var selectedFilter by remember { mutableStateOf("Neon Glitch") }
    var captionText by remember { mutableStateOf("Capturing late night cyber aesthetics in Aura Studio ✦") }
    var selectedSticker by remember { mutableStateOf("✦ QUANTUM VIBE") }
    var selectedMusic by remember { mutableStateOf(musicTracks.first()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("story_creator_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AuraNeonViolet, AuraHotPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "NEW 24H STORY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Story Preview Card (9:16 aspect ratio mini preview)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AuraDarkBackground)
                    .border(1.5.dp, AuraNeonCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            ) {
                val context = LocalContext.current
                val resId = rememberDrawableResId(context, selectedBg)
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Story Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Filter Overlay simulation
                val filterBrush = when (selectedFilter) {
                    "Neon Glitch" -> Brush.verticalGradient(
                        listOf(AuraHotPink.copy(alpha = 0.35f), AuraNeonCyan.copy(alpha = 0.25f))
                    )
                    "Prism Glow" -> Brush.radialGradient(
                        listOf(AuraNeonViolet.copy(alpha = 0.45f), Color.Transparent)
                    )
                    "Cyber Noir" -> Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
                    )
                    "Matrix" -> Brush.verticalGradient(
                        listOf(AuraEmerald.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.3f))
                    )
                    else -> Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                    )
                }
                Box(modifier = Modifier.fillMaxSize().background(filterBrush))

                // Overlays on top of Story
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top story tags: filter & sticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, AuraNeonCyan, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedSticker,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraNeonCyan
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AuraHotPink.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = selectedFilter,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Bottom: Caption & Music track
                    Column {
                        if (captionText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = captionText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = AuraEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedMusic,
                                fontSize = 10.sp,
                                color = AuraEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Asset Selector
            Text(
                text = "Select Visual Canvas",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(backgroundOptions) { (drawableName, label) ->
                    val isSelected = selectedBg == drawableName
                    val context = LocalContext.current
                    val resId = rememberDrawableResId(context, drawableName)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AuraNeonCyan else Color(0xFF223048),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedBg = drawableName }
                    ) {
                        if (resId != 0) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(AuraNeonCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Neural Shaders / Filter selection
            Text(
                text = "Neural Shader / Filter",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AuraHotPink.copy(alpha = 0.25f) else AuraDarkCard)
                            .border(
                                1.dp,
                                if (isSelected) AuraHotPink else Color(0xFF22304A),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sticker Selector
            Text(
                text = "Story Sticker / Vibe",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(stickerPresets) { sticker ->
                    val isSelected = selectedSticker == sticker
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AuraNeonCyan.copy(alpha = 0.25f) else AuraDarkCard)
                            .border(
                                1.dp,
                                if (isSelected) AuraNeonCyan else Color(0xFF22304A),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedSticker = sticker }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sticker,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraNeonCyan else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Story Caption Input
            Text(
                text = "Story Caption",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = { Text("What's on your neural mind?", color = Color.Gray, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("story_caption_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraNeonCyan,
                    unfocusedBorderColor = Color(0xFF22304A),
                    focusedContainerColor = AuraDarkCard,
                    unfocusedContainerColor = AuraDarkCard,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Publish Button
            Button(
                onClick = {
                    onPostStory(
                        selectedBg,
                        captionText,
                        selectedFilter,
                        selectedMusic,
                        selectedSticker
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_story_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(AuraNeonViolet, AuraHotPink, AuraNeonCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share to Your Story (24h)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
