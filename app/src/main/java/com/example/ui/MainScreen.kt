package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.chat.VaultChatScreen
import com.example.ui.collab.CollabStudioScreen
import com.example.ui.components.AuraAvatar
import com.example.ui.explore.ExploreScreen
import com.example.ui.feed.FeedScreen
import com.example.ui.navigation.Screen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.reels.ReelsScreen
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@Composable
fun MainScreen(viewModel: SocialViewModel) {
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
    val activeToast by viewModel.activeToast.collectAsStateWithLifecycle()
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground),
        bottomBar = {
            AuraBottomNavBar(
                currentRoute = currentRoute,
                unreadNotifs = unreadNotifs,
                onNavigate = { viewModel.setRoute(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Destination Switcher
            Crossfade(
                targetState = currentRoute,
                label = "screen_transition"
            ) { route ->
                when (route) {
                    Screen.Feed.route -> FeedScreen(
                        viewModel = viewModel,
                        onNavigateToVault = { viewModel.setRoute(Screen.VaultChat.route) },
                        onNavigateToStudio = { viewModel.setRoute(Screen.CollabStudio.route) },
                        onNavigateToNotifications = { viewModel.setRoute(Screen.Notifications.route) }
                    )
                    Screen.Reels.route -> ReelsScreen(
                        viewModel = viewModel,
                        onNavigateToStudio = { viewModel.setRoute(Screen.CollabStudio.route) }
                    )
                    Screen.CollabStudio.route -> CollabStudioScreen(viewModel = viewModel)
                    Screen.VaultChat.route -> VaultChatScreen(viewModel = viewModel)
                    Screen.Notifications.route -> NotificationsScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.setRoute(it) }
                    )
                    Screen.Explore.route -> ExploreScreen(viewModel = viewModel)
                    Screen.Profile.route -> ProfileScreen(viewModel = viewModel)
                    else -> FeedScreen(
                        viewModel = viewModel,
                        onNavigateToVault = { viewModel.setRoute(Screen.VaultChat.route) },
                        onNavigateToStudio = { viewModel.setRoute(Screen.CollabStudio.route) },
                        onNavigateToNotifications = { viewModel.setRoute(Screen.Notifications.route) }
                    )
                }
            }

            // Real-Time Floating In-App Notification Toast
            AnimatedVisibility(
                visible = activeToast != null,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                activeToast?.let { notif ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AuraDarkSurface)
                            .border(1.2.dp, AuraNeonCyan, RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.dismissActiveToast()
                                notif.targetRoute?.let { viewModel.setRoute(it) }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            AuraAvatar(
                                drawableName = notif.actorAvatarDrawable,
                                size = 36.dp,
                                hasGlowBorder = true
                            )
                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(
                                    text = notif.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraNeonCyan
                                )
                                Text(
                                    text = notif.description,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.dismissActiveToast() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuraBottomNavBar(
    currentRoute: String,
    unreadNotifs: Int,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(AuraDarkSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF283652), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        containerColor = AuraDarkSurface,
        tonalElevation = 8.dp
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val isCollabCenter = screen.route == Screen.CollabStudio.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    if (isCollabCenter) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(AuraNeonViolet, AuraNeonCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = screen.title,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        val iconColor = when {
                            isSelected && screen.route == Screen.Reels.route -> AuraHotPink
                            isSelected -> AuraNeonCyan
                            else -> Color.Gray
                        }
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = screen.title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    if (!isCollabCenter) {
                        Text(
                            text = screen.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                if (screen.route == Screen.Reels.route) AuraHotPink else AuraNeonCyan
                            } else Color.Gray
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AuraNeonCyan,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}
