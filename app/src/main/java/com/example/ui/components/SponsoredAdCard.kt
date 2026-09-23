package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SponsoredAd
import com.example.ui.theme.AuraAmberGold
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardBorder
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@Composable
fun SponsoredAdCard(
    ad: SponsoredAd,
    onCtaClicked: () -> Unit
) {
    val context = LocalContext.current
    val imageRes = rememberDrawableResId(context, ad.imageDrawableName)
    var showTransparencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AuraDarkCard)
            .border(1.dp, AuraDarkCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("sponsored_ad_card_${ad.id}")
    ) {
        // Top Brand Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AuraAvatar(
                    drawableName = ad.brandAvatarDrawable,
                    size = 38.dp,
                    hasGlowBorder = false
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ad.brandName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AuraAmberGold.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "SPONSORED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraAmberGold
                            )
                        }
                    }
                    Text(
                        text = "${ad.brandHandle} • Privacy-Preserving Neural Drop",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = { showTransparencyDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Why this ad?",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Ad Headline & Body
        Text(
            text = ad.headline,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )

        Text(
            text = ad.body,
            fontSize = 12.sp,
            color = Color.LightGray,
            lineHeight = 17.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Ad Creative Image Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF25334D), RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = ad.headline,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Micro privacy badge
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AuraEmerald, modifier = Modifier.size(11.dp))
                Text(
                    text = "Zero-Knowledge Ad",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraEmerald,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CTA Button
        Button(
            onClick = onCtaClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("ad_cta_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuraNeonCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = ad.ctaText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .padding(start = 4.dp)
            )
        }
    }

    // Zero Knowledge Ad Transparency Dialog
    if (showTransparencyDialog) {
        AlertDialog(
            onDismissRequest = { showTransparencyDialog = false },
            containerColor = AuraDarkSurface,
            icon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AuraEmerald, modifier = Modifier.size(28.dp))
            },
            title = {
                Text(
                    text = "Aura Zero-Knowledge Ads",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Why am I seeing this sponsored post?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraEmerald,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = ad.zeroKnowledgeExplanation,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ No ad tracking ID or cookies\n✓ Private keys never leave device\n✓ Cryptographically audited sponsors only",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTransparencyDialog = false }) {
                    Text("Understood", color = AuraNeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
