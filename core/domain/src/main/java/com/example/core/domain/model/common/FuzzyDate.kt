package com.example.core.domain.model.common

data class FuzzyDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
) {
    fun isNull(): Boolean {
        return year == null || month == null || day == null
    }
}
