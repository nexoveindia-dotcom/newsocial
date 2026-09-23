package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

enum class CreateOptionType {
    STORY,
    POST,
    REEL,
    GO_LIVE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedCreateSheet(
    onDismiss: () -> Unit,
    onSelectOption: (CreateOptionType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("unified_create_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
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
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AuraNeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CREATE & BROADCAST",
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

            Text(
                text = "Choose a creation mode to drop content onto the neural stream.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            // Grid of 4 options
            CreateOptionCard(
                title = "Story",
                subtitle = "24-hour fleeting visual moment with spatial audio & neon stickers",
                icon = Icons.Default.CameraAlt,
                gradientColors = listOf(AuraNeonViolet, AuraHotPink),
                tag = "24h Expire",
                tagColor = AuraHotPink,
                testTag = "create_story_card_btn",
                onClick = {
                    onDismiss()
                    onSelectOption(CreateOptionType.STORY)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CreateOptionCard(
                title = "Feed Post",
                subtitle = "High-res digital artwork with AI captions & collaborative co-authors",
                icon = Icons.Default.PostAdd,
                gradientColors = listOf(AuraNeonCyan, Color(0xFF3B82F6)),
                tag = "Neural Feed",
                tagColor = AuraNeonCyan,
                testTag = "create_post_card_btn",
                onClick = {
                    onDismiss()
                    onSelectOption(CreateOptionType.POST)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CreateOptionCard(
                title = "Reel",
                subtitle = "Vertical 9:16 short-form video stream with synced beats & remixing",
                icon = Icons.Default.Videocam,
                gradientColors = listOf(Color(0xFFFF007A), Color(0xFFFF5252)),
                tag = "9:16 Pulse",
                tagColor = AuraHotPink,
                testTag = "create_reel_card_btn",
                onClick = {
                    onDismiss()
                    onSelectOption(CreateOptionType.REEL)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CreateOptionCard(
                title = "Go Live",
                subtitle = "Broadcast live interactive video with audience comments & split-screen co-host",
                icon = Icons.Default.LiveTv,
                gradientColors = listOf(Color(0xFFEF4444), Color(0xFFF97316)),
                tag = "🔴 REALTIME",
                tagColor = Color(0xFFEF4444),
                testTag = "create_go_live_card_btn",
                onClick = {
                    onDismiss()
                    onSelectOption(CreateOptionType.GO_LIVE)
                }
            )
        }
    }
}

@Composable
private fun CreateOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    tag: String,
    tagColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AuraDarkCard)
            .border(1.dp, Color(0xFF22304A), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tagColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    maxLines = 2,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
