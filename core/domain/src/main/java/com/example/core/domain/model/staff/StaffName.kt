package com.example.core.domain.model.staff

data class StaffName(
    val first: String = "",
    val middle: String = "",
    val last: String = "",
    val full: String = "",
    val native: String = "",
    val alternative: List<String> = listOf(),
    val userPreferred: String = ""
)