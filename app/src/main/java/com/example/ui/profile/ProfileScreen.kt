package com.example.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.components.AuraBadge
import com.example.ui.components.rememberDrawableResId
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@Composable
fun ProfileScreen(viewModel: SocialViewModel) {
    val user = viewModel.currentUser
    val posts by viewModel.feedPosts.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val collabPosts = posts.filter { it.isCollaborative }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Identity & Aura Score row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuraAvatar(
                        drawableName = user.avatarDrawableName,
                        size = 76.dp,
                        hasGlowBorder = true,
                        isVerified = true
                    )

                    // Metrics
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileMetric(title = "Artifacts", count = "${posts.size + 4}")
                        ProfileMetric(title = "Collabs", count = "${collabPosts.size + 2}")
                        ProfileMetric(title = "Aura Score", count = "98/100", isAura = true)
                    }
                }

                // Name & Handle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = user.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = AuraNeonCyan,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(start = 4.dp)
                    )
                }

                Text(
                    text = user.handle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // Bio
                Text(
                    text = user.bio,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                // E2EE Identity Fingerprint Chip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1B2D))
                        .border(1.dp, AuraEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Identity",
                            tint = AuraEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "CRYPTOGRAPHIC IDENTITY FINGERPRINT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraEmerald
                            )
                            Text(
                                text = user.e2eeKeyFingerprint,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.setRoute("collab") },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraNeonCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = "New Live Collab",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.setRoute("vault") },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraDarkCard,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AuraEmerald, modifier = Modifier.size(16.dp))
                        Text(
                            text = "E2EE Vault",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }

                // RBAC Admin Command Center Entry Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setRoute("admin") }
                        .testTag("open_admin_command_center_button"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131722)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(listOf(AuraNeonViolet, AuraNeonCyan))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(AuraNeonViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = AuraNeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Admin Command Center",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Role-Based Access Control (RBAC) Governance",
                                    fontSize = 10.sp,
                                    color = AuraNeonCyan
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AuraNeonViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "OPEN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = AuraNeonCyan
                            )
                        }
                    }
                }
            }
        }

        // Profile Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AuraDarkCard,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AuraNeonCyan
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Artifacts", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Co-Authored", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Keys & Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0, 1 -> {
                val displayList = if (selectedTab == 1) collabPosts else posts
                val chunked = displayList.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { post ->
                            val imgRes = rememberDrawableResId(context, post.imageDrawableName)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setRoute("feed") }
                            ) {
                                Image(
                                    painter = painterResource(id = imgRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (post.isCollaborative) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AuraNeonViolet.copy(alpha = 0.85f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "COLLAB",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            2 -> {
                // Cryptographic Keys & Vault Audit Tab
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuraDarkCard)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = AuraNeonCyan)
                            Text(
                                text = "On-Device Cryptographic Keys",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Text(
                            text = "Aura uses standard RSA-2048 identity keys combined with AES-256-GCM ratcheted session keys. Private keys never leave your secure enclave.",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        KeyDetailRow(label = "Algorithm", value = "RSA-2048 / AES-256-GCM")
                        KeyDetailRow(label = "Digest", value = "SHA-256 (NIST Compliant)")
                        KeyDetailRow(label = "Key Rotation", value = "Active Forward Secrecy")
                        KeyDetailRow(label = "Key Status", value = "Verified & Operational")

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { /* simulated rotation */ },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = AuraNeonCyan
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Rotate Ephemeral Session Ratchets",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMetric(title: String, count: String, isAura: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAura) AuraNeonCyan else Color.White
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun KeyDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AuraEmerald)
    }
}
