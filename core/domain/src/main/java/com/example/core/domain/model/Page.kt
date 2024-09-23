package com.example.core.domain.model

data class Page<T>(
    val pageInfo: PageInfo? = PageInfo(),
    val data: List<T> = listOf(),
)
