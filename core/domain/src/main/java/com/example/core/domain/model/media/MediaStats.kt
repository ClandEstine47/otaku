package com.example.core.domain.model.media

import com.example.core.domain.model.ScoreDistribution
import com.example.core.domain.model.StatusDistribution

data class MediaStats(
    val scoreDistribution: List<ScoreDistribution> = listOf(),
    val statusDistribution: List<StatusDistribution> = listOf(),
)
