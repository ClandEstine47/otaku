package com.example.core.domain.model.staff

import com.example.core.domain.model.PageInfo

data class StaffConnection(
    val edges: List<StaffEdge> = listOf(),
    val nodes: List<Staff> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)