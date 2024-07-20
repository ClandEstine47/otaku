package com.example.core.domain.model.character

import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.MediaConnection

data class Character(
    val id: Int = 0,
    val name: CharacterName = CharacterName(),
    val image: CharacterImage = CharacterImage(),
    val description: String = "",
    val gender: String = "",
    val dateOfBirth: FuzzyDate? = null,
    val age: String = "",
    val bloodType: String = "",
    var isFavourite: Boolean = false,
    val siteUrl: String = "",
    val media: MediaConnection = MediaConnection(),
    val favourites: Int = 0
)