package com.leeeyou.isplash.data

import com.leeeyou.isplash.data.bean.*

data class PhotoDetail(
    val alt_description: String,
    val blur_hash: String,
    val categories: List<Any>,
    val color: String,
    val created_at: String,
    val current_user_collections: List<Any>,
    val description: String,
    val downloads: Int,
    val exif: Exif,
    val height: Int,
    val id: String,
    val liked_by_user: Boolean,
    val likes: Int,
    val links: Links,
    val location: Location,
    val meta: Meta,
    val promoted_at: String,
    val related_collections: RelatedCollections,
    val sponsorship: Any,
    val tags: List<Tag>,
    val tags_preview: List<TagsPreview>,
    val topics: List<Any>,
    val updated_at: String,
    val urls: Urls,
    val user: User,
    val views: Int,
    val width: Int
)