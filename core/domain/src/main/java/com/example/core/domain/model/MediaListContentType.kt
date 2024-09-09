package com.example.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class MediaListContentType : Parcelable {
    RECENTLY_UPDATED,
    CURRENT_SEASON,
    POPULAR_NOW,
    NEXT_SEASON,
    POPULAR_MANGA,
    POPULAR_MANHWA,
    POPULAR_NOVEL,
    ONE_SHOT,
}
