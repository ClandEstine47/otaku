package com.example.core.domain.model.character

import com.example.core.domain.model.PageInfo

data class CharacterConnection(
    val edges: List<CharacterEdge>? = listOf(),
    val nodes: List<Character> = listOf(),
    val pageInfo: PageInfo = PageInfo(),
)
