package com.example.core.domain.model.studio

import com.example.core.domain.model.PageInfo

data class StudioConnection(
    val edges: List<StudioEdge> = listOf(),
    val nodes: List<Studio> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)