package com.example.core.domain.model.user

import com.example.core.domain.model.character.CharacterConnection
import com.example.core.domain.model.media.MediaConnection
import com.example.core.domain.model.staff.StaffConnection
import com.example.core.domain.model.studio.StudioConnection

data class Favourites(
    val anime: MediaConnection = MediaConnection(),
    val manga: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val staff: StaffConnection = StaffConnection(),
    val studios: StudioConnection = StudioConnection(),
)
