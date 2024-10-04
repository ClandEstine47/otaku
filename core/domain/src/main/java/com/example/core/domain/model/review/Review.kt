package com.example.core.domain.model.review

import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.user.User

data class Review(
    val id: Int? = 0,
    val userId: Int? = 0,
    val mediaId: Int? = 0,
    val mediaType: MediaType?,
    val summary: String? = "",
    val body: String? = "",
    val rating: Int? = 0,
    val ratingAmount: Int? = 0,
    val userRating: ReviewRating?,
    val score: Int? = 0,
    val private: Boolean = false,
    val siteUrl: String = "",
    val createdAt: Int? = 0,
    val updatedAt: Int? = 0,
    val user: User = User(),
    val media: Media = Media(),
)
