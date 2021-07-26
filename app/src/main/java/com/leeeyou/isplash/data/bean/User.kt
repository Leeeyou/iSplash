package com.leeeyou.isplash.data.bean

data class User(
    val id: String,
    val updated_at: String,
    val username: String,
    val name: String,
    val first_name: String,
    val last_name: String,
    val twitter_username: Any,
    val portfolio_url: String,
    val bio: String,
    val location: String,
    val links: LinksX,
    val profile_image: ProfileImage,
    val instagram_username: Any,
    val total_collections: Int,
    val total_likes: Int,
    val total_photos: Int,
    val accepted_tos: Boolean,
    val for_hire: Boolean
)