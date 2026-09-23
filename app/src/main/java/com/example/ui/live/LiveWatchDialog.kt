package com.example.ui.live

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LiveComment
import com.example.data.model.LiveStreamSession
import com.example.ui.components.AuraAvatar
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FloatingHeart(val id: Long, val emoji: String, val offsetX: Float)

@Composable
fun LiveWatchDialog(
    session: LiveStreamSession,
    comments: List<LiveComment>,
    onSendComment: (String) -> Unit,
    onClose: () -> Unit
) {
    var viewerCommentText by remember { mutableStateOf("") }
    var joinRequestSent by remember { mutableStateOf(false) }
    val floatingHearts = remember { mutableStateListOf<FloatingHeart>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("live_watch_dialog")
        ) {
            val context = LocalContext.current

            // Live Stream Video Canvas
            if (session.coHost != null) {
                // Dual Split Screen
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val hostResId = rememberDrawableResId(context, session.videoBackgroundDrawable)
                        if (hostResId != 0) {
                            Image(
                                painter = painterResource(id = hostResId),
                                contentDescription = "Host Stream",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Host • ${session.host.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val coHostResId = rememberDrawableResId(context, session.coHostBackgroundDrawable ?: "reel_neon_oasis_1790201522014")
                        if (coHostResId != 0) {
                            Image(
                                painter = painterResource(id = coHostResId),
                                contentDescription = "CoHost Stream",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(AuraHotPink.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Co-Host • ${session.coHost.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                val resId = rememberDrawableResId(context, session.videoBackgroundDrawable)
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Live Broadcast",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Darkening overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Pulsing LIVE Alpha
            val infiniteTransition = rememberInfiniteTransition(label = "watch_pulse")
            val liveAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
                label = "watch_live_alpha"
            )

            // Viewer Overlay Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 24.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Host info, LIVE badge, viewers, close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuraAvatar(drawableName = session.host.avatarDrawableName, size = 40.dp, isVerified = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(session.host.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = liveAlpha))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${session.viewerCount} watching", fontSize = 10.sp, color = Color.LightGray)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Request to Join as Co-Host Button
                        Button(
                            onClick = {
                                joinRequestSent = true
                                scope.launch {
                                    delay(3000)
                                    joinRequestSent = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (joinRequestSent) AuraEmerald else AuraHotPink,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("request_join_live_button")
                        ) {
                            Text(if (joinRequestSent) "Requested ✓" else "Join Co-Host", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("close_watch_live_button")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Middle: Title Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(session.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Bottom: Comments & Floating Reaction Button
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Comments Stream
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(comments) { comment ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AuraAvatar(
                                        drawableName = comment.senderAvatar,
                                        size = 20.dp,
                                        isVerified = false
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${comment.senderName}: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (comment.isSparked) AuraHotPink else AuraNeonCyan
                                    )
                                    Text(
                                        text = comment.text,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Comment Input Bar + Heart Burst Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewerCommentText,
                            onValueChange = { viewerCommentText = it },
                            placeholder = { Text("Say something live...", color = Color.Gray, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("viewer_comment_input"),
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

                        // Send Comment Button
                        IconButton(
                            onClick = {
                                if (viewerCommentText.isNotBlank()) {
                                    onSendComment(viewerCommentText)
                                    viewerCommentText = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AuraNeonCyan)
                                .testTag("viewer_send_comment_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Heart Button (spawns animated floating reactions)
                        IconButton(
                            onClick = {
                                val heart = FloatingHeart(
                                    id = System.currentTimeMillis(),
                                    emoji = listOf("❤️", "🔥", "⚡", "✦").random(),
                                    offsetX = (-30..30).random().toFloat()
                                )
                                floatingHearts.add(heart)
                                scope.launch {
                                    delay(2000)
                                    floatingHearts.remove(heart)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF007A))
                                .testTag("floating_heart_reaction_button")
                        ) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Send Heart", tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // Render Floating Hearts on Right Side
            floatingHearts.forEach { heart ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 80.dp)
                        .offset(x = heart.offsetX.dp)
                ) {
                    Text(text = heart.emoji, fontSize = 28.sp)
                }
            }
        }
    }
}
