package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.crypto.CryptoManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura Social", appName)
    }

    @Test
    fun `verify E2EE encryption and decryption cycle`() {
        val plaintext = "Quantum encrypted payload test 42"
        val encrypted = CryptoManager.encryptMessage(plaintext)

        // Must be prefixed with AURA:ENC:
        assertTrue(encrypted.startsWith("AURA:ENC:"))
        assertNotEquals(plaintext, encrypted)

        // Decrypted must match original
        val decrypted = CryptoManager.decryptMessage(encrypted)
        assertEquals(plaintext, decrypted)
    }

    @Test
    fun `verify safety number generation is deterministic`() {
        val alice = "user_alice_pk_hash"
        val bob = "user_bob_pk_hash"
        val safetyNumber1 = CryptoManager.computeSafetyNumber(alice, bob)
        val safetyNumber2 = CryptoManager.computeSafetyNumber(bob, alice)
        assertEquals(safetyNumber1, safetyNumber2)
        assertEquals(4, safetyNumber1.split(" ").size)
    }

    @Test
    fun `verify story posting and seen state`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.repository.SocialRepository(context)

        val initialStoriesCount = repo.stories.value.size
        repo.postStory(
            imageDrawable = "post_crystal_city_1790200645179",
            caption = "Test story caption",
            filterName = "Neon Glitch",
            musicTrack = "Obsidian Skyline",
            stickerText = "✦ QUANTUM VIBE"
        )

        val updatedStories = repo.stories.value
        assertEquals(initialStoriesCount, updatedStories.size)

        val myStory = updatedStories.first { story -> story.id == "story_me" }
        assertEquals("Test story caption", myStory.caption)
        assertEquals("Neon Glitch", myStory.filterName)
        assertEquals("✦ QUANTUM VIBE", myStory.stickerText)
        assertEquals(false, myStory.isSeen)

        repo.markStoryAsSeen(myStory.id)
        assertTrue(repo.stories.value.first { story -> story.id == "story_me" }.isSeen)
    }

    @Test
    fun `verify feed post publishing with vibe and coauthors`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.repository.SocialRepository(context)

        repo.publishFeedPost(
            imageRes = "post_aurora_vibes_1790200680892",
            caption = "New neural artifact drop",
            vibe = "Cyberpunk",
            coAuthors = listOf("Elena Rostova", "Marcus Vance"),
            isCipherGuildOnly = true
        )

        val updatedPosts = repo.getAllPosts().first { posts -> posts.any { it.caption == "New neural artifact drop" } }
        val newest = updatedPosts.first { it.caption == "New neural artifact drop" }
        assertEquals("New neural artifact drop", newest.caption)
        assertEquals("Cyberpunk", newest.vibeTag)
        assertEquals(2, newest.coAuthors.size)
    }

    @Test
    fun `verify live broadcast lifecycle and comments`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.repository.SocialRepository(context)

        repo.startLiveBroadcast(
            title = "Test Quantum Live Stream",
            category = "Creative Studio",
            allowCoHost = true
        )

        val active = repo.activeLiveStream.value
        org.junit.Assert.assertNotNull(active)
        assertEquals("Test Quantum Live Stream", active?.title)
        assertEquals("Nova Sterling", active?.host?.name)

        repo.sendLiveComment("Welcome to the test broadcast!", isHost = true)
        val comments = repo.liveComments.value
        assertTrue(comments.any { comment -> comment.text == "Welcome to the test broadcast!" && comment.isHost })

        val coHost = com.example.data.model.UserProfile(
            "user_elena", "Elena Rostova", "@elena.synapse", "post_cyber_creator_1790200662289", true, 94
        )
        repo.inviteCoHostToLive(coHost)
        assertEquals("Elena Rostova", repo.activeLiveStream.value?.coHost?.name)

        repo.endLiveBroadcast()
        org.junit.Assert.assertNull(repo.activeLiveStream.value)
    }
}
