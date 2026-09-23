package com.example.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.data.model.LiveStreamSession
import com.example.data.model.Post
import com.example.data.model.StoryItem
import com.example.data.model.UserProfile
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.components.AuraBadge
import com.example.ui.components.AuraEncryptedBadge
import com.example.ui.components.CreateOptionType
import com.example.ui.components.SponsoredAdCard
import com.example.ui.components.UnifiedCreateSheet
import com.example.ui.components.rememberDrawableResId
import com.example.ui.live.GoLiveSetupSheet
import com.example.ui.live.LiveBroadcastStudioDialog
import com.example.ui.live.LiveWatchDialog
import com.example.ui.reels.ReelCreatorSheet
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    viewModel: SocialViewModel,
    onNavigateToVault: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val posts by viewModel.feedPosts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val selectedVibe by viewModel.selectedVibe.collectAsStateWithLifecycle()
    val algoSettings by viewModel.algoSettings.collectAsStateWithLifecycle()
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val sponsoredAds by viewModel.sponsoredAds.collectAsStateWithLifecycle()

    val isRemixing by viewModel.isRemixing.collectAsStateWithLifecycle()
    val remixResult by viewModel.remixResult.collectAsStateWithLifecycle()
    val targetPostForRemix by viewModel.targetPostForRemix.collectAsStateWithLifecycle()

    var showTunerSheet by remember { mutableStateOf(false) }
    var commentPostTarget by remember { mutableStateOf<Post?>(null) }

    // Live Streaming State
    val activeLiveStream by viewModel.activeLiveStream.collectAsStateWithLifecycle()
    val watchingLiveStream by viewModel.watchingLiveStream.collectAsStateWithLifecycle()
    val liveComments by viewModel.liveComments.collectAsStateWithLifecycle()

    // Creator & Broadcast Sheets
    var showUnifiedCreateSheet by remember { mutableStateOf(false) }
    var showStoryCreatorSheet by remember { mutableStateOf(false) }
    var showPostCreatorSheet by remember { mutableStateOf(false) }
    var showReelCreatorSheet by remember { mutableStateOf(false) }
    var showGoLiveSetupSheet by remember { mutableStateOf(false) }
    var activeStoryViewerIndex by remember { mutableStateOf<Int?>(null) }

    val vibes = listOf("All", "Cyberpunk", "Neo-Chill", "Quantum Tech", "Ambient Art", "Deep Lore")

    Box(modifier = Modifier.fillMaxSize().background(AuraDarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AuraNeonViolet, AuraNeonCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AURA",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AuraNeonCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "AI FEED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraNeonCyan
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Unified Create (+) Button
                    IconButton(
                        onClick = { showUnifiedCreateSheet = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AuraNeonViolet, AuraHotPink))
                            )
                            .size(34.dp)
                            .testTag("open_unified_create_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Content",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Notification Center Icon with Realtime Badge
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.testTag("open_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(
                                        containerColor = AuraNeonCyan,
                                        contentColor = Color.Black
                                    ) {
                                        Text("$unreadNotifs", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Aura Notifications",
                                tint = if (unreadNotifs > 0) AuraNeonCyan else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Neural Algorithm Tuner Icon
                    IconButton(
                        onClick = { showTunerSheet = true },
                        modifier = Modifier.testTag("open_algo_tuner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Neural Feed Settings",
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Encrypted Direct Message Vault button
                    IconButton(
                        onClick = onNavigateToVault,
                        modifier = Modifier.testTag("open_vault_dm_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AuraHotPink,
                                    contentColor = Color.White
                                ) {
                                    Text("1", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "E2EE Vault Messages",
                                tint = AuraEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Feed Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_post_list"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Stories Tray
                item {
                    StoriesTray(
                        stories = stories,
                        onAddStory = { showStoryCreatorSheet = true },
                        onStoryClick = { story ->
                            val nonLiveStories = stories.filter { !it.isLiveCollab }
                            val idx = nonLiveStories.indexOfFirst { it.id == story.id }
                            if (idx >= 0) {
                                activeStoryViewerIndex = idx
                            }
                        },
                        onLiveClick = {
                            val liveSession = LiveStreamSession(
                                id = "stream_elena_marcus",
                                host = UserProfile("user_elena", "Elena Rostova", "@elena.synapse", "post_cyber_creator_1790200662289", true, 94),
                                coHost = UserProfile("user_marcus", "Marcus Vance", "@marcus.v", "post_crystal_city_1790200645179", true, 91),
                                title = "⚡ Live Neural Jam & 3D Volumetric Shaders",
                                category = "Creative Studio",
                                videoBackgroundDrawable = "reel_cyber_synth_1790201507030",
                                coHostBackgroundDrawable = "reel_neon_oasis_1790201522014",
                                viewerCount = 1420
                            )
                            viewModel.openWatchLive(liveSession)
                        }
                    )
                }

                // Quick Creation Action Bar
                item {
                    QuickCreateBar(
                        onStoryClick = { showStoryCreatorSheet = true },
                        onPostClick = { showPostCreatorSheet = true },
                        onReelClick = { showReelCreatorSheet = true },
                        onLiveClick = { showGoLiveSetupSheet = true }
                    )
                }

                // Vibe Filters Row
                item {
                    VibeFilterBar(
                        vibes = vibes,
                        selectedVibe = selectedVibe,
                        onVibeSelected = { viewModel.setVibe(it) }
                    )
                }

                // Post Cards with Sponsored Showcase Integration
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLike = { viewModel.toggleLike(post) },
                        onSave = { viewModel.toggleSave(post) },
                        onRemix = { viewModel.openRemixModal(post) },
                        onComment = { commentPostTarget = post }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Insert Sponsored Ad Showcase after the 1st post
                    if (post.id == posts.firstOrNull()?.id && sponsoredAds.isNotEmpty()) {
                        val ad = sponsoredAds.first()
                        SponsoredAdCard(
                            ad = ad,
                            onCtaClicked = {
                                // Trigger in-app toast for ad interaction
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Tuner Bottom Sheet
        if (showTunerSheet) {
            AlgorithmTunerSheet(
                settings = algoSettings,
                onSave = { viewModel.updateAlgorithmSettings(it) },
                onDismiss = { showTunerSheet = false }
            )
        }

        // AI Remix Bottom Sheet
        if (targetPostForRemix != null) {
            RemixModalSheet(
                post = targetPostForRemix!!,
                isRemixing = isRemixing,
                remixResult = remixResult,
                onRemixRequested = { targetVibe -> viewModel.triggerAiRemix(targetVibe) },
                onDismiss = { viewModel.closeRemixModal() }
            )
        }

        // Comments Bottom Sheet
        if (commentPostTarget != null) {
            CommentsSheet(
                post = commentPostTarget!!,
                onDismiss = { commentPostTarget = null }
            )
        }

        // Unified Creation Modal Sheet
        if (showUnifiedCreateSheet) {
            UnifiedCreateSheet(
                onDismiss = { showUnifiedCreateSheet = false },
                onSelectOption = { option ->
                    when (option) {
                        CreateOptionType.STORY -> showStoryCreatorSheet = true
                        CreateOptionType.POST -> showPostCreatorSheet = true
                        CreateOptionType.REEL -> showReelCreatorSheet = true
                        CreateOptionType.GO_LIVE -> showGoLiveSetupSheet = true
                    }
                }
            )
        }

        // Story Creator Modal Sheet
        if (showStoryCreatorSheet) {
            StoryCreatorSheet(
                onDismiss = { showStoryCreatorSheet = false },
                onPostStory = { img, caption, filter, music, sticker ->
                    viewModel.postStory(img, caption, filter, music, sticker)
                }
            )
        }

        // Feed Post Creator Modal Sheet
        if (showPostCreatorSheet) {
            PostCreatorSheet(
                onDismiss = { showPostCreatorSheet = false },
                onPublishPost = { img, caption, vibe, coAuthors, isEncrypted ->
                    viewModel.publishFeedPost(img, caption, vibe, coAuthors, isEncrypted)
                }
            )
        }

        // Reel Creator Modal Sheet
        if (showReelCreatorSheet) {
            ReelCreatorSheet(
                onDismiss = { showReelCreatorSheet = false },
                onPostReel = { clip, audioTitle, audioArtist, caption, filter, isCollab, coAuthor ->
                    viewModel.createAndPostReel(clip, audioTitle, audioArtist, caption, filter, isCollab, coAuthor)
                }
            )
        }

        // Go Live Setup Sheet
        if (showGoLiveSetupSheet) {
            GoLiveSetupSheet(
                onDismiss = { showGoLiveSetupSheet = false },
                onStartBroadcast = { title, category, allowCoHost ->
                    viewModel.startLiveBroadcast(title, category, allowCoHost)
                }
            )
        }

        // Active Live Broadcast Studio (Host Mode)
        activeLiveStream?.let { liveSession ->
            LiveBroadcastStudioDialog(
                session = liveSession,
                comments = liveComments,
                onSendComment = { viewModel.sendLiveComment(it, isHost = true) },
                onInviteCoHost = { viewModel.inviteCoHostToLive(it) },
                onEndBroadcast = { viewModel.endLiveBroadcast() }
            )
        }

        // Active Live Watch Dialog (Audience Mode)
        watchingLiveStream?.let { liveSession ->
            LiveWatchDialog(
                session = liveSession,
                comments = liveComments,
                onSendComment = { viewModel.sendLiveComment(it, isHost = false) },
                onClose = { viewModel.closeWatchLive() }
            )
        }

        // Fullscreen Immersive Story Viewer
        activeStoryViewerIndex?.let { startIndex ->
            val nonLiveStories = stories.filter { !it.isLiveCollab }
            if (nonLiveStories.isNotEmpty()) {
                StoryViewerDialog(
                    stories = nonLiveStories,
                    initialStoryIndex = startIndex,
                    onDismiss = { activeStoryViewerIndex = null },
                    onStorySeen = { viewModel.markStoryAsSeen(it) },
                    onReplyToStory = { story, replyText ->
                        viewModel.addStoryReply(story, replyText)
                    }
                )
            }
        }
    }
}

@Composable
fun QuickCreateBar(
    onStoryClick: () -> Unit,
    onPostClick: () -> Unit,
    onReelClick: () -> Unit,
    onLiveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Story button
        QuickCreatePill(
            label = "Story",
            icon = Icons.Default.CameraAlt,
            color = AuraHotPink,
            testTag = "quick_create_story_btn",
            onClick = onStoryClick,
            modifier = Modifier.weight(1f)
        )

        // Post button
        QuickCreatePill(
            label = "Post",
            icon = Icons.Default.PostAdd,
            color = AuraNeonCyan,
            testTag = "quick_create_post_btn",
            onClick = onPostClick,
            modifier = Modifier.weight(1f)
        )

        // Reel button
        QuickCreatePill(
            label = "Reel",
            icon = Icons.Default.Videocam,
            color = AuraNeonViolet,
            testTag = "quick_create_reel_btn",
            onClick = onReelClick,
            modifier = Modifier.weight(1f)
        )

        // Live button
        QuickCreatePill(
            label = "Live",
            icon = Icons.Default.LiveTv,
            color = Color(0xFFEF4444),
            testTag = "quick_create_live_btn",
            onClick = onLiveClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickCreatePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AuraDarkCard)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun StoriesTray(
    stories: List<StoryItem>,
    onAddStory: () -> Unit,
    onStoryClick: (StoryItem) -> Unit,
    onLiveClick: (StoryItem) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(stories) { story ->
            val isMyStory = story.id == "story_me"
            val isLive = story.isLiveCollab

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(68.dp)
                    .clickable {
                        when {
                            isMyStory -> onAddStory()
                            isLive -> onLiveClick(story)
                            else -> onStoryClick(story)
                        }
                    }
                    .testTag("story_item_${story.id}")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val ringBrush = when {
                        isLive -> Brush.sweepGradient(listOf(Color(0xFFEF4444), Color(0xFFFF007A), Color(0xFFEF4444)))
                        story.isSeen -> Brush.linearGradient(listOf(Color.DarkGray, Color(0xFF334155)))
                        else -> Brush.sweepGradient(
                            listOf(AuraNeonViolet, AuraHotPink, AuraNeonCyan, AuraNeonViolet)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .border(2.dp, ringBrush, CircleShape)
                            .padding(3.dp)
                    ) {
                        AuraAvatar(
                            drawableName = story.avatarDrawableName,
                            size = 56.dp,
                            isVerified = false
                        )
                    }

                    // My Story "+" badge overlay
                    if (isMyStory) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AuraNeonViolet, AuraNeonCyan)))
                                .border(1.5.dp, Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // LIVE badge overlay
                    if (isLive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "● LIVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = if (isMyStory) "Your Story" else story.creatorName.split(" ").first(),
                    fontSize = 11.sp,
                    color = if (story.isSeen && !isLive && !isMyStory) Color.Gray else Color.White,
                    maxLines = 1,
                    fontWeight = if (isLive) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VibeFilterBar(
    vibes: List<String>,
    selectedVibe: String,
    onVibeSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(vibes) { vibe ->
            val isSelected = selectedVibe == vibe
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) AuraNeonCyan.copy(alpha = 0.2f)
                        else AuraDarkCard
                    )
                    .border(
                        1.2.dp,
                        if (isSelected) AuraNeonCyan else Color(0xFF243048),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onVibeSelected(vibe) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = vibe,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onRemix: () -> Unit,
    onComment: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = rememberDrawableResId(context, post.imageDrawableName)
    val scope = rememberCoroutineScope()

    var showHeartBurst by remember { mutableStateOf(false) }
    val heartScale = remember { Animatable(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuraAvatar(
                        drawableName = post.author.avatarDrawableName,
                        size = 42.dp,
                        hasGlowBorder = post.isCollaborative,
                        isVerified = post.author.isVerified
                    )

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.author.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (post.coAuthors.isNotEmpty()) {
                                Text(
                                    text = " & ${post.coAuthors.joinToString(", ") { it.name }}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AuraNeonCyan,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.author.handle,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = " • ${post.timestampText}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Collaborative Badge / Vibe
                if (post.isCollaborative) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AuraNeonViolet.copy(alpha = 0.2f))
                            .border(1.dp, AuraNeonViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Collab Post",
                                tint = AuraNeonViolet,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "COLLAB",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraNeonViolet,
                                modifier = Modifier.padding(start = 3.dp)
                            )
                        }
                    }
                }
            }

            // Post Visual Media with double tap gesture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!post.isLiked) onLike()
                                showHeartBurst = true
                                scope.launch {
                                    heartScale.snapTo(0.2f)
                                    heartScale.animateTo(
                                        1.3f,
                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                                    )
                                    delay(400)
                                    showHeartBurst = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Post Artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Double tap heart burst animation
                if (showHeartBurst) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = AuraHotPink,
                        modifier = Modifier
                            .size(90.dp)
                            .scale(heartScale.value)
                    )
                }

                // AI Vibe Pill Overlay at top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, AuraNeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ ${post.vibeTag}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraNeonCyan
                    )
                }
            }

            // Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) AuraHotPink else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onComment,
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // AI Remix Button
                    IconButton(
                        onClick = onRemix,
                        modifier = Modifier.testTag("remix_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Remix",
                            tint = AuraNeonViolet,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onSave,
                    modifier = Modifier.testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) AuraNeonCyan else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Likes and AI affinity info
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${post.likesCount} sparks • ${post.commentsCount} comments",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // AI Affinity badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "${post.aiAffinityScore}% AI Match",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuraNeonCyan,
                            modifier = Modifier.padding(start = 3.dp)
                        )
                    }
                }

                // AI Explanation Banner
                Text(
                    text = post.aiExplanation,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )

                // Caption
                Text(
                    text = post.caption,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    lineHeight = 18.sp
                )

                // View comments button
                Text(
                    text = "View all ${post.commentsCount} comments & sentiment analysis...",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { onComment() }
                        .padding(top = 6.dp, bottom = 12.dp)
                )
            }
        }
    }
}
