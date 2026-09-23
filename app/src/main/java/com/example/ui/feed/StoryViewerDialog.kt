package com.example.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StoryItem
import com.example.ui.components.AuraAvatar
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun StoryViewerDialog(
    stories: List<StoryItem>,
    initialStoryIndex: Int,
    onDismiss: () -> Unit,
    onStorySeen: (String) -> Unit,
    onReplyToStory: (StoryItem, String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialStoryIndex.coerceIn(0, stories.size - 1)) }
    val currentStory = stories.getOrNull(currentIndex) ?: run {
        onDismiss()
        return
    }

    var isPaused by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var showReplySentBanner by remember { mutableStateOf(false) }
    var reactionEmojiBurst by remember { mutableStateOf<String?>(null) }

    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Mark current story as seen
    LaunchedEffect(currentStory.id) {
        onStorySeen(currentStory.id)
    }

    // Auto-advance timer (4.5 seconds per story)
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            progress.snapTo(0f)
            val durationMs = 4500
            val startTime = System.currentTimeMillis()
            while (isActive && progress.value < 1f) {
                if (!isPaused) {
                    val elapsed = (System.currentTimeMillis() - startTime).toFloat()
                    progress.snapTo((elapsed / durationMs).coerceIn(0f, 1f))
                    if (progress.value >= 1f) break
                }
                delay(16)
            }
            if (!isPaused && currentIndex < stories.size - 1) {
                currentIndex++
            } else if (!isPaused) {
                onDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_modal")
        ) {
            val context = LocalContext.current
            val imageResId = rememberDrawableResId(context, currentStory.imageDrawableName)

            // Background Image
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Story visual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Shading & Tint Gradients
            val filterGradient = when (currentStory.filterName) {
                "Neon Glitch" -> Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.5f),
                        AuraHotPink.copy(alpha = 0.25f),
                        Color.Black.copy(alpha = 0.6f)
                    )
                )
                "Prism Glow" -> Brush.radialGradient(
                    listOf(AuraNeonViolet.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.5f))
                )
                "Cyber Noir" -> Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.5f))
                )
                "Matrix" -> Brush.verticalGradient(
                    listOf(AuraEmerald.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.6f))
                )
                else -> Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.6f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.75f)
                    )
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(filterGradient))

            // Touch Zones: Left (prev), Right (next), Long Press (pause)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentIndex) {
                        detectTapGestures(
                            onPress = {
                                isPaused = true
                                tryAwaitRelease()
                                isPaused = false
                            },
                            onTap = { offset ->
                                if (offset.x < size.width * 0.3f) {
                                    if (currentIndex > 0) currentIndex--
                                } else if (offset.x > size.width * 0.3f) {
                                    if (currentIndex < stories.size - 1) currentIndex++ else onDismiss()
                                }
                            }
                        )
                    }
            )

            // UI Content Layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 24.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Progress bars & Header
                Column {
                    // Segmented Progress Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        stories.forEachIndexed { index, _ ->
                            val itemProgress = when {
                                index < currentIndex -> 1f
                                index == currentIndex -> progress.value
                                else -> 0f
                            }
                            LinearProgressIndicator(
                                progress = { itemProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = AuraNeonCyan,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Header: Avatar, Name, Handle, Time, Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AuraAvatar(
                                drawableName = currentStory.avatarDrawableName,
                                size = 42.dp,
                                isVerified = true
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentStory.creatorName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${currentStory.timestampText}",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                                Text(
                                    text = currentStory.creatorHandle,
                                    fontSize = 11.sp,
                                    color = AuraNeonCyan
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("close_story_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Story",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Middle: Sticker & Audio disc overlay
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!currentStory.stickerText.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.5.dp, AuraNeonCyan, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = currentStory.stickerText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = AuraNeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!currentStory.musicTrack.isNullOrBlank()) {
                        val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(4000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "spin_angle"
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, AuraEmerald.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(rotation)
                                    .clip(CircleShape)
                                    .background(AuraEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentStory.musicTrack,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Bottom: Caption, Quick Reactions & Reply Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (currentStory.caption.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.dp, Color(0xFF223048), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = currentStory.caption,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Emoji Reactions Quick Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("❤️", "🔥", "⚡", "✦", "🚀", "💎").forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable {
                                        reactionEmojiBurst = emoji
                                        onReplyToStory(currentStory, "Reacted with $emoji")
                                        scope.launch {
                                            delay(1500)
                                            reactionEmojiBurst = null
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reply Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = {
                                Text(
                                    text = "Send encrypted reply to ${currentStory.creatorName.split(" ").first()}...",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("story_reply_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color.Black.copy(alpha = 0.7f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.7f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onReplyToStory(currentStory, replyText)
                                    replyText = ""
                                    showReplySentBanner = true
                                    scope.launch {
                                        delay(2500)
                                        showReplySentBanner = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(AuraNeonViolet, AuraNeonCyan))
                                )
                                .testTag("send_story_reply_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Reply",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Notification Banner on Reply Sent
            AnimatedVisibility(
                visible = showReplySentBanner,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 70.dp, start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraDarkCard)
                        .border(1.dp, AuraEmerald, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "🔒 Sealed & Delivered to ${currentStory.creatorName}'s Vault",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraEmerald
                    )
                }
            }

            // Floating Emoji Burst Animation
            reactionEmojiBurst?.let { emoji ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 54.sp)
                }
            }
        }
    }
}
