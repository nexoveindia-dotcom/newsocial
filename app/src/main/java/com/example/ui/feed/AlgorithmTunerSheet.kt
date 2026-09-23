package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AlgorithmSettings
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmTunerSheet(
    settings: AlgorithmSettings,
    onSave: (AlgorithmSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var serendipity by remember { mutableFloatStateOf(settings.serendipityLevel) }
    var freshness by remember { mutableFloatStateOf(settings.freshnessWeight) }
    var collabBoost by remember { mutableStateOf(settings.collaborativeBoost) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AuraDarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Tuning",
                        tint = AuraNeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Neural Feed Engine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Text(
                text = "Take complete control of what you see. Unlike legacy social algorithms designed for addiction, Aura lets you fine-tune your discovery parameters transparently.",
                fontSize = 13.sp,
                color = Color.LightGray,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
            )

            // Serendipity slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Serendipity & Novelty",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${(serendipity * 100).toInt()}% Discovery",
                        color = AuraNeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = if (serendipity < 0.4f) "Echo Chamber Reduction: Low (Strictly follows your established habits)"
                    else if (serendipity < 0.75f) "Balanced serendipity: Integrates 35% unexpected creative domains"
                    else "Maximum Exploration: AI introduces rare, high-affinity avant-garde works",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                )
                Slider(
                    value = serendipity,
                    onValueChange = { serendipity = it },
                    colors = SliderDefaults.colors(
                        thumbColor = AuraNeonCyan,
                        activeTrackColor = AuraNeonCyan,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.testTag("slider_serendipity")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Freshness slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Real-Time Velocity Weight",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${(freshness * 100).toInt()}% Chrono",
                        color = AuraNeonViolet,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "Controls balance between breaking live posts vs all-time high affinity artifacts.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                )
                Slider(
                    value = freshness,
                    onValueChange = { freshness = it },
                    colors = SliderDefaults.colors(
                        thumbColor = AuraNeonViolet,
                        activeTrackColor = AuraNeonViolet,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.testTag("slider_freshness")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Collaborative Boost Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraDarkCard)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Boost Co-Authored Collabs",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Prioritize real-time multi-creator collaborative works over solo posts.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Switch(
                    checked = collabBoost,
                    onCheckedChange = { collabBoost = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AuraNeonCyan,
                        checkedTrackColor = AuraNeonCyan.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("switch_collab_boost")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Zero Knowledge Privacy info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraEmerald.copy(alpha = 0.1f))
                    .border(1.dp, AuraEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Zero-Knowledge",
                        tint = AuraEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Zero-Knowledge Personalization: Vectors are computed locally on device. Your tastes are never sold or profiled for ad tracking.",
                        fontSize = 11.sp,
                        color = AuraEmerald,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    onSave(
                        settings.copy(
                            serendipityLevel = serendipity,
                            freshnessWeight = freshness,
                            collaborativeBoost = collabBoost
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_algorithm_settings_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuraNeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = "Apply Neural Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
