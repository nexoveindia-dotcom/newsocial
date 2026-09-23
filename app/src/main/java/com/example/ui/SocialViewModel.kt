package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.crypto.CryptoManager
import com.example.data.model.AuraNotification
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.CollabSession
import com.example.data.model.LayerType
import com.example.data.model.LiveComment
import com.example.data.model.LiveStreamSession
import com.example.data.model.Post
import com.example.data.model.Reel
import com.example.data.model.SponsoredAd
import com.example.data.model.StoryItem
import com.example.data.model.UserProfile
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AlgorithmSettings(
    val serendipityLevel: Float = 0.65f, // 0 = hyper-predictable, 1 = maximum discovery
    val freshnessWeight: Float = 0.8f,
    val collaborativeBoost: Boolean = true,
    val privacyLevel: String = "Zero Knowledge (Client Side)"
)

@OptIn(ExperimentalCoroutinesApi::class)
class SocialViewModel(application: Application) : AndroidViewModel(application) {
    val repository = SocialRepository(application)

    // Current User
    val currentUser: UserProfile = repository.currentUser

    // Vibe filter for feed
    private val _selectedVibe = MutableStateFlow("All")
    val selectedVibe = _selectedVibe.asStateFlow()

    // Algorithm settings
    private val _algoSettings = MutableStateFlow(AlgorithmSettings())
    val algoSettings = _algoSettings.asStateFlow()

    // All posts combined with filter
    val feedPosts: StateFlow<List<Post>> = combine(
        repository.getAllPosts(),
        _selectedVibe,
        _algoSettings
    ) { posts, vibe, settings ->
        var list = if (vibe == "All") posts else posts.filter { it.vibeTag.equals(vibe, ignoreCase = true) }
        if (settings.collaborativeBoost) {
            list = list.sortedByDescending { if (it.isCollaborative) 1000 + it.likesCount else it.likesCount }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stories & Collaborative Sessions
    val stories: StateFlow<List<StoryItem>> = repository.stories
    val conversations: StateFlow<List<ChatConversation>> = repository.conversations
    val collabSession: StateFlow<CollabSession> = repository.collabSession

    // Live Streaming
    val activeLiveStream: StateFlow<LiveStreamSession?> = repository.activeLiveStream
    val watchingLiveStream: StateFlow<LiveStreamSession?> = repository.watchingLiveStream
    val liveComments: StateFlow<List<LiveComment>> = repository.liveComments

    // Reels Feed
    val reels: StateFlow<List<Reel>> = repository.getAllReels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<AuraNotification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Sponsored Ads
    val sponsoredAds: StateFlow<List<SponsoredAd>> = repository.sponsoredAds
    val inAppToastEvent: SharedFlow<AuraNotification> = repository.inAppToastEvent

    // Active Chat State
    private val _activeConvoId = MutableStateFlow("convo_elena")
    val activeConvoId = _activeConvoId.asStateFlow()

    private val _activeConvo = MutableStateFlow(repository.conversations.value.first())
    val activeConvo = _activeConvo.asStateFlow()

    val currentMessages: StateFlow<List<ChatMessage>> = _activeConvoId.flatMapLatest { id ->
        repository.getMessagesForConversation(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Remix State
    private val _isRemixing = MutableStateFlow(false)
    val isRemixing = _isRemixing.asStateFlow()

    private val _remixResult = MutableStateFlow<String?>(null)
    val remixResult = _remixResult.asStateFlow()

    private val _targetPostForRemix = MutableStateFlow<Post?>(null)
    val targetPostForRemix = _targetPostForRemix.asStateFlow()

    // AI Caption Generator for Collab Studio & Reels
    private val _aiGeneratedCaption = MutableStateFlow<String?>(null)
    val aiGeneratedCaption = _aiGeneratedCaption.asStateFlow()
    private val _isGeneratingCaption = MutableStateFlow(false)
    val isGeneratingCaption = _isGeneratingCaption.asStateFlow()

    // Active Route
    private val _currentRoute = MutableStateFlow("feed")
    val currentRoute = _currentRoute.asStateFlow()

    // In-App Toast Active State for UI banner
    private val _activeToast = MutableStateFlow<AuraNotification?>(null)
    val activeToast = _activeToast.asStateFlow()

    private var selfDestructTickerJob: Job? = null

    init {
        startSelfDestructTicker()
        observeInAppNotifications()
    }

    private fun observeInAppNotifications() {
        viewModelScope.launch {
            repository.inAppToastEvent.collect { notif ->
                _activeToast.value = notif
                delay(4000)
                if (_activeToast.value?.id == notif.id) {
                    _activeToast.value = null
                }
            }
        }
    }

    fun dismissActiveToast() {
        _activeToast.value = null
    }

    private fun startSelfDestructTicker() {
        selfDestructTickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val now = System.currentTimeMillis()
                val msgs = currentMessages.value
                for (msg in msgs) {
                    if (msg.selfDestructSeconds > 0 && msg.expiresAt > 0L && now >= msg.expiresAt && !msg.isIncinerated) {
                        repository.incinerateMessage(msg.id)
                    }
                }
            }
        }
    }

    fun setVibe(vibe: String) {
        _selectedVibe.value = vibe
    }

    fun setRoute(route: String) {
        _currentRoute.value = route
    }

    fun updateAlgorithmSettings(newSettings: AlgorithmSettings) {
        _algoSettings.value = newSettings
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            repository.toggleLike(post.id, post.isLiked)
        }
    }

    fun toggleSave(post: Post) {
        viewModelScope.launch {
            repository.toggleSave(post.id, post.isSaved)
        }
    }

    // Reels
    fun toggleReelLike(reel: Reel) {
        viewModelScope.launch {
            repository.toggleReelLike(reel.id, reel.isLiked)
        }
    }

    fun toggleReelSave(reel: Reel) {
        viewModelScope.launch {
            repository.toggleReelSave(reel.id, reel.isSaved)
        }
    }

    fun createAndPostReel(
        videoDrawable: String,
        audioTitle: String,
        audioArtist: String,
        caption: String,
        filterName: String,
        isCollab: Boolean,
        coAuthorName: String?
    ) {
        viewModelScope.launch {
            repository.createAndPostReel(
                videoDrawable = videoDrawable,
                audioTitle = audioTitle,
                audioArtist = audioArtist,
                caption = caption,
                filterName = filterName,
                isCollab = isCollab,
                coAuthorName = coAuthorName
            )
            _currentRoute.value = "reels"
        }
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // AI Remix
    fun openRemixModal(post: Post) {
        _targetPostForRemix.value = post
        _remixResult.value = null
    }

    fun closeRemixModal() {
        _targetPostForRemix.value = null
        _remixResult.value = null
        _isRemixing.value = false
    }

    fun triggerAiRemix(targetVibe: String) {
        val post = _targetPostForRemix.value ?: return
        viewModelScope.launch {
            _isRemixing.value = true
            val remixed = GeminiService.remixPostContent(post.caption, targetVibe)
            _remixResult.value = remixed
            _isRemixing.value = false
        }
    }

    // Chat Actions
    fun selectConversation(conversation: ChatConversation) {
        _activeConvo.value = conversation
        _activeConvoId.value = conversation.id
    }

    fun toggleMessageReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            repository.toggleMessageReaction(messageId, emoji)
        }
    }

    fun sendEncryptedMessage(text: String, selfDestructSec: Int) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendEncryptedMessage(_activeConvoId.value, text, selfDestructSec)
        }
    }

    fun sendEncryptedVoiceNote(durationSec: Int) {
        viewModelScope.launch {
            repository.sendEncryptedVoiceNote(_activeConvoId.value, durationSec)
        }
    }

    // Collab Studio Actions
    fun updateCollabLayerText(layerId: String, text: String) {
        repository.updateCollabLayerText(layerId, text)
    }

    fun updateCollabFilter(filterName: String, intensity: Float) {
        repository.updateCollabFilter(filterName, intensity)
    }

    fun addCollabLayer(type: LayerType, title: String, content: String, color: Long) {
        repository.addCollabLayer(type, title, content, color)
    }

    fun toggleVoiceHuddle() {
        repository.toggleVoiceHuddle()
    }

    fun generateAiCaptionForStudio(theme: String) {
        viewModelScope.launch {
            _isGeneratingCaption.value = true
            val caption = GeminiService.generateCaptionIdeas(theme)
            _aiGeneratedCaption.value = caption
            _isGeneratingCaption.value = false
        }
    }

    fun publishStudioPost(caption: String) {
        viewModelScope.launch {
            val session = collabSession.value
            val coAuthorNames = session.collaborators
                .filter { it.id != currentUser.id }
                .map { it.name }

            repository.publishCollaborativePost(
                title = session.title,
                caption = caption.ifBlank { "Co-created in Aura Studio with live cryptographic verification. ✦" },
                imageRes = session.baseImageResName,
                coAuthors = coAuthorNames,
                vibe = "Cyberpunk"
            )
            _currentRoute.value = "feed"
        }
    }

    // Story System Actions
    fun postStory(
        imageDrawable: String,
        caption: String,
        filterName: String,
        musicTrack: String?,
        stickerText: String?
    ) {
        repository.postStory(
            imageDrawable = imageDrawable,
            caption = caption,
            filterName = filterName,
            musicTrack = musicTrack,
            stickerText = stickerText
        )
    }

    fun markStoryAsSeen(storyId: String) {
        repository.markStoryAsSeen(storyId)
    }

    fun addStoryReply(story: StoryItem, replyText: String) {
        if (replyText.isBlank()) return
        viewModelScope.launch {
            repository.addStoryReply(story, replyText)
        }
    }

    // Feed Post Publishing
    fun publishFeedPost(
        imageRes: String,
        caption: String,
        vibe: String,
        coAuthors: List<String>,
        isCipherGuildOnly: Boolean
    ) {
        viewModelScope.launch {
            repository.publishFeedPost(
                imageRes = imageRes,
                caption = caption.ifBlank { "Unveiling new digital artifacts on the Aura stream. ✦" },
                vibe = vibe,
                coAuthors = coAuthors,
                isCipherGuildOnly = isCipherGuildOnly
            )
            _currentRoute.value = "feed"
        }
    }

    // Go Live System Actions
    fun startLiveBroadcast(title: String, category: String, allowCoHost: Boolean) {
        repository.startLiveBroadcast(title, category, allowCoHost)
    }

    fun inviteCoHostToLive(user: UserProfile) {
        repository.inviteCoHostToLive(user)
    }

    fun sendLiveComment(text: String, isHost: Boolean = false) {
        repository.sendLiveComment(text, isHost)
    }

    fun endLiveBroadcast() {
        repository.endLiveBroadcast()
    }

    fun openWatchLive(stream: LiveStreamSession) {
        repository.openWatchLive(stream)
    }

    fun closeWatchLive() {
        repository.closeWatchLive()
    }

    override fun onCleared() {
        super.onCleared()
        selfDestructTickerJob?.cancel()
    }
}
