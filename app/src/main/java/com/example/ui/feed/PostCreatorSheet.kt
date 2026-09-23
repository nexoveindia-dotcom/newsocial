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
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
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
import com.example.ui.components.AuraAvatar
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreatorSheet(
    onDismiss: () -> Unit,
    onPublishPost: (imageRes: String, caption: String, vibe: String, coAuthors: List<String>, isCipherGuildOnly: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val availableImages = listOf(
        "post_crystal_city_1790200645179" to "Crystal Megacity",
        "post_aurora_vibes_1790200680892" to "Aurora Bioluminescence",
        "reel_cyber_synth_1790201507030" to "Cyber Kinetic Synth",
        "reel_neon_oasis_1790201522014" to "Neon Oasis",
        "post_cyber_creator_1790200662289" to "Neural Alchemist"
    )

    val vibeTags = listOf("Cyberpunk", "Neo-Chill", "Quantum Tech", "Ambient Art", "Deep Lore")
    val coAuthorCandidates = listOf(
        "Elena Rostova" to "post_cyber_creator_1790200662289",
        "Marcus Vance" to "post_crystal_city_1790200645179",
        "Sarah Chen" to "post_aurora_vibes_1790200680892"
    )

    var selectedImage by remember { mutableStateOf(availableImages.first().first) }
    var captionText by remember { mutableStateOf("Co-synthesizing neural aesthetic structures across distributed quantum networks. ✦") }
    var selectedVibe by remember { mutableStateOf("Quantum Tech") }
    var selectedCoAuthors by remember { mutableStateOf(setOf<String>()) }
    var isCipherGuildOnly by remember { mutableStateOf(false) }
    var isGeneratingAiCaption by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("post_creator_bottom_sheet")
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AuraNeonCyan, Color(0xFF3B82F6)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "NEW FEED POST",
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

            // Post Visual Selector
            Text(
                text = "Select Digital Artwork",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(availableImages) { (drawableName, label) ->
                    val isSelected = selectedImage == drawableName
                    val context = LocalContext.current
                    val resId = rememberDrawableResId(context, drawableName)
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AuraNeonCyan else Color(0xFF223048),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedImage = drawableName }
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

            // Caption Area with Gemini AI Generate Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post Caption & Lore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )

                // AI Spark Caption Button
                Button(
                    onClick = {
                        scope.launch {
                            isGeneratingAiCaption = true
                            val generated = GeminiService.generateCaptionIdeas(selectedVibe)
                            captionText = generated
                            isGeneratingAiCaption = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraDarkCard,
                        contentColor = AuraNeonCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("ai_spark_caption_button")
                ) {
                    if (isGeneratingAiCaption) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = AuraNeonCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "AI Caption", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = { Text("Write lore or description...", color = Color.Gray, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_caption_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraNeonCyan,
                    unfocusedBorderColor = Color(0xFF22304A),
                    focusedContainerColor = AuraDarkCard,
                    unfocusedContainerColor = AuraDarkCard,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Vibe Tags
            Text(
                text = "Vibe Categorization",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vibeTags) { vibe ->
                    val isSelected = selectedVibe == vibe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) AuraNeonCyan.copy(alpha = 0.25f) else AuraDarkCard)
                            .border(
                                1.dp,
                                if (isSelected) AuraNeonCyan else Color(0xFF22304A),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedVibe = vibe }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = vibe,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraNeonCyan else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Collaborative Co-Authors
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GroupAdd,
                    contentDescription = null,
                    tint = AuraHotPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Collaborative Co-Authors (Split Attribution)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                coAuthorCandidates.forEach { (name, avatarRes) ->
                    val isChecked = selectedCoAuthors.contains(name)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isChecked) AuraHotPink.copy(alpha = 0.2f) else AuraDarkCard)
                            .border(
                                1.dp,
                                if (isChecked) AuraHotPink else Color(0xFF22304A),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedCoAuthors = if (isChecked) {
                                    selectedCoAuthors - name
                                } else {
                                    selectedCoAuthors + name
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AuraAvatar(drawableName = avatarRes, size = 20.dp, isVerified = false)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = name.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChecked) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Toggle: Public Feed vs Cipher Guild
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AuraDarkCard)
                    .border(1.dp, Color(0xFF22304A), RoundedCornerShape(14.dp))
                .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isCipherGuildOnly) AuraEmerald.copy(alpha = 0.2f) else AuraNeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCipherGuildOnly) Icons.Default.Lock else Icons.Default.Public,
                            contentDescription = null,
                            tint = if (isCipherGuildOnly) AuraEmerald else AuraNeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isCipherGuildOnly) "Cipher Guild Only (E2EE)" else "Public Neural Stream",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isCipherGuildOnly) "Restricted to verified enclave members" else "Visible to all followers & discovery",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Switch(
                    checked = isCipherGuildOnly,
                    onCheckedChange = { isCipherGuildOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AuraEmerald,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF1E293B)
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Publish Button
            Button(
                onClick = {
                    onPublishPost(
                        selectedImage,
                        captionText,
                        selectedVibe,
                        selectedCoAuthors.toList(),
                        isCipherGuildOnly
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_post_confirm_button"),
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
                            Brush.linearGradient(listOf(AuraNeonCyan, Color(0xFF3B82F6), AuraNeonViolet))
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
                            text = "Publish to Feed",
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
