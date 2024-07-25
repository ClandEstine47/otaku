package com.example.core.domain.model.media

import com.example.core.domain.model.PageInfo

data class MediaConnection(
    var edges: List<MediaEdge> = listOf(),
    val nodes: List<Media> = listOf(),
    val pageInfo: PageInfo = PageInfo(),
)
