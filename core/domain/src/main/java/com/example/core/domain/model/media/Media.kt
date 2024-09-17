package com.example.core.domain.model.media

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.airing.AiringScheduleConnection
import com.example.core.domain.model.character.CharacterConnection
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.recommendation.RecommendationConnection
import com.example.core.domain.model.staff.StaffConnection
import com.example.core.domain.model.studio.StudioConnection

data class Media(
    val idAniList: Int = 0,
    val idMal: Int? = null,
    val title: MediaTitle = MediaTitle(),
    val type: MediaType? = null,
    val format: MediaFormat? = null,
    val status: MediaStatus? = null,
    val description: String? = "",
    val startDate: FuzzyDate? = null,
    val endDate: FuzzyDate? = null,
    val season: MediaSeason? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val countryOfOrigin: String? = null,
    val isLicensed: Boolean? = null,
    val source: MediaSource? = null,
    val trailer: MediaTrailer? = null,
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val bannerImage: String = "",
    val genres: List<String?>? = listOf(),
    val synonyms: List<String>? = listOf(),
    val averageScore: Int = 0,
    val meanScore: Int = 0,
    val popularity: Int? = 0,
    val trending: Int? = 0,
    val favourites: Int? = 0,
    val tags: List<MediaTag>? = listOf(),
    val relations: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val staff: StaffConnection = StaffConnection(),
    val studios: StudioConnection? = StudioConnection(),
    var isFavourite: Boolean = false,
    val isAdult: Boolean = false,
    val nextAiringEpisode: AiringSchedule? = null,
    val airingSchedule: AiringScheduleConnection = AiringScheduleConnection(),
    val externalLinks: List<MediaExternalLink>? = listOf(),
    val rankings: List<MediaRank>? = listOf(),
    val recommendations: RecommendationConnection = RecommendationConnection(),
    val stats: MediaStats? = null,
    val siteUrl: String? = "",
    val mediaListEntry: MediaList? = null,
)
