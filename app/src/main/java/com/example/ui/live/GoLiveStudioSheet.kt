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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LiveComment
import com.example.data.model.LiveStreamSession
import com.example.data.model.UserProfile
import com.example.ui.components.AuraAvatar
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoLiveSetupSheet(
    onDismiss: () -> Unit,
    onStartBroadcast: (title: String, category: String, allowCoHost: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var streamTitle by remember { mutableStateOf("⚡ Live Neural Jam & 3D Shader Synthesis") }
    var streamCategory by remember { mutableStateOf("Creative Studio") }
    var allowCoHost by remember { mutableStateOf(true) }

    val categories = listOf("Creative Studio", "Live Synth Jam", "3D Shaders", "Cipher Enclave", "Deep Lore")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("go_live_setup_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
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
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GO LIVE STUDIO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Broadcast Title
            Text(
                text = "Broadcast Stream Title",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = streamTitle,
                onValueChange = { streamTitle = it },
                placeholder = { Text("What are you sharing live?", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_stream_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFEF4444),
                    unfocusedBorderColor = Color(0xFF22304A),
                    focusedContainerColor = AuraDarkCard,
                    unfocusedContainerColor = AuraDarkCard,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector
            Text(
                text = "Stream Category",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = streamCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFEF4444).copy(alpha = 0.25f) else AuraDarkCard)
                            .border(1.dp, if (isSelected) Color(0xFFEF4444) else Color(0xFF223048), RoundedCornerShape(12.dp))
                            .clickable { streamCategory = cat }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Co-Host Toggle
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
                Column {
                    Text(
                        text = "Allow Split-Screen Co-Hosts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Verified peers can request or be invited to join",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = allowCoHost,
                    onCheckedChange = { allowCoHost = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFEF4444),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF1E293B)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Live Broadcast Button
            Button(
                onClick = {
                    onStartBroadcast(streamTitle, streamCategory, allowCoHost)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_live_broadcast_button"),
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
                            Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFFF007A)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "START LIVE BROADCAST",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveBroadcastStudioDialog(
    session: LiveStreamSession,
    comments: List<LiveComment>,
    onSendComment: (String) -> Unit,
    onInviteCoHost: (UserProfile) -> Unit,
    onEndBroadcast: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var currentFxIndex by remember { mutableIntStateOf(0) }
    val fxNames = listOf("Matrix Glitch", "Neon Cyan", "Cyber Noir", "Clean 4K")
    var hostCommentText by remember { mutableStateOf("") }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showEndSummaryDialog by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(1) }
    val listState = rememberLazyListState()

    // Timer ticker
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000)
            elapsedSeconds++
        }
    }

    // Auto-scroll to newest comment
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Dialog(
        onDismissRequest = { showEndSummaryDialog = true },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("active_live_broadcast_studio")
        ) {
            val context = LocalContext.current

            // Video Canvas Layout: Single or Split Screen if Co-Host joined!
            if (session.coHost != null) {
                // Split Screen (Host top half, Co-Host bottom half)
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
                        // Host Tag
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
                        // Co-Host Tag
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
                // Single fullscreen video
                val bgResId = rememberDrawableResId(context, session.videoBackgroundDrawable)
                if (bgResId != 0) {
                    Image(
                        painter = painterResource(id = bgResId),
                        contentDescription = "Live Video Stream",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Shader Overlay
            val shaderOverlay = when (fxNames[currentFxIndex]) {
                "Matrix Glitch" -> Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.4f), AuraEmerald.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.5f))
                )
                "Neon Cyan" -> Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.35f), AuraNeonCyan.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.5f))
                )
                "Cyber Noir" -> Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.6f), Color.Black.copy(alpha = 0.6f))
                )
                else -> Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.55f))
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(shaderOverlay))

            // Pulsing Red "LIVE" Badge Animation
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val liveAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
                label = "live_badge_alpha"
            )

            // Live HUD Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 24.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar: LIVE badge, viewer count, title, controls
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing LIVE badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEF4444).copy(alpha = liveAlpha))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Viewer Count
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${session.viewerCount}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Timer Counter (mm:ss)
                            val minutes = elapsedSeconds / 60
                            val seconds = elapsedSeconds % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.LightGray
                            )
                        }

                        // End Stream Button
                        Button(
                            onClick = { showEndSummaryDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("end_stream_top_button")
                        ) {
                            Text("End Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title & Category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Middle Right: Quick Broadcast Controls (Camera Flip / FX / Mic / Invite Co-Host)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Co-Host Invite
                        IconButton(
                            onClick = { showInviteDialog = true },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, AuraHotPink, CircleShape)
                                .testTag("invite_cohost_button")
                        ) {
                            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = "Invite CoHost", tint = AuraHotPink, modifier = Modifier.size(20.dp))
                        }

                        // FX Cycle
                        IconButton(
                            onClick = { currentFxIndex = (currentFxIndex + 1) % fxNames.size },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, AuraNeonCyan, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Cycle FX", tint = AuraNeonCyan, modifier = Modifier.size(20.dp))
                        }

                        // Mic Mute / Unmute
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.dp, if (isMuted) Color(0xFFEF4444) else AuraEmerald, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Toggle Mic",
                                tint = if (isMuted) Color(0xFFEF4444) else AuraEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom: Scrolling Live Comments Stream & Host Comment Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Comments list
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
                                        color = if (comment.isHost) Color(0xFFEF4444) else AuraNeonCyan
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

                    // Host Comment Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = hostCommentText,
                            onValueChange = { hostCommentText = it },
                            placeholder = { Text("Comment as host...", color = Color.Gray, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("host_comment_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFEF4444),
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
                                if (hostCommentText.isNotBlank()) {
                                    onSendComment(hostCommentText)
                                    hostCommentText = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .testTag("host_send_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Co-Host Invitation Dialog
    if (showInviteDialog) {
        val candidatePeers = listOf(
            UserProfile("user_elena", "Elena Rostova", "@elena.synapse", "post_cyber_creator_1790200662289", true, 94),
            UserProfile("user_marcus", "Marcus Vance", "@marcus.v", "post_crystal_city_1790200645179", true, 91),
            UserProfile("user_sarah", "Sarah Chen", "@schen_art", "post_aurora_vibes_1790200680892", true, 89)
        )

        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            containerColor = AuraDarkCard,
            title = {
                Text("Invite Split-Screen Co-Host", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a verified creator to broadcast together in real-time split-screen:", fontSize = 12.sp, color = Color.LightGray)
                    candidatePeers.forEach { peer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    onInviteCoHost(peer)
                                    showInviteDialog = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AuraAvatar(drawableName = peer.avatarDrawableName, size = 32.dp, isVerified = true)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(peer.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(peer.handle, fontSize = 11.sp, color = AuraNeonCyan)
                                }
                            }
                            Text("Invite ✦", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraHotPink)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInviteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // End Broadcast Summary Dialog
    if (showEndSummaryDialog) {
        AlertDialog(
            onDismissRequest = { showEndSummaryDialog = false },
            containerColor = AuraDarkCard,
            title = {
                Text("Broadcast Ended", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Here is your live broadcast performance summary:", fontSize = 12.sp, color = Color.LightGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131D30))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${session.viewerCount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AuraNeonCyan)
                            Text("Peak Viewers", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val mins = elapsedSeconds / 60
                            val secs = elapsedSeconds % 60
                            Text(String.format("%02d:%02d", mins, secs), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Duration", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("2.4k", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AuraHotPink)
                            Text("Sparks Earned", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndSummaryDialog = false
                        onEndBroadcast()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_end_stream_button")
                ) {
                    Text("Finish & Close")
                }
            }
        )
    }
}
