package com.example.data.repository

import android.content.Context
import com.example.data.crypto.CryptoManager
import com.example.data.local.AppDatabase
import com.example.data.local.MessageEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PostEntity
import com.example.data.local.ReelEntity
import com.example.data.model.AuraNotification
import com.example.data.model.CanvasLayer
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.CollabActivityEvent
import com.example.data.model.CollabSession
import com.example.data.model.Collaborator
import com.example.data.model.Comment
import com.example.data.model.LayerType
import com.example.data.model.LiveComment
import com.example.data.model.LiveStreamSession
import com.example.data.model.NotificationType
import com.example.data.model.Post
import com.example.data.model.Reel
import com.example.data.model.SponsoredAd
import com.example.data.model.StoryItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SocialRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val postDao = db.postDao()
    private val messageDao = db.messageDao()
    private val reelDao = db.reelDao()
    private val notificationDao = db.notificationDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Current User Profile
    val currentUser = UserProfile(
        id = "user_me",
        name = "Nova Sterling",
        handle = "@novasterling",
        avatarDrawableName = "post_cyber_creator_1790200662289",
        isVerified = true,
        auraLevel = 98,
        bio = "Creative Technologist • Building multi-layer neural worlds & encrypted communications ✦",
        followersCount = 28400,
        followingCount = 384,
        e2eeKeyFingerprint = CryptoManager.getFingerprintSafetyNumber()
    )

    // Peers
    val peerElena = UserProfile(
        id = "user_elena",
        name = "Elena Rostova",
        handle = "@elena.synapse",
        avatarDrawableName = "post_cyber_creator_1790200662289",
        isVerified = true,
        auraLevel = 94,
        bio = "Neural Architect • Generative 3D & light sculptures"
    )

    val peerMarcus = UserProfile(
        id = "user_marcus",
        name = "Marcus Vance",
        handle = "@marcus.v",
        avatarDrawableName = "post_crystal_city_1790200645179",
        isVerified = true,
        auraLevel = 91,
        bio = "Sound design, spatial audio synthesis, ambient synthwave"
    )

    val peerSarah = UserProfile(
        id = "user_sarah",
        name = "Sarah Chen",
        handle = "@schen_art",
        avatarDrawableName = "post_aurora_vibes_1790200680892",
        isVerified = true,
        auraLevel = 96,
        bio = "Quantum optics researcher & digital impressionist"
    )

    // Toast event for real-time in-app notifications
    private val _inAppToastEvent = MutableSharedFlow<AuraNotification>(extraBufferCapacity = 5)
    val inAppToastEvent = _inAppToastEvent.asSharedFlow()

    // Sponsored Ads
    private val _sponsoredAds = MutableStateFlow(
        listOf(
            SponsoredAd(
                id = "ad_neural_01",
                brandName = "Synapse Optics",
                brandHandle = "@synapse.optics",
                brandAvatarDrawable = "post_crystal_city_1790200645179",
                headline = "Neural Retinal Projection Glasses",
                body = "Spatial computing eyewear engineered for zero-latency holographic creative collaboration. Experience true depth.",
                imageDrawableName = "ad_neural_lens_1790201536203",
                ctaText = "Explore Drop"
            )
        )
    )
    val sponsoredAds = _sponsoredAds.asStateFlow()

    // Collaborative Studio State
    private val _collabSession = MutableStateFlow(
        CollabSession(
            id = "session_live_01",
            title = "Project: Horizon Drift (Live Canvas)",
            baseImageResName = "post_crystal_city_1790200645179",
            collaborators = listOf(
                Collaborator("user_me", "You", "post_cyber_creator_1790200662289", 0xFFA855F7, "Editing composition", true, true),
                Collaborator("user_elena", "Elena R.", "post_cyber_creator_1790200662289", 0xFF06B6D4, "Adjusting neon glow shader", true, true),
                Collaborator("user_marcus", "Marcus V.", "post_crystal_city_1790200645179", 0xFFEC4899, "Adding spatial sound beat", true, false)
            ),
            layers = listOf(
                CanvasLayer("layer_1", LayerType.TEXT, "Header", "FUTURE SYNTHESIS", 20f, 40f, 1.2f, 0f, 0xFFFFFFFF, "Elena R."),
                CanvasLayer("layer_2", LayerType.STICKER, "Sticker", "✦ QUANTUM VIBE", 20f, 180f, 1.0f, -5f, 0xFF06B6D4, "Marcus V."),
                CanvasLayer("layer_3", LayerType.GLOW_EFFECT, "Shader", "PRISM IRIDESCENCE", 0f, 0f, 1.0f, 0f, 0xFFA855F7, "You")
            ),
            activityEvents = listOf(
                CollabActivityEvent("evt_1", "Elena R.", "joined the live canvas", "Just now"),
                CollabActivityEvent("evt_2", "Marcus V.", "pinned layer '✦ QUANTUM VIBE'", "1m ago"),
                CollabActivityEvent("evt_3", "You", "synchronized 256-bit session key", "2m ago")
            ),
            activeFilterName = "Prism Glow",
            filterIntensity = 0.85f,
            isVoiceHuddleActive = true
        )
    )
    val collabSession = _collabSession.asStateFlow()

    // Stories
    private val _stories = MutableStateFlow(
        listOf(
            StoryItem(
                id = "story_me",
                creatorName = "Your Story",
                creatorHandle = "@novasterling",
                avatarDrawableName = "post_cyber_creator_1790200662289",
                imageDrawableName = "post_crystal_city_1790200645179",
                isSeen = false,
                isLiveCollab = false,
                liveCollabParticipants = 0,
                caption = "Drafting new cyber architecture shaders in Aura Studio ✦",
                musicTrack = "Obsidian Skyline",
                stickerText = "✦ NEURAL LAB",
                timestampText = "Just now",
                filterName = "Normal"
            ),
            StoryItem(
                id = "story_live",
                creatorName = "Elena & Marcus",
                creatorHandle = "@elena.synapse",
                avatarDrawableName = "post_cyber_creator_1790200662289",
                imageDrawableName = "reel_cyber_synth_1790201507030",
                isSeen = false,
                isLiveCollab = true,
                liveCollabParticipants = 1420,
                caption = "⚡ Volumetric audio & 3D synth jamming live from Tokyo enclave!",
                musicTrack = "Cyber Kinetic Pulse (Spatial Mix)",
                stickerText = "🔴 LIVE SESSION",
                timestampText = "LIVE NOW",
                filterName = "Neon Glitch"
            ),
            StoryItem(
                id = "story_3",
                creatorName = "Sarah Chen",
                creatorHandle = "@schen_art",
                avatarDrawableName = "post_aurora_vibes_1790200680892",
                imageDrawableName = "post_aurora_vibes_1790200680892",
                isSeen = false,
                isLiveCollab = false,
                liveCollabParticipants = 0,
                caption = "Bioluminescent botanical garden simulation at 432Hz ✨",
                musicTrack = "Bioluminescent Dreams",
                stickerText = "✦ 432Hz HARMONIC",
                timestampText = "2h ago",
                filterName = "Prism Glow"
            ),
            StoryItem(
                id = "story_4",
                creatorName = "Marcus Vance",
                creatorHandle = "@marcus.v",
                avatarDrawableName = "post_crystal_city_1790200645179",
                imageDrawableName = "reel_neon_oasis_1790201522014",
                isSeen = true,
                isLiveCollab = false,
                liveCollabParticipants = 0,
                caption = "Late night stem testing. Sending cryptographic cipher keys to the guild.",
                musicTrack = "Sub-bass Resonance",
                stickerText = "⚡ CYPHER VAULT",
                timestampText = "5h ago",
                filterName = "Cyber Noir"
            )
        )
    )
    val stories = _stories.asStateFlow()

    // Live Streaming State
    private val _activeLiveStream = MutableStateFlow<LiveStreamSession?>(null)
    val activeLiveStream = _activeLiveStream.asStateFlow()

    private val _watchingLiveStream = MutableStateFlow<LiveStreamSession?>(null)
    val watchingLiveStream = _watchingLiveStream.asStateFlow()

    private val _liveComments = MutableStateFlow<List<LiveComment>>(emptyList())
    val liveComments = _liveComments.asStateFlow()

    private var liveAudienceJob: kotlinx.coroutines.Job? = null

    // Conversations
    private val _conversations = MutableStateFlow(
        listOf(
            ChatConversation(
                id = "convo_elena",
                participant = peerElena,
                lastMessageText = "The collaborative canvas is synced with 256-bit ratcheted encryption.",
                lastMessageTime = "12:42 PM",
                unreadCount = 1,
                isE2eeVerified = true,
                hasSelfDestructActive = true,
                safetyNumber = "49201 82941 77301 22910 84910",
                isGroup = false
            ),
            ChatConversation(
                id = "convo_marcus",
                participant = peerMarcus,
                lastMessageText = "Just dropped the new ambient frequency layer into the studio draft!",
                lastMessageTime = "11:20 AM",
                unreadCount = 0,
                isE2eeVerified = true,
                hasSelfDestructActive = false,
                safetyNumber = "18293 99401 22948 11940 33819",
                isGroup = false
            ),
            ChatConversation(
                id = "convo_group",
                participant = UserProfile(
                    id = "group_cipher",
                    name = "Cipher Guild ✦ (Collab)",
                    handle = "@guild.cipher",
                    avatarDrawableName = "post_crystal_city_1790200645179",
                    isVerified = true
                ),
                lastMessageText = "Marcus: Finalizing 432Hz spatial audio pass for our reel drop tonight.",
                lastMessageTime = "10:15 AM",
                unreadCount = 2,
                isE2eeVerified = true,
                hasSelfDestructActive = false,
                safetyNumber = "77391 22910 84910 49201 99120",
                isGroup = true,
                groupMembersCount = 4
            ),
            ChatConversation(
                id = "convo_sarah",
                participant = peerSarah,
                lastMessageText = "Our co-authored post reached #1 on the Quantum Tech feed ✦",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isE2eeVerified = true,
                hasSelfDestructActive = false,
                safetyNumber = "99210 33918 44021 77291 55019",
                isGroup = false
            )
        )
    )
    val conversations = _conversations.asStateFlow()

    init {
        repositoryScope.launch {
            seedInitialPostsIfEmpty()
            seedInitialMessagesIfEmpty()
            seedInitialReelsIfEmpty()
            seedInitialNotificationsIfEmpty()
        }
    }

    private suspend fun seedInitialPostsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = postDao.getAllPosts().first()
        if (existing.isEmpty()) {
            val initial = listOf(
                PostEntity(
                    id = "post_1",
                    authorName = "Elena Rostova",
                    authorHandle = "@elena.synapse",
                    authorAvatarRes = "post_cyber_creator_1790200662289",
                    coAuthorsFormatted = "Marcus Vance, Nova Sterling",
                    imageResName = "post_crystal_city_1790200645179",
                    caption = "Co-created with @marcus.v & @novasterling in Aura Live Studio. Crystalline metropolis folding reality into light corridors. 🌆✨",
                    timestampText = "15m ago",
                    likesCount = 1248,
                    commentsCount = 89,
                    vibeTag = "Cyberpunk",
                    aiAffinityScore = 99,
                    aiExplanation = "Deep semantic alignment with your interests in futuristic architecture and multi-creator artwork.",
                    isLiked = false,
                    isSaved = false,
                    isCollaborative = true
                ),
                PostEntity(
                    id = "post_2",
                    authorName = "Sarah Chen",
                    authorHandle = "@schen_art",
                    authorAvatarRes = "post_aurora_vibes_1790200680892",
                    coAuthorsFormatted = "",
                    imageResName = "post_aurora_vibes_1790200680892",
                    caption = "Aurora reflections captured over the mirror fjord. Spectral resonance tuned to 528Hz. What vibe do you feel here? 🌌",
                    timestampText = "1h ago",
                    likesCount = 3412,
                    commentsCount = 214,
                    vibeTag = "Neo-Chill",
                    aiAffinityScore = 96,
                    aiExplanation = "Recommended based on your recent ambient aesthetic mood and chill harmonic ratings.",
                    isLiked = true,
                    isSaved = true,
                    isCollaborative = false
                ),
                PostEntity(
                    id = "post_3",
                    authorName = "Nova Sterling",
                    authorHandle = "@novasterling",
                    authorAvatarRes = "post_cyber_creator_1790200662289",
                    coAuthorsFormatted = "Elena Rostova",
                    imageResName = "post_cyber_creator_1790200662289",
                    caption = "Neural lens active. Synchronized cryptographic presence with @elena.synapse for tonight's visual drop. ⚡",
                    timestampText = "3h ago",
                    likesCount = 892,
                    commentsCount = 47,
                    vibeTag = "Quantum Tech",
                    aiAffinityScore = 94,
                    aiExplanation = "Your co-authored creation trending in the Quantum Tech graph.",
                    isLiked = false,
                    isSaved = false,
                    isCollaborative = true
                )
            )
            postDao.insertPosts(initial)
        }
    }

    private suspend fun seedInitialMessagesIfEmpty() = withContext(Dispatchers.IO) {
        val existing = messageDao.getMessagesForConversation("convo_elena").first()
        if (existing.isEmpty()) {
            val now = System.currentTimeMillis()
            val initialMsgs = listOf(
                MessageEntity(
                    id = "msg_1",
                    conversationId = "convo_elena",
                    senderId = "user_elena",
                    senderName = "Elena Rostova",
                    isFromMe = false,
                    encryptedPayload = CryptoManager.encryptMessage("Hey Nova! Ready to finalize the neural landscape canvas?"),
                    decryptedText = "Hey Nova! Ready to finalize the neural landscape canvas?",
                    timestamp = now - 120000,
                    timestampFormatted = "12:40 PM",
                    isEncrypted = true,
                    selfDestructSeconds = 0,
                    expiresAt = 0L,
                    isIncinerated = false,
                    isVoiceNote = false,
                    voiceDurationSeconds = 0,
                    deliveryStatus = "READ"
                ),
                MessageEntity(
                    id = "msg_2",
                    conversationId = "convo_elena",
                    senderId = "user_me",
                    senderName = "Nova Sterling",
                    isFromMe = true,
                    encryptedPayload = CryptoManager.encryptMessage("Just opened Studio! Marcus is adding the ambient audio frequency."),
                    decryptedText = "Just opened Studio! Marcus is adding the ambient audio frequency.",
                    timestamp = now - 60000,
                    timestampFormatted = "12:41 PM",
                    isEncrypted = true,
                    selfDestructSeconds = 0,
                    expiresAt = 0L,
                    isIncinerated = false,
                    isVoiceNote = false,
                    voiceDurationSeconds = 0,
                    deliveryStatus = "READ"
                ),
                MessageEntity(
                    id = "msg_3",
                    conversationId = "convo_elena",
                    senderId = "user_elena",
                    senderName = "Elena Rostova",
                    isFromMe = false,
                    encryptedPayload = CryptoManager.encryptMessage("The collaborative canvas is synced with 256-bit ratcheted encryption."),
                    decryptedText = "The collaborative canvas is synced with 256-bit ratcheted encryption.",
                    timestamp = now - 30000,
                    timestampFormatted = "12:42 PM",
                    isEncrypted = true,
                    selfDestructSeconds = 0,
                    expiresAt = 0L,
                    isIncinerated = false,
                    isVoiceNote = false,
                    voiceDurationSeconds = 0,
                    reaction = "❤️",
                    deliveryStatus = "READ"
                )
            )
            messageDao.insertMessages(initialMsgs)
        }
    }

    private suspend fun seedInitialReelsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = reelDao.getAllReels().first()
        if (existing.isEmpty()) {
            val initial = listOf(
                ReelEntity(
                    id = "reel_1",
                    authorName = "Elena Rostova",
                    authorHandle = "@elena.synapse",
                    authorAvatarRes = "post_cyber_creator_1790200662289",
                    videoDrawableName = "reel_cyber_synth_1790201507030",
                    audioTrackTitle = "Cyber Kinetic Pulse (Spatial Mix)",
                    audioTrackArtist = "Marcus Vance ft. Nova",
                    caption = "Synchronized kinetic movement with volumetric laser projections. Co-created in Aura Studio! ⚡✨",
                    likesCount = 4820,
                    commentsCount = 312,
                    sharesCount = 184,
                    remixesCount = 56,
                    isLiked = false,
                    isSaved = false,
                    filterName = "Neon Glitch",
                    durationSec = 15,
                    isCollaborative = true,
                    coAuthorName = "Nova Sterling"
                ),
                ReelEntity(
                    id = "reel_2",
                    authorName = "Sarah Chen",
                    authorHandle = "@schen_art",
                    authorAvatarRes = "post_aurora_vibes_1790200680892",
                    videoDrawableName = "reel_neon_oasis_1790201522014",
                    audioTrackTitle = "432Hz Bioluminescent Dreams",
                    audioTrackArtist = "Sarah Chen (Original Sound)",
                    caption = "Floating botanical oasis in deep cyberspace. Turn sound on for binaural harmonic resonance 🌿🌌",
                    likesCount = 9240,
                    commentsCount = 580,
                    sharesCount = 640,
                    remixesCount = 89,
                    isLiked = true,
                    isSaved = true,
                    filterName = "Prism Glow",
                    durationSec = 22,
                    isCollaborative = false,
                    coAuthorName = null
                ),
                ReelEntity(
                    id = "reel_3",
                    authorName = "Marcus Vance",
                    authorHandle = "@marcus.v",
                    authorAvatarRes = "post_crystal_city_1790200645179",
                    videoDrawableName = "post_crystal_city_1790200645179",
                    audioTrackTitle = "Obsidian Skyline Resonance",
                    audioTrackArtist = "Marcus Vance",
                    caption = "Speed flying through the crystalline megastructure at midnight. Real-time shader render. 🏙️⚡",
                    likesCount = 3190,
                    commentsCount = 198,
                    sharesCount = 142,
                    remixesCount = 28,
                    isLiked = false,
                    isSaved = false,
                    filterName = "Cyber Noir",
                    durationSec = 18,
                    isCollaborative = true,
                    coAuthorName = "Elena Rostova"
                )
            )
            reelDao.insertReels(initial)
        }
    }

    private suspend fun seedInitialNotificationsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = notificationDao.getAllNotifications().first()
        if (existing.isEmpty()) {
            val initial = listOf(
                NotificationEntity(
                    id = "notif_1",
                    type = NotificationType.COLLAB_INVITE.name,
                    actorName = "Elena Rostova",
                    actorAvatarDrawable = "post_cyber_creator_1790200662289",
                    title = "Live Studio Invitation",
                    description = "invited you to co-author 'Quantum Neon Shards' in Aura Studio",
                    timestampText = "5m ago",
                    isRead = false,
                    actionLabel = "Join Canvas",
                    targetRoute = "collab",
                    targetId = "session_live_01"
                ),
                NotificationEntity(
                    id = "notif_2",
                    type = NotificationType.ENCRYPTED_MESSAGE.name,
                    actorName = "Marcus Vance",
                    actorAvatarDrawable = "post_crystal_city_1790200645179",
                    title = "New Encrypted Ephemeral Message",
                    description = "sent a 256-bit sealed note (expires in 30 seconds)",
                    timestampText = "20m ago",
                    isRead = false,
                    actionLabel = "Open Vault",
                    targetRoute = "vault",
                    targetId = "convo_marcus"
                ),
                NotificationEntity(
                    id = "notif_3",
                    type = NotificationType.SPARK_LIKE.name,
                    actorName = "Sarah Chen",
                    actorAvatarDrawable = "post_aurora_vibes_1790200680892",
                    title = "Sparks Received",
                    description = "sparked your collaborative reel 'Cyber Kinetic Pulse'",
                    timestampText = "1h ago",
                    isRead = true,
                    actionLabel = "View Reel",
                    targetRoute = "reels",
                    targetId = "reel_1"
                ),
                NotificationEntity(
                    id = "notif_4",
                    type = NotificationType.AI_REMIX.name,
                    actorName = "Aura Neural Engine",
                    actorAvatarDrawable = "post_cyber_creator_1790200662289",
                    title = "Neural Remix Generated",
                    description = "Your artwork was remixed into 'Deep Lore' aesthetic by 4 co-creators",
                    timestampText = "3h ago",
                    isRead = true,
                    actionLabel = "Inspect",
                    targetRoute = "feed",
                    targetId = "post_1"
                )
            )
            notificationDao.insertNotifications(initial)
        }
    }

    // Reels API
    fun getAllReels(): Flow<List<Reel>> {
        return reelDao.getAllReels().map { entities ->
            entities.map { entity ->
                val author = when (entity.authorHandle) {
                    "@elena.synapse" -> peerElena
                    "@marcus.v" -> peerMarcus
                    "@schen_art" -> peerSarah
                    else -> currentUser
                }
                Reel(
                    id = entity.id,
                    author = author,
                    videoDrawableName = entity.videoDrawableName,
                    audioTrackTitle = entity.audioTrackTitle,
                    audioTrackArtist = entity.audioTrackArtist,
                    caption = entity.caption,
                    likesCount = entity.likesCount,
                    commentsCount = entity.commentsCount,
                    sharesCount = entity.sharesCount,
                    remixesCount = entity.remixesCount,
                    isLiked = entity.isLiked,
                    isSaved = entity.isSaved,
                    filterName = entity.filterName,
                    durationSec = entity.durationSec,
                    isCollaborative = entity.isCollaborative,
                    coAuthorName = entity.coAuthorName
                )
            }
        }
    }

    suspend fun toggleReelLike(id: String, currentLiked: Boolean) = withContext(Dispatchers.IO) {
        val newLiked = !currentLiked
        val delta = if (newLiked) 1 else -1
        reelDao.updateLike(id, newLiked, delta)
    }

    suspend fun toggleReelSave(id: String, currentSaved: Boolean) = withContext(Dispatchers.IO) {
        reelDao.updateSave(id, !currentSaved)
    }

    suspend fun createAndPostReel(
        videoDrawable: String,
        audioTitle: String,
        audioArtist: String,
        caption: String,
        filterName: String,
        isCollab: Boolean,
        coAuthorName: String?
    ) = withContext(Dispatchers.IO) {
        val newReel = ReelEntity(
            id = "reel_${System.currentTimeMillis()}",
            authorName = currentUser.name,
            authorHandle = currentUser.handle,
            authorAvatarRes = currentUser.avatarDrawableName,
            videoDrawableName = videoDrawable,
            audioTrackTitle = audioTitle.ifBlank { "Original Audio • Nova Sterling" },
            audioTrackArtist = audioArtist.ifBlank { "Nova Sterling" },
            caption = caption.ifBlank { "Live creative capture from Aura Reels Studio! ✦" },
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            remixesCount = 0,
            isLiked = true,
            isSaved = false,
            filterName = filterName,
            durationSec = 15,
            isCollaborative = isCollab,
            coAuthorName = coAuthorName
        )
        reelDao.insertReel(newReel)

        // Post notification
        val notif = AuraNotification(
            id = "notif_${System.currentTimeMillis()}",
            type = NotificationType.SYSTEM_ALERT,
            actorName = "Aura Engine",
            actorAvatarDrawable = currentUser.avatarDrawableName,
            title = "Reel Published Live!",
            description = "Your reel '$audioTitle' is now streaming across the neural feed.",
            timestampText = "Just now",
            isRead = false,
            actionLabel = "Watch",
            targetRoute = "reels"
        )
        addNotification(notif)
    }

    // Notifications API
    fun getAllNotifications(): Flow<List<AuraNotification>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { entity ->
                val type = try {
                    NotificationType.valueOf(entity.type)
                } catch (e: Exception) {
                    NotificationType.SYSTEM_ALERT
                }
                AuraNotification(
                    id = entity.id,
                    type = type,
                    actorName = entity.actorName,
                    actorAvatarDrawable = entity.actorAvatarDrawable,
                    title = entity.title,
                    description = entity.description,
                    timestampText = entity.timestampText,
                    isRead = entity.isRead,
                    actionLabel = entity.actionLabel,
                    targetRoute = entity.targetRoute,
                    targetId = entity.targetId
                )
            }
        }
    }

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun addNotification(notification: AuraNotification) = withContext(Dispatchers.IO) {
        val entity = NotificationEntity(
            id = notification.id,
            type = notification.type.name,
            actorName = notification.actorName,
            actorAvatarDrawable = notification.actorAvatarDrawable,
            title = notification.title,
            description = notification.description,
            timestampText = notification.timestampText,
            isRead = notification.isRead,
            actionLabel = notification.actionLabel,
            targetRoute = notification.targetRoute,
            targetId = notification.targetId
        )
        notificationDao.insertNotification(entity)
        _inAppToastEvent.emit(notification)
    }

    // Posts API
    fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { entity ->
                val author = when (entity.authorHandle) {
                    "@elena.synapse" -> peerElena
                    "@marcus.v" -> peerMarcus
                    "@schen_art" -> peerSarah
                    else -> currentUser
                }
                val coAuthors = if (entity.coAuthorsFormatted.isBlank()) emptyList()
                else entity.coAuthorsFormatted.split(",").map { name ->
                    when (name.trim()) {
                        "Elena Rostova" -> peerElena
                        "Marcus Vance" -> peerMarcus
                        "Sarah Chen" -> peerSarah
                        else -> currentUser
                    }
                }

                Post(
                    id = entity.id,
                    author = author,
                    coAuthors = coAuthors,
                    imageDrawableName = entity.imageResName,
                    caption = entity.caption,
                    timestampText = entity.timestampText,
                    likesCount = entity.likesCount,
                    commentsCount = entity.commentsCount,
                    vibeTag = entity.vibeTag,
                    aiAffinityScore = entity.aiAffinityScore,
                    aiExplanation = entity.aiExplanation,
                    isLiked = entity.isLiked,
                    isSaved = entity.isSaved,
                    comments = listOf(
                        Comment("c1", "Marcus Vance", "@marcus.v", "The iridescence shader on this is pristine. Did you use the 432Hz spatial pulse?", "12m ago", "Constructive", 14),
                        Comment("c2", "Sarah Chen", "@schen_art", "Pure metaphysical elevation. Love the co-authorship vibe! ✦", "8m ago", "Inspiring", 9),
                        Comment("c3", "Elena Rostova", "@elena.synapse", "Collaborating on this live in Studio was effortless with the CRDT sync.", "2m ago", "Philosophical", 21)
                    ),
                    isCollaborative = entity.isCollaborative
                )
            }
        }
    }

    suspend fun toggleLike(postId: String, currentLiked: Boolean): Unit = withContext(Dispatchers.IO) {
        postDao.toggleLike(postId, !currentLiked)
    }

    suspend fun toggleSave(postId: String, currentSaved: Boolean): Unit = withContext(Dispatchers.IO) {
        postDao.toggleSave(postId, !currentSaved)
    }

    suspend fun publishCollaborativePost(
        title: String,
        caption: String,
        imageRes: String,
        coAuthors: List<String>,
        vibe: String
    ) = withContext(Dispatchers.IO) {
        val newPost = PostEntity(
            id = "post_${System.currentTimeMillis()}",
            authorName = currentUser.name,
            authorHandle = currentUser.handle,
            authorAvatarRes = currentUser.avatarDrawableName,
            coAuthorsFormatted = coAuthors.joinToString(", "),
            imageResName = imageRes,
            caption = caption,
            timestampText = "Just now",
            likesCount = 1,
            commentsCount = 0,
            vibeTag = vibe,
            aiAffinityScore = 100,
            aiExplanation = "Co-created by you in real-time Aura Studio.",
            isLiked = true,
            isSaved = false,
            isCollaborative = true
        )
        postDao.insertPost(newPost)
    }

    suspend fun publishFeedPost(
        imageRes: String,
        caption: String,
        vibe: String,
        coAuthors: List<String>,
        isCipherGuildOnly: Boolean
    ) = withContext(Dispatchers.IO) {
        val newPost = PostEntity(
            id = "post_${System.currentTimeMillis()}",
            authorName = currentUser.name,
            authorHandle = currentUser.handle,
            authorAvatarRes = currentUser.avatarDrawableName,
            coAuthorsFormatted = if (coAuthors.isNotEmpty()) coAuthors.joinToString(", ") else "",
            imageResName = imageRes,
            caption = caption,
            timestampText = "Just now",
            likesCount = 1,
            commentsCount = 0,
            vibeTag = vibe,
            aiAffinityScore = 99,
            aiExplanation = if (isCipherGuildOnly) "Encrypted to Cipher Guild members only." else "Freshly broadcasted to your neural followers.",
            isLiked = true,
            isSaved = false,
            isCollaborative = coAuthors.isNotEmpty()
        )
        postDao.insertPost(newPost)

        val notif = AuraNotification(
            id = "notif_post_${System.currentTimeMillis()}",
            type = NotificationType.SYSTEM_ALERT,
            actorName = currentUser.name,
            actorAvatarDrawable = currentUser.avatarDrawableName,
            title = "Post Published Live!",
            description = "Your post '$caption' is now streaming across the $vibe feed.",
            timestampText = "Just now",
            isRead = false,
            actionLabel = "View",
            targetRoute = "feed"
        )
        addNotification(notif)
    }

    fun postStory(
        imageDrawable: String,
        caption: String,
        filterName: String,
        musicTrack: String?,
        stickerText: String?
    ) {
        val myStory = StoryItem(
            id = "story_me",
            creatorName = "Your Story",
            creatorHandle = currentUser.handle,
            avatarDrawableName = currentUser.avatarDrawableName,
            imageDrawableName = imageDrawable,
            isSeen = false,
            isLiveCollab = false,
            liveCollabParticipants = 0,
            caption = caption,
            musicTrack = musicTrack,
            stickerText = stickerText,
            timestampText = "Just now",
            filterName = filterName
        )
        val currentList = _stories.value.filter { it.id != "story_me" }
        _stories.value = listOf(myStory) + currentList

        repositoryScope.launch {
            val notif = AuraNotification(
                id = "notif_story_${System.currentTimeMillis()}",
                type = NotificationType.SYSTEM_ALERT,
                actorName = "Aura Story Engine",
                actorAvatarDrawable = currentUser.avatarDrawableName,
                title = "Story Published ✦",
                description = "Your 24-hour story is now active on followers' stories rail.",
                timestampText = "Just now",
                isRead = false,
                actionLabel = "Watch",
                targetRoute = "feed"
            )
            addNotification(notif)
        }
    }

    fun markStoryAsSeen(storyId: String) {
        _stories.value = _stories.value.map {
            if (it.id == storyId) it.copy(isSeen = true) else it
        }
    }

    suspend fun addStoryReply(story: StoryItem, replyText: String) = withContext(Dispatchers.IO) {
        val targetConvoId = when (story.creatorHandle) {
            "@elena.synapse" -> "convo_elena"
            "@marcus.v" -> "convo_marcus"
            "@schen_art" -> "convo_sarah"
            else -> "convo_elena"
        }
        val fullText = "Replied to story: '$replyText'"
        val encrypted = CryptoManager.encryptMessage(fullText)
        val replyMsg = MessageEntity(
            id = "msg_story_${System.currentTimeMillis()}",
            conversationId = targetConvoId,
            senderId = currentUser.id,
            senderName = currentUser.name,
            isFromMe = true,
            encryptedPayload = encrypted,
            decryptedText = fullText,
            timestamp = System.currentTimeMillis(),
            timestampFormatted = "Just now",
            isEncrypted = true,
            selfDestructSeconds = 0,
            expiresAt = 0L,
            isIncinerated = false,
            isVoiceNote = false,
            voiceDurationSeconds = 0,
            deliveryStatus = "SENT",
            attachmentDrawable = story.imageDrawableName
        )
        messageDao.insertMessage(replyMsg)
    }

    // Live Streaming Engine
    fun startLiveBroadcast(title: String, category: String, allowCoHost: Boolean) {
        val session = LiveStreamSession(
            id = "live_${System.currentTimeMillis()}",
            host = currentUser,
            coHost = null,
            title = title.ifBlank { "⚡ Live Neural Stream & Synthesizer Jam" },
            category = category.ifBlank { "Creative Studio" },
            viewerCount = 1280,
            isBroadcasting = true,
            videoBackgroundDrawable = "reel_cyber_synth_1790201507030",
            coHostBackgroundDrawable = "reel_neon_oasis_1790201522014"
        )
        _activeLiveStream.value = session
        _liveComments.value = listOf(
            LiveComment("c_init_1", "Elena Rostova", "post_cyber_creator_1790200662289", "You're LIVE! The video bitrate is crystal clean 🔥", false, true),
            LiveComment("c_init_2", "Marcus Vance", "post_crystal_city_1790200645179", "Audio frequency synced via 432Hz spatial bus ✦", false, false)
        )
        startAudienceSimulation()

        repositoryScope.launch {
            val notif = AuraNotification(
                id = "notif_live_${System.currentTimeMillis()}",
                type = NotificationType.SYSTEM_ALERT,
                actorName = "Broadcast Center",
                actorAvatarDrawable = currentUser.avatarDrawableName,
                title = "Live Broadcast Active 🔴",
                description = "Broadcasting '$title' to 1,280+ live viewers.",
                timestampText = "Just now",
                isRead = false,
                actionLabel = "Studio",
                targetRoute = "feed"
            )
            addNotification(notif)
        }
    }

    fun openWatchLive(stream: LiveStreamSession) {
        _watchingLiveStream.value = stream
        _liveComments.value = listOf(
            LiveComment("c_watch_1", "Sarah Chen", "post_aurora_vibes_1790200680892", "Loving the chromatic dispersion layers! 🌿✨", false, true),
            LiveComment("c_watch_2", "Cipher Guild", "post_crystal_city_1790200645179", "Over 1.4k peers tuning in from Europe & Tokyo nodes ⚡", false, false),
            LiveComment("c_watch_3", "Elena Rostova", "post_cyber_creator_1790200662289", "Requesting co-host split stream in 1 minute!", false, false)
        )
        startAudienceSimulation()
    }

    fun closeWatchLive() {
        liveAudienceJob?.cancel()
        liveAudienceJob = null
        _watchingLiveStream.value = null
    }

    fun inviteCoHostToLive(coHostUser: UserProfile) {
        val current = _activeLiveStream.value ?: return
        _activeLiveStream.value = current.copy(coHost = coHostUser)
        val systemMsg = LiveComment(
            id = "c_sys_${System.currentTimeMillis()}",
            senderName = "System",
            senderAvatar = coHostUser.avatarDrawableName,
            text = "✦ ${coHostUser.name} joined as Co-Host in Split-Screen mode!",
            isHost = false,
            isSparked = true
        )
        _liveComments.value = _liveComments.value + systemMsg
    }

    fun sendLiveComment(text: String, isHost: Boolean = false) {
        if (text.isBlank()) return
        val newComment = LiveComment(
            id = "lc_${System.currentTimeMillis()}",
            senderName = currentUser.name,
            senderAvatar = currentUser.avatarDrawableName,
            text = text,
            isHost = isHost,
            isSparked = isHost
        )
        _liveComments.value = _liveComments.value + newComment
    }

    fun endLiveBroadcast() {
        liveAudienceJob?.cancel()
        liveAudienceJob = null
        _activeLiveStream.value = null
        _liveComments.value = emptyList()
    }

    private fun startAudienceSimulation() {
        liveAudienceJob?.cancel()
        liveAudienceJob = repositoryScope.launch {
            val sampleComments = listOf(
                "Elena: Shader rendering looks unreal at 120fps! 🔥",
                "Marcus: Stems synced! Let's drop the sub-bass pulse ⚡",
                "Sarah: The ambient particle lighting is mesmerizing 🌌",
                "Kai: Just sent 500 Sparks to support the studio! ✦",
                "Devin: Is this encrypted ratcheting stream or WebRTC?",
                "Maya: Co-authorship in real time is the future of art!",
                "Nova: Audio sounds huge on headphones ✨",
                "Ren: 1.5k viewers! Trending #1 in Cyberpunk 🚀"
            )
            var index = 0
            while (true) {
                delay(3500)
                val raw = sampleComments[index % sampleComments.size]
                val parts = raw.split(": ", limit = 2)
                val name = parts[0]
                val content = if (parts.size > 1) parts[1] else raw
                val avatar = when (name) {
                    "Elena" -> "post_cyber_creator_1790200662289"
                    "Marcus" -> "post_crystal_city_1790200645179"
                    "Sarah" -> "post_aurora_vibes_1790200680892"
                    else -> "post_cyber_creator_1790200662289"
                }

                val simulatedComment = LiveComment(
                    id = "sim_${System.currentTimeMillis()}",
                    senderName = name,
                    senderAvatar = avatar,
                    text = content,
                    isHost = false,
                    isSparked = content.contains("🔥") || content.contains("✦") || content.contains("⚡")
                )
                _liveComments.value = (_liveComments.value + simulatedComment).takeLast(25)

                // update viewer count fluctuating slightly
                _activeLiveStream.value?.let { active ->
                    _activeLiveStream.value = active.copy(viewerCount = active.viewerCount + ((-5..12).random()))
                }
                _watchingLiveStream.value?.let { watching ->
                    _watchingLiveStream.value = watching.copy(viewerCount = watching.viewerCount + ((-4..15).random()))
                }
                index++
            }
        }
    }

    // Encrypted Chat API
    fun getMessagesForConversation(convoId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(convoId).map { entities ->
            entities.map { entity ->
                val decrypted = if (entity.isIncinerated) "[Self-destructed message incinerated]"
                else CryptoManager.decryptMessage(entity.encryptedPayload)

                ChatMessage(
                    id = entity.id,
                    conversationId = entity.conversationId,
                    senderId = entity.senderId,
                    senderName = entity.senderName,
                    isFromMe = entity.isFromMe,
                    encryptedPayload = entity.encryptedPayload,
                    decryptedText = decrypted,
                    timestamp = entity.timestamp,
                    timestampFormatted = entity.timestampFormatted,
                    isEncrypted = entity.isEncrypted,
                    selfDestructSeconds = entity.selfDestructSeconds,
                    expiresAt = entity.expiresAt,
                    isIncinerated = entity.isIncinerated,
                    isVoiceNote = entity.isVoiceNote,
                    voiceDurationSeconds = entity.voiceDurationSeconds,
                    reaction = entity.reaction,
                    deliveryStatus = entity.deliveryStatus,
                    attachmentDrawable = entity.attachmentDrawable
                )
            }
        }
    }

    suspend fun toggleMessageReaction(messageId: String, emoji: String) = withContext(Dispatchers.IO) {
        messageDao.updateReaction(messageId, emoji)
    }

    suspend fun sendEncryptedVoiceNote(convoId: String, durationSec: Int) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val plain = "🎤 [Encrypted Voice Note • ${durationSec}s]"
        val encrypted = CryptoManager.encryptMessage(plain)

        val message = MessageEntity(
            id = "msg_vn_${now}",
            conversationId = convoId,
            senderId = currentUser.id,
            senderName = currentUser.name,
            isFromMe = true,
            encryptedPayload = encrypted,
            decryptedText = plain,
            timestamp = now,
            timestampFormatted = "Just now",
            isEncrypted = true,
            selfDestructSeconds = 0,
            expiresAt = 0L,
            isIncinerated = false,
            isVoiceNote = true,
            voiceDurationSeconds = durationSec,
            deliveryStatus = "SENT"
        )
        messageDao.insertMessage(message)
    }

    suspend fun sendEncryptedMessage(
        convoId: String,
        plainText: String,
        selfDestructSec: Int = 0
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val encryptedPayload = CryptoManager.encryptMessage(plainText)
        val expiresAt = if (selfDestructSec > 0) now + (selfDestructSec * 1000L) else 0L

        val message = MessageEntity(
            id = "msg_${now}",
            conversationId = convoId,
            senderId = currentUser.id,
            senderName = currentUser.name,
            isFromMe = true,
            encryptedPayload = encryptedPayload,
            decryptedText = plainText,
            timestamp = now,
            timestampFormatted = "Just now",
            isEncrypted = true,
            selfDestructSeconds = selfDestructSec,
            expiresAt = expiresAt,
            isIncinerated = false,
            isVoiceNote = false,
            voiceDurationSeconds = 0,
            deliveryStatus = "SENT"
        )
        messageDao.insertMessage(message)

        // Simulate Peer Typing & Encrypted Reply
        repositoryScope.launch {
            setConversationTyping(convoId, true, "Elena is typing...")
            delay(1800)
            setConversationTyping(convoId, false, "")

            val replyText = when {
                plainText.contains("studio", ignoreCase = true) || plainText.contains("canvas", ignoreCase = true) ->
                    "I just reviewed the latest layer update! It renders flawlessly at 120fps."
                plainText.contains("key", ignoreCase = true) || plainText.contains("safety", ignoreCase = true) ->
                    "Confirmed! My local safety number hash matches yours: 49201 82941."
                plainText.contains("reel", ignoreCase = true) ->
                    "The spatial audio mix for our collaborative reel is ready to drop! ✦"
                else ->
                    "Cryptographically received and verified on my secure enclave. Let's create!"
            }

            val replyEncrypted = CryptoManager.encryptMessage(replyText)
            val replyMessage = MessageEntity(
                id = "msg_reply_${System.currentTimeMillis()}",
                conversationId = convoId,
                senderId = "user_elena",
                senderName = "Elena Rostova",
                isFromMe = false,
                encryptedPayload = replyEncrypted,
                decryptedText = replyText,
                timestamp = System.currentTimeMillis(),
                timestampFormatted = "Just now",
                isEncrypted = true,
                selfDestructSeconds = if (selfDestructSec > 0) selfDestructSec else 0,
                expiresAt = if (selfDestructSec > 0) System.currentTimeMillis() + (selfDestructSec * 1000L) else 0L,
                isIncinerated = false,
                isVoiceNote = false,
                voiceDurationSeconds = 0,
                deliveryStatus = "READ"
            )
            messageDao.insertMessage(replyMessage)

            // Trigger notification
            val notif = AuraNotification(
                id = "notif_msg_${System.currentTimeMillis()}",
                type = NotificationType.ENCRYPTED_MESSAGE,
                actorName = "Elena Rostova",
                actorAvatarDrawable = "post_cyber_creator_1790200662289",
                title = "Encrypted Reply Received",
                description = "Elena sent a sealed response: '$replyText'",
                timestampText = "Just now",
                isRead = false,
                actionLabel = "Reply",
                targetRoute = "vault",
                targetId = convoId
            )
            addNotification(notif)
        }
    }

    private fun setConversationTyping(convoId: String, isTyping: Boolean, statusText: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == convoId) it.copy(isTyping = isTyping, typingStatusText = statusText) else it
        }
    }

    suspend fun incinerateMessage(id: String) = withContext(Dispatchers.IO) {
        messageDao.incinerateMessage(id)
    }

    // Collaborative Studio Live Updates
    fun updateCollabLayerText(layerId: String, newText: String) {
        val current = _collabSession.value
        val updatedLayers = current.layers.map {
            if (it.id == layerId) it.copy(content = newText, lastEditedBy = "You") else it
        }
        val newEvent = CollabActivityEvent("evt_${System.currentTimeMillis()}", "You", "edited layer '$newText'", "Just now")
        _collabSession.value = current.copy(
            layers = updatedLayers,
            activityEvents = listOf(newEvent) + current.activityEvents.take(4)
        )
    }

    fun updateCollabFilter(filterName: String, intensity: Float) {
        val current = _collabSession.value
        val newEvent = CollabActivityEvent("evt_${System.currentTimeMillis()}", "You", "applied filter $filterName (${(intensity * 100).toInt()}%)", "Just now")
        _collabSession.value = current.copy(
            activeFilterName = filterName,
            filterIntensity = intensity,
            activityEvents = listOf(newEvent) + current.activityEvents.take(4)
        )
    }

    fun addCollabLayer(type: LayerType, title: String, content: String, color: Long) {
        val current = _collabSession.value
        val newLayer = CanvasLayer(
            id = "layer_${System.currentTimeMillis()}",
            type = type,
            title = title,
            content = content,
            offsetX = 30f + (current.layers.size * 15f),
            offsetY = 120f + (current.layers.size * 25f),
            colorHex = color,
            lastEditedBy = "You"
        )
        val newEvent = CollabActivityEvent("evt_${System.currentTimeMillis()}", "You", "added new layer '$content'", "Just now")
        _collabSession.value = current.copy(
            layers = current.layers + newLayer,
            activityEvents = listOf(newEvent) + current.activityEvents.take(4)
        )
    }

    fun toggleVoiceHuddle() {
        val current = _collabSession.value
        _collabSession.value = current.copy(isVoiceHuddleActive = !current.isVoiceHuddleActive)
    }
}
