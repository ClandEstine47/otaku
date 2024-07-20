package com.example.core.domain.model.recommendation

import com.example.core.domain.model.media.Media

data class Recommendation(
    val id: Int = 0,
    val rating: Int = 0,
    val userRating: RecommendationRating? = null,
    val mediaRecommendation: Media? = null
)