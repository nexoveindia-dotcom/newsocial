package com.example.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.crypto.CryptoManager
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraDeepPurple
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultChatScreen(viewModel: SocialViewModel) {
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConvo by viewModel.activeConvo.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()

    var inputMessageText by remember { mutableStateOf("") }
    var selectedTimerSec by remember { mutableIntStateOf(0) }
    var showSafetyNumberDialog by remember { mutableStateOf(false) }
    var showTimerPickerSheet by remember { mutableStateOf(false) }

    // Voice recording simulation state
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceRecordSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceRecordSeconds = 0
            while (isRecordingVoice) {
                delay(1000)
                voiceRecordSeconds++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("vault_chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Conversation Switcher Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuraDarkSurface)
            ) {
                items(conversations) { convo ->
                    val isSelected = convo.id == activeConvo.id
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AuraNeonViolet.copy(alpha = 0.25f) else AuraDarkCard)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.5.dp,
                                color = if (isSelected) AuraNeonViolet else Color(0xFF28364F),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.selectConversation(convo) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AuraAvatar(
                            drawableName = convo.participant.avatarDrawableName,
                            size = 24.dp,
                            hasGlowBorder = false,
                            isOnline = isSelected
                        )
                        Text(
                            text = if (convo.isGroup) "Guild ✦" else convo.participant.name.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.LightGray,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }

            // Vault Header with Cryptographic Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuraDarkSurface)
                    .border(0.5.dp, Color(0xFF202A3D))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuraAvatar(
                        drawableName = activeConvo.participant.avatarDrawableName,
                        size = 38.dp,
                        hasGlowBorder = true,
                        isOnline = true,
                        isVerified = activeConvo.participant.isVerified
                    )

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeConvo.participant.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (activeConvo.isGroup) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AuraNeonCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(text = "4 PEERS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AuraNeonCyan)
                                }
                            }
                        }

                        // Status / Typing
                        if (activeConvo.isTyping) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeConvo.typingStatusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuraNeonCyan
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AuraEmerald,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "AES-256-GCM • Ratcheted",
                                    fontSize = 10.sp,
                                    color = AuraEmerald,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Safety Number Dialog Trigger
                    IconButton(
                        onClick = { showSafetyNumberDialog = true },
                        modifier = Modifier.testTag("safety_number_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Verify Safety Number",
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Self Destruct Timer Button
                    IconButton(
                        onClick = { showTimerPickerSheet = true },
                        modifier = Modifier.testTag("timer_picker_button")
                    ) {
                        Icon(
                            imageVector = if (selectedTimerSec > 0) Icons.Default.HourglassTop else Icons.Default.Timer,
                            contentDescription = "Self Destruct Timer",
                            tint = if (selectedTimerSec > 0) AuraHotPink else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Messages Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Security Handshake Card
                    SecurityHandshakeBanner(
                        convo = activeConvo,
                        onVerifyClick = { showSafetyNumberDialog = true }
                    )
                }

                items(messages) { msg ->
                    EncryptedMessageBubble(
                        msg = msg,
                        onReaction = { emoji ->
                            viewModel.toggleMessageReaction(msg.id, emoji)
                        }
                    )
                }

                // Live typing indicator bubble
                if (activeConvo.isTyping) {
                    item {
                        TypingIndicatorBubble(authorName = activeConvo.participant.name)
                    }
                }
            }

            // Bottom Input Bar
            if (isRecordingVoice) {
                // Voice Note Recording Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AuraDarkSurface)
                        .border(1.dp, AuraHotPink.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(AuraHotPink)
                        )
                        Text(
                            text = "Recording Voice Note: ${voiceRecordSeconds}s (AES-256 Encrypted)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = { isRecordingVoice = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                        }
                        IconButton(
                            onClick = {
                                val sec = maxOf(voiceRecordSeconds, 1)
                                viewModel.sendEncryptedVoiceNote(sec)
                                isRecordingVoice = false
                            },
                            modifier = Modifier.testTag("send_voice_note_button")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = AuraNeonCyan)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AuraDarkSurface)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Note Record Trigger
                    IconButton(
                        onClick = { isRecordingVoice = true },
                        modifier = Modifier.testTag("record_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Record Encrypted Voice Note",
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Text Input
                    OutlinedTextField(
                        value = inputMessageText,
                        onValueChange = { inputMessageText = it },
                        placeholder = {
                            Text(
                                text = if (selectedTimerSec > 0) "Ephemeral (${selectedTimerSec}s)..." else "Encrypted message...",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (selectedTimerSec > 0) AuraHotPink else AuraNeonViolet,
                            unfocusedBorderColor = Color(0xFF28354D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputMessageText.isNotBlank()) {
                                viewModel.sendEncryptedMessage(inputMessageText, selectedTimerSec)
                                inputMessageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AuraNeonViolet, AuraDeepPurple)))
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Encrypted",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Safety Number Verification Dialog
        if (showSafetyNumberDialog) {
            SafetyNumberDialog(
                convo = activeConvo,
                onDismiss = { showSafetyNumberDialog = false }
            )
        }

        // Ephemeral Timer Picker Sheet
        if (showTimerPickerSheet) {
            TimerPickerSheet(
                currentTimer = selectedTimerSec,
                onSelectTimer = {
                    selectedTimerSec = it
                    showTimerPickerSheet = false
                },
                onDismiss = { showTimerPickerSheet = false }
            )
        }
    }
}

@Composable
fun SecurityHandshakeBanner(
    convo: ChatConversation,
    onVerifyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131A29))
            .border(1.dp, AuraNeonViolet.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AuraEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "End-to-End Cryptographic Enclave",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraEmerald,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(
                text = "Messages and voice notes in this thread are authenticated with 256-bit AES-GCM and peer RSA-2048 keypairs. No third party or server can inspect payloads.",
                fontSize = 10.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Text(
                text = "Fingerprint: ${convo.safetyNumber}",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = AuraNeonCyan,
                modifier = Modifier
                    .clickable { onVerifyClick() }
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun EncryptedMessageBubble(
    msg: ChatMessage,
    onReaction: (String) -> Unit
) {
    var showCipherInspector by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }

    val reactions = listOf("❤️", "⚡", "🔥", "✦")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isFromMe) 16.dp else 4.dp,
                        bottomEnd = if (msg.isFromMe) 4.dp else 16.dp
                    )
                )
                .background(
                    if (msg.isIncinerated) Brush.linearGradient(listOf(Color(0xFF1E1E24), Color(0xFF1E1E24)))
                    else if (msg.isFromMe) Brush.linearGradient(listOf(AuraNeonViolet, AuraDeepPurple))
                    else Brush.linearGradient(listOf(Color(0xFF1E283D), AuraDarkCard))
                )
                .border(
                    width = 0.8.dp,
                    color = if (msg.isIncinerated) Color.DarkGray
                    else if (msg.isFromMe) AuraNeonViolet.copy(alpha = 0.4f)
                    else Color(0xFF2E3E5C),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { showReactionPicker = !showReactionPicker }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (msg.isIncinerated) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Incinerated",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "[Message incinerated from storage]",
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                } else if (msg.isVoiceNote) {
                    // Voice Note Audio Bubble
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (msg.isFromMe) Color.White.copy(alpha = 0.2f) else AuraNeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(horizontal = 6.dp)
                        )
                        Text(
                            text = "${msg.voiceDurationSeconds}s (Encrypted Voice Note)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                } else {
                    if (showCipherInspector) {
                        Column {
                            Text(
                                text = "RAW AES-256 CIPHERTEXT:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraAmberColor()
                            )
                            Text(
                                text = msg.encryptedPayload.take(64) + "...",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.LightGray
                            )
                        }
                    } else {
                        Text(
                            text = msg.decryptedText,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 19.sp
                        )
                    }
                }

                // Delivery Status, Timer & Cipher Toggle
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!msg.isIncinerated) {
                        Icon(
                            imageVector = if (showCipherInspector) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Inspect Cipher",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(12.dp)
                                .clickable { showCipherInspector = !showCipherInspector }
                        )
                    }

                    if (msg.selfDestructSeconds > 0 && !msg.isIncinerated) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = AuraHotPink,
                            modifier = Modifier
                                .size(11.dp)
                                .padding(start = 4.dp)
                        )
                        Text(
                            text = "${msg.selfDestructSeconds}s",
                            fontSize = 9.sp,
                            color = AuraHotPink,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Text(
                        text = " • ${msg.timestampFormatted}",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Delivery checkmarks for outgoing
                    if (msg.isFromMe) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = msg.deliveryStatus,
                            tint = if (msg.deliveryStatus == "READ") AuraNeonCyan else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(13.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Reaction badge if reacted
        msg.reaction?.let { r ->
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1B2336))
                    .border(0.5.dp, AuraNeonViolet, RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = r, fontSize = 12.sp)
            }
        }

        // Quick Reaction Picker Bar
        if (showReactionPicker) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AuraDarkSurface)
                    .border(1.dp, Color(0xFF2C3C58), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reactions.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                onReaction(emoji)
                                showReactionPicker = false
                            }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble(authorName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E283D))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$authorName is typing",
            fontSize = 11.sp,
            color = AuraNeonCyan,
            modifier = Modifier.padding(end = 6.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(AuraNeonCyan.copy(alpha = dotAlpha)))
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(AuraNeonCyan.copy(alpha = dotAlpha)))
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(AuraNeonCyan.copy(alpha = dotAlpha)))
        }
    }
}

@Composable
fun SafetyNumberDialog(
    convo: ChatConversation,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraDarkSurface,
        icon = {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AuraNeonCyan, modifier = Modifier.size(32.dp))
        },
        title = {
            Text(
                text = "Verify Cryptographic Safety Number",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Compare this safety number with ${convo.participant.name} to confirm zero man-in-the-middle tampering:",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 60-digit representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AuraDarkCard)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = convo.safetyNumber,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AuraNeonCyan,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuraEmerald.copy(alpha = 0.15f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AuraEmerald, modifier = Modifier.size(16.dp))
                    Text(
                        text = "RSA-2048 Identity Verified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraEmerald,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = Color.Black)
            ) {
                Text("Mark as Verified", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPickerSheet(
    currentTimer: Int,
    onSelectTimer: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = AuraDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Ephemeral Self-Destruct Timer",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "When active, outgoing and incoming messages are permanently incinerated from local memory and storage once the timer expires.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            val options = listOf(
                Pair(0, "Off (Persistent Enclave)"),
                Pair(5, "5 seconds (Quick Peek)"),
                Pair(30, "30 seconds (Standard Ephemeral)"),
                Pair(60, "60 seconds (Extended)")
            )

            options.forEach { (seconds, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (currentTimer == seconds) AuraHotPink.copy(alpha = 0.2f) else AuraDarkCard)
                        .clickable { onSelectTimer(seconds) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (currentTimer == seconds) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentTimer == seconds) AuraHotPink else Color.White
                    )
                    if (currentTimer == seconds) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AuraHotPink, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AuraAmberColor() = Color(0xFFF59E0B)
