package com.example.core.domain.model

import androidx.annotation.StringRes
import com.example.core.domain.R

enum class Countries {
    CHINA,
    SOUTH_KOREA,
    JAPAN,
    TAIWAN,
    ;

    @get:StringRes
    val stringRes
        get() =
            when (this) {
                CHINA -> R.string.china
                SOUTH_KOREA -> R.string.south_korea
                JAPAN -> R.string.japan
                TAIWAN -> R.string.taiwan
            }

    val code
        get() =
            when (this) {
                CHINA -> "CN"
                SOUTH_KOREA -> "KR"
                JAPAN -> "JP"
                TAIWAN -> "TW"
            }

    companion object {
        val countries = Countries.entries.toList()
    }
}
