package com.example.core.network.service

import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.*
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.TrendingNowQuery
import com.example.core.network.type.MediaFormat as NetworkMediaFormat
import com.example.core.network.type.MediaListStatus as NetworkMediaListStatus
import com.example.core.network.type.MediaStatus as NetworkMediaStatus
import com.example.core.network.type.MediaType as NetworkMediaType

fun RecentlyUpdatedQuery.AiringSchedule.toRecentlyUpdatedMedia(): AiringSchedule {
    return AiringSchedule(
        airingAt = airingAt,
        episode = episode,
        media = media?.toDomainMedia() ?: Media(),
    )
}

fun RecentlyUpdatedQuery.Media.toDomainMedia(): Media {
    return Media(
        idAniList = id,
        idMal = idMal,
        status = status?.toDomainMediaStatus(),
        chapters = chapters,
        episodes = episodes,
        isAdult = isAdult ?: false,
        type = type?.toDomainMediaType(),
        meanScore = meanScore ?: 0,
        isFavourite = isFavourite ?: false,
        format = format?.toDomainMediaFormat(),
        bannerImage = bannerImage.orEmpty(),
        countryOfOrigin = countryOfOrigin.toString(),
        coverImage = coverImage?.toDomainMediaCoverImage() ?: MediaCoverImage(),
        title =
            MediaTitle(
                english = title?.english ?: "",
                romaji = title?.romaji ?: "",
                userPreferred = title?.userPreferred ?: "",
            ),
        mediaListEntry =
            MediaList(
                progress = mediaListEntry?.progress ?: 0,
                private = mediaListEntry?.private ?: false,
                score = mediaListEntry?.score ?: 0.0,
                status = mediaListEntry?.status?.toDomainMediaListStatus(),
            ),
    )
}

fun TrendingNowQuery.Medium.toDomainMedia(): Media {
    return Media(
        idAniList = id,
        idMal = idMal,
        status = status?.toDomainMediaStatus(),
        chapters = chapters,
        episodes = episodes,
        nextAiringEpisode =
            AiringSchedule(
                episode = nextAiringEpisode?.episode,
            ),
        isAdult = isAdult ?: false,
        type = type?.toDomainMediaType(),
        meanScore = meanScore ?: 0,
        isFavourite = isFavourite ?: false,
        format = format?.toDomainMediaFormat(),
        bannerImage = bannerImage.orEmpty(),
        countryOfOrigin = countryOfOrigin.toString(),
        coverImage = coverImage?.toDomainMediaCoverImage() ?: MediaCoverImage(),
        title =
            MediaTitle(
                english = title?.english ?: "",
                romaji = title?.romaji ?: "",
                userPreferred = title?.userPreferred ?: "",
            ),
        mediaListEntry =
            MediaList(
                progress = mediaListEntry?.progress ?: 0,
                private = mediaListEntry?.private ?: false,
                score = mediaListEntry?.score ?: 0.0,
                status = mediaListEntry?.status?.toDomainMediaListStatus(),
            ),
    )
}

fun SeasonalAnimeQuery.Medium.toDomainMedia(): Media {
    return Media(
        idAniList = id,
        idMal = idMal,
        status = status?.toDomainMediaStatus(),
        chapters = chapters,
        episodes = episodes,
        nextAiringEpisode =
            AiringSchedule(
                episode = nextAiringEpisode?.episode,
            ),
        isAdult = isAdult ?: false,
        type = type?.toDomainMediaType(),
        meanScore = meanScore ?: 0,
        isFavourite = isFavourite,
        description = description,
        genres = genres,
        format = format?.toDomainMediaFormat(),
        bannerImage = bannerImage.orEmpty(),
        countryOfOrigin = countryOfOrigin.toString(),
        coverImage = coverImage?.toDomainMediaCoverImage() ?: MediaCoverImage(),
        title =
            MediaTitle(
                english = title?.english ?: "",
                romaji = title?.romaji ?: "",
                userPreferred = title?.userPreferred ?: "",
            ),
        mediaListEntry =
            MediaList(
                progress = mediaListEntry?.progress ?: 0,
                private = mediaListEntry?.private ?: false,
                score = mediaListEntry?.score ?: 0.0,
                status = mediaListEntry?.status?.toDomainMediaListStatus(),
            ),
    )
}

fun RecentlyUpdatedQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(large = large.orEmpty())
}

fun TrendingNowQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(large = large.orEmpty())
}

fun SeasonalAnimeQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(large = large.orEmpty())
}

fun NetworkMediaStatus?.toDomainMediaStatus(): MediaStatus? {
    return when (this) {
        NetworkMediaStatus.FINISHED -> MediaStatus.FINISHED
        NetworkMediaStatus.RELEASING -> MediaStatus.RELEASING
        NetworkMediaStatus.NOT_YET_RELEASED -> MediaStatus.NOT_YET_RELEASED
        NetworkMediaStatus.CANCELLED -> MediaStatus.CANCELLED
        NetworkMediaStatus.HIATUS -> MediaStatus.HIATUS
        else -> null
    }
}

fun NetworkMediaType?.toDomainMediaType(): MediaType? {
    return when (this) {
        NetworkMediaType.ANIME -> MediaType.ANIME
        NetworkMediaType.MANGA -> MediaType.MANGA
        else -> null
    }
}

fun NetworkMediaFormat?.toDomainMediaFormat(): MediaFormat? {
    return when (this) {
        NetworkMediaFormat.TV -> MediaFormat.TV
        NetworkMediaFormat.TV_SHORT -> MediaFormat.TV_SHORT
        NetworkMediaFormat.MOVIE -> MediaFormat.MOVIE
        NetworkMediaFormat.OVA -> MediaFormat.OVA
        NetworkMediaFormat.ONA -> MediaFormat.ONA
        NetworkMediaFormat.SPECIAL -> MediaFormat.SPECIAL
        NetworkMediaFormat.MUSIC -> MediaFormat.MUSIC
        NetworkMediaFormat.MANGA -> MediaFormat.MANGA
        NetworkMediaFormat.NOVEL -> MediaFormat.NOVEL
        NetworkMediaFormat.ONE_SHOT -> MediaFormat.ONE_SHOT
        else -> null
    }
}

fun NetworkMediaListStatus?.toDomainMediaListStatus(): MediaListStatus? {
    return when (this) {
        NetworkMediaListStatus.CURRENT -> MediaListStatus.CURRENT
        NetworkMediaListStatus.PLANNING -> MediaListStatus.PLANNING
        NetworkMediaListStatus.COMPLETED -> MediaListStatus.COMPLETED
        NetworkMediaListStatus.DROPPED -> MediaListStatus.DROPPED
        NetworkMediaListStatus.PAUSED -> MediaListStatus.PAUSED
        NetworkMediaListStatus.REPEATING -> MediaListStatus.REPEATING
        else -> null
    }
}

fun MediaSeason.toNetworkMediaSeason(): com.example.core.network.type.MediaSeason {
    return when (this) {
        MediaSeason.WINTER -> com.example.core.network.type.MediaSeason.WINTER
        MediaSeason.SPRING -> com.example.core.network.type.MediaSeason.SPRING
        MediaSeason.SUMMER -> com.example.core.network.type.MediaSeason.SUMMER
        MediaSeason.FALL -> com.example.core.network.type.MediaSeason.FALL
        MediaSeason.UNKNOWN -> com.example.core.network.type.MediaSeason.UNKNOWN__
    }
}

fun MediaType.toNetworkMediaType(): com.example.core.network.type.MediaType {
    return when (this) {
        MediaType.ANIME -> com.example.core.network.type.MediaType.ANIME
        MediaType.MANGA -> com.example.core.network.type.MediaType.MANGA
    }
}
