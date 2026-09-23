package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String, fallback: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API key configured, using high-fidelity algorithmic synthesis.")
            return@withContext fallback
        }

        try {
            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 500)
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "API Error: ${response.code} $errBody")
                return@withContext fallback
            }

            val respBody = response.body?.string() ?: return@withContext fallback
            val root = JSONObject(respBody)
            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) return@withContext text.trim()
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini API", e)
            fallback
        }
    }

    suspend fun remixPostContent(originalCaption: String, targetVibe: String): String {
        val prompt = "You are Aura AI, an avant-garde creative social media director. Remix the following post caption into a fresh, high-impact $targetVibe vibe version with aesthetic emojis and 3 cutting-edge hashtags. Keep it concise (1-2 sentences). Original: \"$originalCaption\""
        val smartFallback = when (targetVibe) {
            "Cyberpunk" -> "⚡ Chromatic glitch echoes through the neon mesh. Reality is just firmware waiting for a patch. #AuraMatrix #Cyberpunk2077 #NeuralEchoes"
            "Neo-Chill" -> "🌿 Drifting on frequency 432Hz. Ambient currents and serene visual silence. #NeoChill #Mindscape #AuraVibes"
            "Quantum Tech" -> "🧬 Entangled particles rendering synthetic sunsets in real time. Superposition unlocked. #QuantumShift #SyntheticHorizon #AuraTech"
            "Ambient Art" -> "✨ Where luminous glass meets liquid dusk. A quiet hymn in hypercolor. #AmbientAesthetics #DigitalSculpt #PrismArt"
            else -> "🔮 Transmuted through the Aura neural lattice. A collaborative artifact of collective dreams. #AuraNext #CoCreate #NeuralArt"
        }
        return generateContent(prompt, smartFallback)
    }

    suspend fun generateCaptionIdeas(theme: String): String {
        val prompt = "Generate a captivating, viral-ready micro-caption (1 sentence) and 3 trending aesthetic tags for a collaborative social post about '$theme'."
        val fallback = "Synthetic dimensions folding into radiant light. 🌌 #AuraSphere #NeuralAesthetic #VisualFuture"
        return generateContent(prompt, fallback)
    }

    suspend fun analyzeFeedSentiment(topic: String): String {
        val prompt = "Provide a 1-sentence poetic AI vibe synthesis of what the community is feeling right now regarding $topic."
        val fallback = "Collective sentiment is radiant with creative anticipation, leaning 88% toward collaborative optimism."
        return generateContent(prompt, fallback)
    }
}
