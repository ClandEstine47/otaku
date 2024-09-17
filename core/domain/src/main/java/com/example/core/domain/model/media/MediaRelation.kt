package com.example.core.domain.model.media

import androidx.annotation.StringRes
import com.example.core.domain.R

enum class MediaRelation {
    ADAPTATION,
    PREQUEL,
    SEQUEL,
    PARENT,
    SIDE_STORY,
    CHARACTER,
    SUMMARY,
    ALTERNATIVE,
    SPIN_OFF,
    OTHER,
    SOURCE,
    COMPILATION,
    CONTAINS,
    UNKNOWN,
    ;

    @get:StringRes
    val stringRes
        get() =
            when (this) {
                ADAPTATION -> R.string.adaptation
                PREQUEL -> R.string.prequel
                SEQUEL -> R.string.sequel
                PARENT -> R.string.parent
                SIDE_STORY -> R.string.side_story
                CHARACTER -> R.string.character
                SUMMARY -> R.string.summary
                ALTERNATIVE -> R.string.alternative
                SPIN_OFF -> R.string.spin_off
                OTHER -> R.string.other
                SOURCE -> R.string.source
                COMPILATION -> R.string.compilation
                CONTAINS -> R.string.contains
                UNKNOWN -> R.string.unknown
            }
}
