package com.example.core.domain.model.character

import com.example.core.domain.model.staff.StaffRoleType
import com.example.core.domain.model.media.Media

data class CharacterEdge(
    val node: Character = Character(),
    val role: CharacterRole? = null,
    val name: String = "",
    val voiceActorRoles: List<StaffRoleType> = listOf(),
    val media: List<Media> = listOf()
)