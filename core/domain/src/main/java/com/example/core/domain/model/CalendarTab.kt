package com.example.core.domain.model

import androidx.annotation.StringRes
import com.example.core.domain.R

enum class CalendarTab {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    ;

    @get:StringRes
    val stringRes
        get() =
            when (this) {
                SUNDAY -> R.string.sunday
                MONDAY -> R.string.monday
                TUESDAY -> R.string.tuesday
                WEDNESDAY -> R.string.wednesday
                THURSDAY -> R.string.thursday
                FRIDAY -> R.string.friday
                SATURDAY -> R.string.saturday
            }
}
