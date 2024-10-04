package com.example.core.domain.model.review

import com.example.core.domain.model.PageInfo

data class ReviewConnection(
    val edges: List<ReviewEdge>? = listOf(),
    val nodes: List<Review> = listOf(),
    val pageInfo: PageInfo = PageInfo(),
)
