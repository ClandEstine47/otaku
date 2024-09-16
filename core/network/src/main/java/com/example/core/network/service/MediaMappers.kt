package com.example.core.network.service

import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.*
import com.example.core.network.MediaQuery
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.TrendingNowQuery
import com.example.core.network.type.MediaFormat as NetworkMediaFormat
import com.example.core.network.type.MediaListStatus as NetworkMediaListStatus
import com.example.core.network.type.MediaRankType as NetworkMediaRankType
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
        genres = genres,
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

fun RecentlyUpdatedQuery.PageInfo.toDomainPageInfo(): PageInfo {
    return PageInfo(
        total = total,
        currentPage = currentPage,
        hasNextPage = hasNextPage,
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
        description = description,
        genres = genres,
        meanScore = meanScore ?: 0,
        isFavourite = isFavourite,
        rankings = rankings?.map { it?.toDomainRankings() ?: MediaRank() },
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

fun TrendingNowQuery.PageInfo.toDomainPageInfo(): PageInfo {
    return PageInfo(
        total = total,
        currentPage = currentPage,
        hasNextPage = hasNextPage,
    )
}

fun SeasonalAnimeQuery.PageInfo.toDomainPageInfo(): PageInfo {
    return PageInfo(
        total = total,
        currentPage = currentPage,
        hasNextPage = hasNextPage,
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

fun MediaQuery.Media.toDomainMedia(): Media {
    return Media(
        idAniList = id,
        idMal = idMal,
        status = status?.toDomainMediaStatus(),
        chapters = chapters,
        episodes = episodes,
        nextAiringEpisode =
            AiringSchedule(
                airingAt = nextAiringEpisode?.airingAt,
                timeUntilAiring = nextAiringEpisode?.timeUntilAiring,
                episode = nextAiringEpisode?.episode,
            ),
        isAdult = isAdult ?: false,
        type = type?.toDomainMediaType(),
        description = description,
        genres = genres,
        meanScore = meanScore ?: 0,
        isFavourite = isFavourite,
        popularity = popularity,
        trending = trending,
        favourites = favourites,
        rankings = rankings?.map { it?.toDomainRankings() ?: MediaRank() },
        format = format?.toDomainMediaFormat(),
        bannerImage = bannerImage.orEmpty(),
        countryOfOrigin = countryOfOrigin.toString(),
        coverImage = coverImage?.toDomainMediaCoverImage() ?: MediaCoverImage(),
        title =
            MediaTitle(
                english = title?.english ?: "",
                romaji = title?.romaji ?: "",
                native = title?.native ?: "",
                userPreferred = title?.userPreferred ?: "",
            ),
        mediaListEntry =
            MediaList(
                progress = mediaListEntry?.progress ?: 0,
                private = mediaListEntry?.private ?: false,
                score = mediaListEntry?.score ?: 0.0,
                status = mediaListEntry?.status?.toDomainMediaListStatus(),
            ),
        trailer =
            MediaTrailer(
                id = trailer?.id,
                site = trailer?.site,
                thumbnail = trailer?.thumbnail,
            ),
        externalLinks = externalLinks?.map { it?.toDomainExternalLink() ?: MediaExternalLink() },
        siteUrl = siteUrl,
    )
}

fun MediaQuery.ExternalLink.toDomainExternalLink(): MediaExternalLink {
    return MediaExternalLink(
        url = url,
        site = site,
        color = color,
        icon = icon,
    )
}

fun TrendingNowQuery.Ranking.toDomainRankings(): MediaRank {
    return MediaRank(
        id = id,
        rank = rank,
        allTime = allTime,
        type = type.toDomainMediaRankType(),
    )
}

fun MediaQuery.Ranking.toDomainRankings(): MediaRank {
    return MediaRank(
        id = id,
        rank = rank,
        allTime = allTime,
        type = type.toDomainMediaRankType(),
    )
}

fun NetworkMediaRankType.toDomainMediaRankType(): MediaRankType {
    return when (this) {
        NetworkMediaRankType.RATED -> MediaRankType.RATED
        NetworkMediaRankType.POPULAR -> MediaRankType.POPULAR
        NetworkMediaRankType.UNKNOWN__ -> MediaRankType.UNKNOWN
    }
}

fun RecentlyUpdatedQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(large = large.orEmpty(), extraLarge = extraLarge.orEmpty())
}

fun TrendingNowQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(
        large = large.orEmpty(),
        extraLarge = extraLarge.orEmpty(),
    )
}

fun SeasonalAnimeQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(
        large = large.orEmpty(),
        extraLarge = extraLarge.orEmpty(),
    )
}

fun MediaQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
    return MediaCoverImage(
        large = large.orEmpty(),
        extraLarge = extraLarge.orEmpty(),
    )
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

fun MediaFormat.toNetworkMediaFormat(): com.example.core.network.type.MediaFormat {
    return when (this) {
        MediaFormat.TV -> com.example.core.network.type.MediaFormat.TV
        MediaFormat.TV_SHORT -> com.example.core.network.type.MediaFormat.TV_SHORT
        MediaFormat.MOVIE -> com.example.core.network.type.MediaFormat.MOVIE
        MediaFormat.OVA -> com.example.core.network.type.MediaFormat.OVA
        MediaFormat.ONA -> com.example.core.network.type.MediaFormat.ONA
        MediaFormat.SPECIAL -> com.example.core.network.type.MediaFormat.SPECIAL
        MediaFormat.MUSIC -> com.example.core.network.type.MediaFormat.MUSIC
        MediaFormat.MANGA -> com.example.core.network.type.MediaFormat.MANGA
        MediaFormat.NOVEL -> com.example.core.network.type.MediaFormat.NOVEL
        MediaFormat.ONE_SHOT -> com.example.core.network.type.MediaFormat.ONE_SHOT
    }
}
