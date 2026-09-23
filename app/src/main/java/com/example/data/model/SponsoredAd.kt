package com.example.data.model

data class SponsoredAd(
    val id: String,
    val brandName: String,
    val brandHandle: String,
    val brandAvatarDrawable: String,
    val headline: String,
    val body: String,
    val imageDrawableName: String,
    val ctaText: String = "Explore Drop",
    val sponsorTag: String = "Sponsored • Zero-Knowledge Matching",
    val zeroKnowledgeExplanation: String = "Matched via on-device neural vectors. No advertising cookies, device identifiers, or biometric profiles were shared with external trackers."
)
