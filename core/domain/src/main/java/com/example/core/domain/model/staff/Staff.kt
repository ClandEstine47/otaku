package com.example.core.domain.model.staff

import com.example.core.domain.model.character.CharacterConnection
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.MediaConnection

data class Staff(
    val id: Int? = 0,
    val name: StaffName = StaffName(),
    val language: String = "",
    val image: StaffImage = StaffImage(),
    val description: String = "",
    val primaryOccupations: List<String> = listOf(),
    val gender: String = "",
    val dateOfBirth: FuzzyDate? = null,
    val dateOfDeath: FuzzyDate? = null,
    val age: Int = 0,
    val yearsActive: List<Int> = listOf(),
    val homeTown: String = "",
    val bloodType: String = "",
    val isFavourite: Boolean = false,
    val siteUrl: String = "",
    val staffMedia: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val characterMedia: MediaConnection = MediaConnection(),
    val favourites: Int = 0,
)
