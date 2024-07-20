package com.example.core.domain.model.media

import com.example.core.domain.model.staff.StaffRoleType
import com.example.core.domain.model.character.Character
import com.example.core.domain.model.character.CharacterRole

data class MediaEdge(
    val node: Media = Media(),
    val relationType: MediaRelation? = null,
    val characters: List<Character> = listOf(),
    val characterRole: CharacterRole? = null,
    val characterName: String = "",
    val roleNotes: String = "",
    val dubGroup: String = "",
    val voiceActorRoles: List<StaffRoleType> = listOf(),
    val staffRole: String = "",
    val isMainStudio: Boolean = false
)