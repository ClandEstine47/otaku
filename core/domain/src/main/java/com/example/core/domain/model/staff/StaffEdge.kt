package com.example.core.domain.model.staff

data class StaffEdge(
    val node: Staff = Staff(),
    val id: Int = 0,
    val role: String = "",
    val favouriteOrder: Int = 0,
)
