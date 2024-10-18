package com.example.core.domain.model.media

import androidx.annotation.StringRes
import com.example.core.domain.R

enum class MediaStatus {
    FINISHED,
    RELEASING,
    NOT_YET_RELEASED,
    CANCELLED,
    HIATUS,
    ;

    companion object {
        val statusList = MediaStatus.entries.toList()
    }

    @get:StringRes
    val stringRes
        get() =
            when (this) {
                FINISHED -> R.string.finished
                RELEASING -> R.string.releasing
                NOT_YET_RELEASED -> R.string.not_yet_released
                CANCELLED -> R.string.cancelled
                HIATUS -> R.string.hiatus
            }
}
