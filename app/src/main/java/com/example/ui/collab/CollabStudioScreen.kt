package com.example.ui.collab

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CanvasLayer
import com.example.data.model.LayerType
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@Composable
fun CollabStudioScreen(viewModel: SocialViewModel) {
    val session by viewModel.collabSession.collectAsStateWithLifecycle()
    val isGeneratingCaption by viewModel.isGeneratingCaption.collectAsStateWithLifecycle()
    val aiGeneratedCaption by viewModel.aiGeneratedCaption.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedLayerId by remember { mutableStateOf(session.layers.firstOrNull()?.id ?: "") }
    var editingTextValue by remember { mutableStateOf("") }
    var finalPostCaption by remember { mutableStateOf("") }

    val context = LocalContext.current
    val imageResId = rememberDrawableResId(context, session.baseImageResName)

    val infiniteTransition = rememberInfiniteTransition(label = "voice_wave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_wave_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("collab_studio_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Studio Header
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AURA STUDIO",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AuraEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LIVE SYNC",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraEmerald
                                )
                            }
                        }
                        Text(
                            text = session.title,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Voice Huddle Toggle
                    Button(
                        onClick = { viewModel.toggleVoiceHuddle() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (session.isVoiceHuddleActive) AuraEmerald.copy(alpha = 0.25f) else AuraDarkCard,
                            contentColor = if (session.isVoiceHuddleActive) AuraEmerald else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("voice_huddle_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (session.isVoiceHuddleActive) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .then(if (session.isVoiceHuddleActive) Modifier.scale(waveScale) else Modifier)
                        )
                        Text(
                            text = if (session.isVoiceHuddleActive) "Huddle (3)" else "Join Audio",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Collaborators Presence Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraDarkCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        session.collaborators.forEachIndexed { index, col ->
                            Box(modifier = Modifier.offset(x = (-index * 10).dp)) {
                                AuraAvatar(
                                    drawableName = col.avatarDrawableName,
                                    size = 28.dp,
                                    isOnline = col.isOnline
                                )
                            }
                        }
                        Text(
                            text = "3 creators co-editing now",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraNeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CRDT v4.2 Active",
                            fontSize = 9.sp,
                            color = AuraNeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Interactive Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, AuraNeonViolet.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .testTag("interactive_canvas_preview")
            ) {
                // Base Artwork with Filter Color Shader overlay
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Canvas Artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dynamic Filter Tint Overlay
                val filterColor = when (session.activeFilterName) {
                    "Prism Glow" -> AuraNeonViolet.copy(alpha = 0.25f * session.filterIntensity)
                    "Cyber Noir" -> Color(0xFF0F172A).copy(alpha = 0.4f * session.filterIntensity)
                    "Solar Flare" -> AuraHotPink.copy(alpha = 0.22f * session.filterIntensity)
                    else -> AuraNeonCyan.copy(alpha = 0.25f * session.filterIntensity)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(filterColor)
                )

                // Render Canvas Layers
                session.layers.forEach { layer ->
                    val isSelected = layer.id == selectedLayerId

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(layer.offsetX.toInt() * 3, layer.offsetY.toInt() * 2) }
                            .rotate(layer.rotation)
                            .scale(layer.scale)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color.Black.copy(alpha = 0.75f)
                                else Color.Black.copy(alpha = 0.45f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.8.dp,
                                color = if (isSelected) AuraNeonCyan else Color(layer.colorHex).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedLayerId = layer.id
                                editingTextValue = layer.content
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = layer.content,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(layer.colorHex),
                                    letterSpacing = 1.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Selected",
                                        tint = AuraNeonCyan,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .padding(start = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = "By: ${layer.lastEditedBy}",
                                fontSize = 9.sp,
                                color = Color.LightGray.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Active filter & live badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Filter: ${session.activeFilterName} (${(session.filterIntensity * 100).toInt()}%)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuraNeonViolet.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CO-AUTHORING ✦",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Live Real-Time Activity Log Ticker
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AuraEmerald)
                        )
                        Text(
                            text = "LIVE CRDT AUDIT STREAM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                    Text(
                        text = "0ms peer latency",
                        fontSize = 9.sp,
                        color = AuraEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                session.activityEvents.take(3).forEach { evt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = evt.collaboratorName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraNeonCyan
                        )
                        Text(
                            text = " ${evt.actionText} • ${evt.timestampText}",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Studio Controls (Tabs: Layers, Filters, Publish)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = AuraDarkCard,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AuraNeonCyan
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Layers", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("AI & Publish", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTabIndex) {
                    0 -> {
                        // Layers Tab: Edit selected layer text, add layer
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuraDarkCard)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Edit Selected Layer Text:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editingTextValue,
                                    onValueChange = {
                                        editingTextValue = it
                                        viewModel.updateCollabLayerText(selectedLayerId, it)
                                    },
                                    placeholder = { Text("Type text overlay...") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_layer_text"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AuraNeonCyan,
                                        unfocusedBorderColor = Color(0xFF28354D),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                IconButton(
                                    onClick = {
                                        viewModel.addCollabLayer(
                                            type = LayerType.STICKER,
                                            title = "Neon Sticker",
                                            content = "⚡ NEURAL ECHO",
                                            color = 0xFFEC4899
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AuraNeonViolet)
                                        .testTag("add_sticker_layer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Layer",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Layer quick switcher pills
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(session.layers) { layer ->
                                    val isSelected = layer.id == selectedLayerId
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) AuraNeonCyan.copy(alpha = 0.2f) else Color(0xFF1E283D))
                                            .border(1.dp, if (isSelected) AuraNeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedLayerId = layer.id
                                                editingTextValue = layer.content
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = layer.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) AuraNeonCyan else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Filters Tab
                        val filters = listOf("Prism Glow", "Cyber Noir", "Solar Flare", "Neon Glitch")
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuraDarkCard)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Real-Time Neural Filter Shaders:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filters) { fName ->
                                    val isSelected = session.activeFilterName == fName
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) AuraHotPink.copy(alpha = 0.25f) else Color(0xFF1E283D))
                                            .border(1.2.dp, if (isSelected) AuraHotPink else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.updateCollabFilter(fName, session.filterIntensity) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = fName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else Color.LightGray
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Shader Intensity: ${(session.filterIntensity * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Slider(
                                value = session.filterIntensity,
                                onValueChange = { viewModel.updateCollabFilter(session.activeFilterName, it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = AuraHotPink,
                                    activeTrackColor = AuraHotPink,
                                    inactiveTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("filter_intensity_slider")
                            )
                        }
                    }

                    2 -> {
                        // AI & Publish Tab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuraDarkCard)
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gemini Collaborative Caption:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Button(
                                    onClick = { viewModel.generateAiCaptionForStudio("Futuristic architecture and co-creation") },
                                    enabled = !isGeneratingCaption,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AuraNeonViolet,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("generate_studio_caption_button")
                                ) {
                                    if (isGeneratingCaption) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Text("AI Suggest", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                                    }
                                }
                            }

                            if (aiGeneratedCaption != null) {
                                Text(
                                    text = "💡 ${aiGeneratedCaption!!}",
                                    fontSize = 12.sp,
                                    color = AuraNeonCyan,
                                    modifier = Modifier
                                        .clickable { finalPostCaption = aiGeneratedCaption!! }
                                        .padding(vertical = 8.dp)
                                )
                            }

                            OutlinedTextField(
                                value = finalPostCaption,
                                onValueChange = { finalPostCaption = it },
                                placeholder = { Text("Write co-authored caption...", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .testTag("input_studio_caption"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AuraNeonCyan,
                                    unfocusedBorderColor = Color(0xFF28354D),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.publishStudioPost(finalPostCaption) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("publish_collab_post_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AuraNeonCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Publish as Collab Post (Credits 3 Creators)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
