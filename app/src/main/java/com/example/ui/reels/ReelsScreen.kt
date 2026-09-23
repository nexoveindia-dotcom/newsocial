package com.example.ui.reels

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Reel
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
fun ReelsScreen(
    viewModel: SocialViewModel,
    onNavigateToStudio: () -> Unit
) {
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    var showCreateSheet by remember { mutableStateOf(false) }
    var commentTargetReel by remember { mutableStateOf<Reel?>(null) }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("reels_screen")
    ) {
        if (reels.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = reels[page]
                ReelPageItem(
                    reel = reel,
                    onLike = { viewModel.toggleReelLike(reel) },
                    onSave = { viewModel.toggleReelSave(reel) },
                    onComment = { commentTargetReel = reel },
                    onRemix = {
                        viewModel.setRoute("collab")
                    }
                )
            }
        }

        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AURA PULSE",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AuraHotPink.copy(alpha = 0.25f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "9:16 REELS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraHotPink
                    )
                }
            }

            // Create Reel Button
            Button(
                onClick = { showCreateSheet = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuraHotPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("open_reel_creator_button")
            ) {
                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(text = "Post Reel", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            }
        }

        // Reel Creator Modal Sheet
        if (showCreateSheet) {
            ReelCreatorSheet(
                onPostReel = { clip, audioTitle, audioArtist, caption, filter, isCollab, coAuthor ->
                    viewModel.createAndPostReel(clip, audioTitle, audioArtist, caption, filter, isCollab, coAuthor)
                },
                onDismiss = { showCreateSheet = false }
            )
        }

        // Reel Comments Modal Sheet
        commentTargetReel?.let { targetReel ->
            ReelCommentsSheet(
                reel = targetReel,
                onDismiss = { commentTargetReel = null }
            )
        }
    }
}

@Composable
fun ReelPageItem(
    reel: Reel,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onComment: () -> Unit,
    onRemix: () -> Unit
) {
    val context = LocalContext.current
    val imageRes = rememberDrawableResId(context, reel.videoDrawableName)

    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_rot"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!reel.isLiked) onLike()
                    }
                )
            }
    ) {
        // Fullscreen Reel Visual Asset
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Reel Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient shadow overlays for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 0f,
                        endY = 1800f
                    )
                )
        )

        // Floating Right Action Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 85.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with Follow plus badge
            Box(contentAlignment = Alignment.BottomCenter) {
                AuraAvatar(
                    drawableName = reel.author.avatarDrawableName,
                    size = 46.dp,
                    hasGlowBorder = true
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(AuraHotPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            // Like Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.testTag("reel_like_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Reel",
                        tint = if (reel.isLiked) AuraHotPink else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "${reel.likesCount}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Comments Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onComment,
                    modifier = Modifier.testTag("reel_open_comments_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "${reel.commentsCount}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Remix Reel in Collab Studio
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemix) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Remix Reel",
                        tint = AuraNeonCyan,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Remix",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraNeonCyan
                )
            }

            // Save / Bookmark
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = if (reel.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save Reel",
                    tint = if (reel.isSaved) AuraEmerald else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Spinning Vinyl Sound Disc
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF151926))
                    .border(2.dp, AuraNeonViolet, CircleShape)
                    .rotate(discRotation),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = AuraNeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Left Info Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 85.dp)
        ) {
            // Dual Creators Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reel.author.handle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (reel.isCollaborative && reel.coAuthorName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraNeonCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = AuraNeonCyan, modifier = Modifier.size(11.dp))
                        Text(
                            text = "ft. ${reel.coAuthorName}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraNeonCyan,
                            modifier = Modifier.padding(start = 3.dp)
                        )
                    }
                }
            }

            // Caption
            Text(
                text = reel.caption,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Audio Track Bar
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(0.8.dp, AuraNeonViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = AuraNeonViolet,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${reel.audioTrackTitle} • ${reel.audioTrackArtist}",
                    fontSize = 11.sp,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
