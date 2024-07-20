package com.example.core.domain.model.media

import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.user.User

data class MediaList(
    val id: Int? = null,
    var status: MediaListStatus? = null,
    var score: Double = 0.0,
    var progress: Int = 0,
    var progressVolumes: Int? = null,
    var repeat: Int = 0,
    var priority: Int = 0,
    var private: Boolean = false,
    var notes: String = "",
    var hiddenFromStatusLists: Boolean = false,
    var customLists: Any? = null,
    var advancedScores: Any? = null,
    var startedAt: FuzzyDate? = null,
    var completedAt: FuzzyDate? = null,
    var updatedAt: Int = 0,
    var createdAt: Int = 0,
    val media: Media = Media(),
    val user: User = User()
)