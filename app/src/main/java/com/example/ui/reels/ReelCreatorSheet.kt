package com.example.ui.reels

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiService
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelCreatorSheet(
    onPostReel: (videoDrawable: String, audioTitle: String, audioArtist: String, caption: String, filterName: String, isCollab: Boolean, coAuthorName: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val clips = listOf(
        Pair("reel_cyber_synth_1790201507030", "Cyber Kinetic"),
        Pair("reel_neon_oasis_1790201522014", "Bioluminescent"),
        Pair("post_crystal_city_1790200645179", "Crystal Speed")
    )
    var selectedClip by remember { mutableStateOf(clips.first().first) }

    val audioTracks = listOf(
        Pair("Cyber Kinetic Pulse (Spatial Mix)", "Marcus Vance ft. Nova"),
        Pair("432Hz Bioluminescent Dreams", "Sarah Chen"),
        Pair("Obsidian Skyline Resonance", "Marcus Vance"),
        Pair("Quantum Echo Synthesizer", "Nova Sterling")
    )
    var selectedAudio by remember { mutableStateOf(audioTracks.first()) }

    val filters = listOf("Neon Glitch", "Prism Glow", "Cyber Noir", "Hologram Matrix")
    var selectedFilter by remember { mutableStateOf(filters.first()) }

    var caption by remember { mutableStateOf("") }
    var isCollaborative by remember { mutableStateOf(true) }
    var isGeneratingCaption by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = AuraDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(scrollState)
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AuraHotPink, AuraNeonViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Create Aura Reel / Pulse",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Text(
                text = "Capture volumetric visual streams and share synchronized vertical video across the neural graph.",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // Preview & Visual Clip Selector
            Text(
                text = "1. Select Visual Sequence:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(clips) { (drawableName, label) ->
                    val isSelected = selectedClip == drawableName
                    val resId = rememberDrawableResId(context, drawableName)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(86.dp)
                            .clickable { selectedClip = drawableName }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(9f / 16f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AuraHotPink else Color(0xFF25344F),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraHotPink else Color.LightGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Track Selector
            Text(
                text = "2. Synced Spatial Soundtrack:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(8.dp)
            ) {
                audioTracks.forEach { (title, artist) ->
                    val isSelected = selectedAudio.first == title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AuraNeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { selectedAudio = Pair(title, artist) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isSelected) AuraNeonCyan else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.LightGray
                            )
                            Text(
                                text = artist,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Shaders / Filters
            Text(
                text = "3. Real-Time Neural Filter:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { f ->
                    val isSelected = selectedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AuraNeonViolet.copy(alpha = 0.25f) else AuraDarkCard)
                            .border(1.dp, if (isSelected) AuraNeonViolet else Color(0xFF25344F), RoundedCornerShape(14.dp))
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Caption & Hashtags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. Caption & Tags:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Button(
                    onClick = {
                        scope.launch {
                            isGeneratingCaption = true
                            val generated = GeminiService.generateCaptionIdeas("${selectedAudio.first} ${selectedFilter}")
                            caption = generated
                            isGeneratingCaption = false
                        }
                    },
                    enabled = !isGeneratingCaption,
                    colors = ButtonDefaults.buttonColors(containerColor = AuraNeonViolet, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    if (isGeneratingCaption) {
                        CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                        Text("Gemini AI Caption", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Write reel caption with #hashtags...", fontSize = 12.sp, color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .testTag("input_reel_caption"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraHotPink,
                    unfocusedBorderColor = Color(0xFF28354D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Co-Author Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = AuraNeonCyan, modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = "Co-Author with Elena Rostova", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Shares reel royalties and displays dual creator badges", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                Switch(
                    checked = isCollaborative,
                    onCheckedChange = { isCollaborative = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AuraNeonCyan, checkedTrackColor = AuraNeonCyan.copy(alpha = 0.4f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Reel Button
            Button(
                onClick = {
                    onPostReel(
                        selectedClip,
                        selectedAudio.first,
                        selectedAudio.second,
                        caption.ifBlank { "Volumetric creative capture from Aura Reels Studio! ✦" },
                        selectedFilter,
                        isCollaborative,
                        if (isCollaborative) "Elena Rostova" else null
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_reel_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuraHotPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = "Post Reel Live to Feed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
