package com.example.ui.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Comment
import com.example.data.model.Reel
import com.example.ui.components.AuraAvatar
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelCommentsSheet(
    reel: Reel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val commentList = remember {
        mutableStateListOf(
            Comment("rc_1", "Elena Rostova", "@elena.synapse", "The bass drop aligns with the volumetric laser perfectly! 🔥", "14m ago", "Hyped", 42),
            Comment("rc_2", "Marcus Vance", "@marcus.v", "Stems were mixed at 432Hz. Sounds spatial on open-back cans.", "9m ago", "Constructive", 28),
            Comment("rc_3", "Sarah Chen", "@schen_art", "This visual frequency is elevating the entire timeline ✨", "3m ago", "Inspiring", 19)
        )
    }

    var commentInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("reel_comments_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp)
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "COMMENTS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${commentList.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraHotPink
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(commentList) { comment ->
                    var isLiked by remember { mutableStateOf(false) }
                    var likeCount by remember { mutableStateOf(comment.likesCount) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            val avatarRes = when {
                                comment.authorHandle.contains("elena") -> "post_cyber_creator_1790200662289"
                                comment.authorHandle.contains("marcus") -> "post_crystal_city_1790200645179"
                                else -> "post_aurora_vibes_1790200680892"
                            }
                            AuraAvatar(drawableName = avatarRes, size = 34.dp, isVerified = true)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = comment.authorName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = comment.timestamp,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    text = comment.text,
                                    fontSize = 12.sp,
                                    color = Color.LightGray,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                                )

                                // Sentiment Tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (comment.aiSentiment) {
                                                "Hyped" -> AuraHotPink.copy(alpha = 0.2f)
                                                "Inspiring" -> AuraEmerald.copy(alpha = 0.2f)
                                                else -> AuraNeonCyan.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AI: ${comment.aiSentiment}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (comment.aiSentiment) {
                                            "Hyped" -> AuraHotPink
                                            "Inspiring" -> AuraEmerald
                                            else -> AuraNeonCyan
                                        }
                                    )
                                }
                            }
                        }

                        // Comment Like Action
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isLiked = !isLiked
                                    likeCount += if (isLiked) 1 else -1
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like Comment",
                                    tint = if (isLiked) AuraHotPink else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(text = "$likeCount", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Emoji Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("🔥", "⚡", "✦", "❤️", "🌌").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuraDarkCard)
                            .clickable { commentInput += emoji }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(emoji, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Comment Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("Add comment to reel...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reel_new_comment_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraHotPink,
                        unfocusedBorderColor = Color(0xFF243048),
                        focusedContainerColor = AuraDarkCard,
                        unfocusedContainerColor = AuraDarkCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            val newComment = Comment(
                                id = "rc_${System.currentTimeMillis()}",
                                authorName = "Nova Sterling",
                                authorHandle = "@novasterling",
                                text = commentInput,
                                timestamp = "Just now",
                                aiSentiment = "Hyped",
                                likesCount = 1
                            )
                            commentList.add(newComment)
                            commentInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AuraHotPink)
                        .testTag("reel_submit_comment_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
