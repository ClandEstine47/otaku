package com.example.core.domain.model.character

data class CharacterName(
    val first: String = "",
    val middle: String = "",
    val last: String = "",
    val full: String? = "",
    val native: String = "",
    val alternative: List<String> = listOf(),
    val alternativeSpoiler: List<String> = listOf(),
    val userPreferred: String = "",
)
