package com.example.core.domain.model.media

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class MediaType : Parcelable {
    ANIME,
    MANGA,
}
