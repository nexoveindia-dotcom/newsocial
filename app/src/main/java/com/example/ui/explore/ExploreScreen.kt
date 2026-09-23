package com.example.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.GeminiService
import com.example.ui.SocialViewModel
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.launch

data class TrendingTopic(val name: String, val postCount: String, val vibe: String)

@Composable
fun ExploreScreen(viewModel: SocialViewModel) {
    val posts by viewModel.feedPosts.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var aiSearchResult by remember { mutableStateOf<String?>(null) }
    var isSearchingAi by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val trendingTopics = listOf(
        TrendingTopic("#NeuralSculpts", "48.2k artifacts", "Cyberpunk"),
        TrendingTopic("#Ambient432Hz", "29.1k sounds", "Neo-Chill"),
        TrendingTopic("#CollaborativeCanvases", "14.8k rooms", "Quantum Tech"),
        TrendingTopic("#PrismAura", "82.5k vibes", "Ambient Art")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search Header
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "NEURAL EXPLORE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )

                // Search Bar with AI prompt assistant
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.length > 3) {
                            scope.launch {
                                isSearchingAi = true
                                aiSearchResult = GeminiService.analyzeFeedSentiment(it)
                                isSearchingAi = false
                            }
                        } else {
                            aiSearchResult = null
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AuraNeonCyan
                        )
                    },
                    trailingIcon = {
                        if (isSearchingAi) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AuraNeonCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = AuraHotPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search vibe, aesthetic, or prompt AI...",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("search_explore_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraNeonCyan,
                        unfocusedBorderColor = Color(0xFF28354D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                if (aiSearchResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuraNeonViolet.copy(alpha = 0.15f))
                            .border(1.dp, AuraNeonViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AuraNeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI Curation: $aiSearchResult",
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Trending AI Topics
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "Trending Neural Nodes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trendingTopics) { topic ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(AuraDarkCard)
                                .border(1.dp, Color(0xFF2A374F), RoundedCornerShape(14.dp))
                                .clickable { viewModel.setVibe(topic.vibe) }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = topic.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraNeonCyan
                            )
                            Text(
                                text = topic.postCount,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Soundtracks / Audio Vibe Tags
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF141E33), Color(0xFF1F1133)))
                    )
                    .border(1.dp, AuraEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio Vibe",
                        tint = AuraEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = "Live Spatial Audio Frequencies",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "432Hz ambient binaural beats synced to trending artworks",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Visual Discovery Grid (Staggered-style responsive cards)
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    text = "High-Affinity Feed Grid",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 2-column aesthetic grid
                val chunked = posts.chunked(2)
                chunked.forEach { rowPosts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPosts.forEach { post ->
                            val imgRes = rememberDrawableResId(context, post.imageDrawableName)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.9f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { viewModel.setRoute("feed") }
                            ) {
                                Image(
                                    painter = painterResource(id = imgRes),
                                    contentDescription = "Explore Item",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient tint at bottom
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                )

                                // Overlay info
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    if (post.isCollaborative) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Group,
                                                contentDescription = null,
                                                tint = AuraNeonCyan,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = "COLLAB",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AuraNeonCyan,
                                                modifier = Modifier.padding(start = 3.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = post.author.handle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = AuraHotPink,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "${post.likesCount}",
                                            fontSize = 9.sp,
                                            color = Color.LightGray,
                                            modifier = Modifier.padding(start = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (rowPosts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
