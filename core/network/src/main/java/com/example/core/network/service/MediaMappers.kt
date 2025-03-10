package com.example.core.network.service

import com.example.core.domain.model.PageInfo
import com.example.core.domain.model.ScoreDistribution
import com.example.core.domain.model.StatusDistribution
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.character.Character
import com.example.core.domain.model.character.CharacterConnection
import com.example.core.domain.model.character.CharacterEdge
import com.example.core.domain.model.character.CharacterImage
import com.example.core.domain.model.character.CharacterName
import com.example.core.domain.model.character.CharacterRole
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.*
import com.example.core.domain.model.recommendation.Recommendation
import com.example.core.domain.model.recommendation.RecommendationConnection
import com.example.core.domain.model.review.Review
import com.example.core.domain.model.review.ReviewConnection
import com.example.core.domain.model.review.ReviewEdge
import com.example.core.domain.model.review.ReviewRating
import com.example.core.domain.model.staff.Staff
import com.example.core.domain.model.staff.StaffConnection
import com.example.core.domain.model.staff.StaffEdge
import com.example.core.domain.model.staff.StaffImage
import com.example.core.domain.model.staff.StaffName
import com.example.core.domain.model.studio.Studio
import com.example.core.domain.model.studio.StudioConnection
import com.example.core.domain.model.studio.StudioEdge
import com.example.core.domain.model.thread.Thread
import com.example.core.domain.model.user.User
import com.example.core.domain.model.user.UserAvatar
import com.example.core.network.MediaQuery
import com.example.core.network.MediaSearchQuery
import com.example.core.network.MediaThreadsQuery
import com.example.core.network.RecentlyUpdatedQuery
import com.example.core.network.SeasonalAnimeQuery
import com.example.core.network.TrendingNowQuery
import com.example.core.network.MediaQuery.Studios as NetworkStudios
import com.example.core.network.type.CharacterRole as NetworkCharacterRole
import com.example.core.network.type.MediaFormat as NetworkMediaFormat
import com.example.core.network.type.MediaListStatus as NetworkMediaListStatus
import com.example.core.network.type.MediaRankType as NetworkMediaRankType
import com.example.core.network.type.MediaRelation as NetworkMediaRelation
import com.example.core.network.type.MediaSeason as NetworkMediaSeason
import com.example.core.network.type.MediaSource as NetworkMediaSource
import com.example.core.network.type.MediaStatus as NetworkMediaStatus
import com.example.core.network.type.MediaType as NetworkMediaType
import com.example.core.network.type.ReviewRating as NetworkReviewRating

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

fun MediaThreadsQuery.PageInfo.toDomainPageInfo(): PageInfo {
    return PageInfo(
        total = total,
        currentPage = currentPage,
        hasNextPage = hasNextPage,
    )
}

fun MediaSearchQuery.PageInfo.toDomainPageInfo(): PageInfo {
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

fun MediaThreadsQuery.Thread.toDomainThread(): Thread {
    return Thread(
        id = id,
        title = title,
        body = body,
        isLiked = isLiked,
        isLocked = isLocked,
        isSubscribed = isSubscribed,
        likeCount = likeCount,
        replyCount = totalReplies,
        viewCount = viewCount,
        user = user?.toDomainMediaThreadUser(),
        createdAt = createdAt,
    )
}

fun MediaThreadsQuery.User.toDomainMediaThreadUser(): User {
    return User(
        id = id,
        name = name,
        avatar =
            UserAvatar(
                medium = avatar?.medium,
                large = avatar?.large,
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
        duration = duration,
        startDate =
            FuzzyDate(
                year = startDate?.year,
                month = startDate?.month,
                day = startDate?.day,
            ),
        endDate =
            FuzzyDate(
                year = endDate?.year,
                month = endDate?.month,
                day = endDate?.day,
            ),
        nextAiringEpisode =
            AiringSchedule(
                airingAt = nextAiringEpisode?.airingAt,
                timeUntilAiring = nextAiringEpisode?.timeUntilAiring,
                episode = nextAiringEpisode?.episode,
            ),
        season = season?.toDomainMediaSeason(),
        seasonYear = seasonYear,
        isAdult = isAdult ?: false,
        type = type?.toDomainMediaType(),
        description = description,
        source = source?.toDomainMediaSource(),
        synonyms = synonyms?.filterNotNull(),
        genres = genres,
        meanScore = meanScore ?: 0,
        averageScore = averageScore ?: 0,
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
        studios = studios?.toDomainStudios(),
        tags = tags?.map { it?.toDomainMediaTag() ?: MediaTag() },
        relations =
            MediaConnection(
                edges = relations?.toDomainMediaRelations(),
            ),
        recommendations =
            RecommendationConnection(
                nodes = recommendations?.toDomainMediaRecommendations(),
            ),
        stats =
            MediaStats(
                scoreDistribution = stats?.scoreDistribution?.map { it?.toDomainScoreDistribution() ?: ScoreDistribution() },
                statusDistribution = stats?.statusDistribution?.map { it?.toDomainStatusDistribution() ?: StatusDistribution() },
            ),
        characters =
            CharacterConnection(
                edges = characters?.toDomainCharacters(),
            ),
        staff =
            StaffConnection(
                edges = staff?.toDomainStaffs(),
            ),
        review =
            ReviewConnection(
                edges = reviews?.toDomainReviews(),
            ),
    )
}

fun MediaSearchQuery.Medium.toDomainMedia(): Media {
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

fun MediaQuery.Reviews.toDomainReviews(): List<ReviewEdge>? {
    return edges?.map { edge ->
        ReviewEdge(
            node =
                Review(
                    id = edge?.node?.id,
                    summary = edge?.node?.summary,
                    ratingAmount = edge?.node?.ratingAmount,
                    score = edge?.node?.score,
                    rating = edge?.node?.rating,
                    createdAt = edge?.node?.createdAt,
                    user =
                        User(
                            name = edge?.node?.user?.name,
                            avatar =
                                UserAvatar(
                                    medium = edge?.node?.user?.avatar?.medium,
                                    large = edge?.node?.user?.avatar?.large,
                                ),
                        ),
                    mediaType = edge?.node?.mediaType.toDomainMediaType(),
                    userRating = edge?.node?.userRating?.toDomainReviewRating(),
                ),
        )
    }
}

fun MediaQuery.ScoreDistribution.toDomainScoreDistribution(): ScoreDistribution {
    return ScoreDistribution(
        score = score,
        amount = amount,
    )
}

fun MediaQuery.StatusDistribution.toDomainStatusDistribution(): StatusDistribution {
    return StatusDistribution(
        status = status.toDomainMediaListStatus(),
        amount = amount,
    )
}

fun NetworkCharacterRole.toDomainCharacterRole(): CharacterRole {
    return when (this) {
        NetworkCharacterRole.MAIN -> CharacterRole.MAIN
        NetworkCharacterRole.SUPPORTING -> CharacterRole.SUPPORTING
        NetworkCharacterRole.BACKGROUND -> CharacterRole.BACKGROUND
        NetworkCharacterRole.UNKNOWN__ -> CharacterRole.UNKNOWN
    }
}

fun MediaQuery.Characters.toDomainCharacters(): List<CharacterEdge>? {
    return edges?.map { edge ->
        CharacterEdge(
            role = edge?.role?.toDomainCharacterRole(),
            node =
                Character(
                    id = edge?.node?.id,
                    name =
                        CharacterName(
                            full = edge?.node?.name?.full,
                        ),
                    image =
                        CharacterImage(
                            medium = edge?.node?.image?.medium,
                            large = edge?.node?.image?.large,
                        ),
                ),
        )
    }
}

fun MediaQuery.Staff.toDomainStaffs(): List<StaffEdge>? {
    return edges?.map { edge ->
        StaffEdge(
            role = edge?.role,
            node =
                Staff(
                    id = edge?.node?.id,
                    name =
                        StaffName(
                            full = edge?.node?.name?.full,
                        ),
                    image =
                        StaffImage(
                            medium = edge?.node?.image?.medium,
                            large = edge?.node?.image?.large,
                        ),
                ),
        )
    }
}

fun MediaQuery.Recommendations.toDomainMediaRecommendations(): List<Recommendation>? {
    return edges?.map { edge -> Recommendation(mediaRecommendation = edge?.node?.mediaRecommendation?.toDomainMediaRecommendation()) }
}

fun MediaQuery.MediaRecommendation.toDomainMediaRecommendation(): Media {
    return Media(
        idAniList = id,
        idMal = idMal,
        title =
            MediaTitle(
                english = title?.english ?: "",
                romaji = title?.romaji ?: "",
            ),
        type = type.toDomainMediaType(),
        format = format.toDomainMediaFormat(),
        status = status.toDomainMediaStatus(),
        episodes = episodes,
        chapters = chapters,
        volumes = volumes,
        coverImage =
            MediaCoverImage(
                large = coverImage?.large ?: "",
            ),
        meanScore = meanScore ?: 0,
        nextAiringEpisode =
            AiringSchedule(
                episode = nextAiringEpisode?.episode,
            ),
    )
}

fun MediaQuery.Relations.toDomainMediaRelations(): List<MediaEdge>? {
    return edges?.map { edge -> edge?.toDomainMediaEdge() ?: MediaEdge() }
}

fun MediaQuery.Edge1.toDomainMediaEdge(): MediaEdge {
    return MediaEdge(
        relationType = relationType?.toDomainMediaRelation(),
        node =
            Media(
                idAniList = node?.id ?: 0,
                idMal = node?.idMal,
                title =
                    MediaTitle(
                        english = node?.title?.english ?: "",
                        romaji = node?.title?.romaji ?: "",
                    ),
                type = node?.type.toDomainMediaType(),
                format = node?.format.toDomainMediaFormat(),
                status = node?.status.toDomainMediaStatus(),
                episodes = node?.episodes,
                chapters = node?.chapters,
                volumes = node?.volumes,
                coverImage =
                    MediaCoverImage(
                        large = node?.coverImage?.large ?: "",
                    ),
                meanScore = node?.meanScore ?: 0,
                nextAiringEpisode =
                    AiringSchedule(
                        episode = node?.nextAiringEpisode?.episode,
                    ),
            ),
    )
}

fun NetworkMediaRelation.toDomainMediaRelation(): MediaRelation {
    return when (this) {
        NetworkMediaRelation.ADAPTATION -> MediaRelation.ADAPTATION
        NetworkMediaRelation.PREQUEL -> MediaRelation.PREQUEL
        NetworkMediaRelation.SEQUEL -> MediaRelation.SEQUEL
        NetworkMediaRelation.PARENT -> MediaRelation.PARENT
        NetworkMediaRelation.SIDE_STORY -> MediaRelation.SIDE_STORY
        NetworkMediaRelation.CHARACTER -> MediaRelation.CHARACTER
        NetworkMediaRelation.SUMMARY -> MediaRelation.SUMMARY
        NetworkMediaRelation.ALTERNATIVE -> MediaRelation.ALTERNATIVE
        NetworkMediaRelation.SPIN_OFF -> MediaRelation.SPIN_OFF
        NetworkMediaRelation.OTHER -> MediaRelation.OTHER
        NetworkMediaRelation.SOURCE -> MediaRelation.SOURCE
        NetworkMediaRelation.COMPILATION -> MediaRelation.COMPILATION
        NetworkMediaRelation.CONTAINS -> MediaRelation.CONTAINS
        NetworkMediaRelation.UNKNOWN__ -> MediaRelation.UNKNOWN
    }
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

fun NetworkStudios.toDomainStudios(): StudioConnection {
    return StudioConnection(
        edges = edges?.map { edge -> edge?.toDomainEdge() ?: StudioEdge() },
    )
}

fun MediaQuery.Edge.toDomainEdge(): StudioEdge {
    return StudioEdge(
        isMain = isMain,
        node =
            Studio(
                name = node?.name ?: "",
            ),
    )
}

fun NetworkMediaSource.toDomainMediaSource(): MediaSource {
    return when (this) {
        NetworkMediaSource.ORIGINAL -> MediaSource.ORIGINAL
        NetworkMediaSource.MANGA -> MediaSource.MANGA
        NetworkMediaSource.LIGHT_NOVEL -> MediaSource.LIGHT_NOVEL
        NetworkMediaSource.VISUAL_NOVEL -> MediaSource.VISUAL_NOVEL
        NetworkMediaSource.VIDEO_GAME -> MediaSource.VIDEO_GAME
        NetworkMediaSource.OTHER -> MediaSource.OTHER
        NetworkMediaSource.NOVEL -> MediaSource.NOVEL
        NetworkMediaSource.DOUJINSHI -> MediaSource.DOUJINSHI
        NetworkMediaSource.ANIME -> MediaSource.ANIME
        NetworkMediaSource.WEB_NOVEL -> MediaSource.WEB_NOVEL
        NetworkMediaSource.LIVE_ACTION -> MediaSource.LIVE_ACTION
        NetworkMediaSource.GAME -> MediaSource.GAME
        NetworkMediaSource.COMIC -> MediaSource.COMIC
        NetworkMediaSource.MULTIMEDIA_PROJECT -> MediaSource.MULTIMEDIA_PROJECT
        NetworkMediaSource.PICTURE_BOOK -> MediaSource.PICTURE_BOOK
        NetworkMediaSource.UNKNOWN__ -> MediaSource.UNKNOWN
    }
}

fun MediaQuery.Tag.toDomainMediaTag(): MediaTag {
    return MediaTag(
        name = name,
        rank = rank,
        isGeneralSpoiler = isGeneralSpoiler,
    )
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

fun MediaSearchQuery.CoverImage.toDomainMediaCoverImage(): MediaCoverImage {
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

fun NetworkReviewRating.toDomainReviewRating(): ReviewRating {
    return when (this) {
        NetworkReviewRating.NO_VOTE -> ReviewRating.NO_VOTE
        NetworkReviewRating.UP_VOTE -> ReviewRating.UP_VOTE
        NetworkReviewRating.DOWN_VOTE -> ReviewRating.DOWN_VOTE
        NetworkReviewRating.UNKNOWN__ -> ReviewRating.UNKNOWN
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

fun NetworkMediaSeason.toDomainMediaSeason(): MediaSeason {
    return when (this) {
        NetworkMediaSeason.WINTER -> MediaSeason.WINTER
        NetworkMediaSeason.SPRING -> MediaSeason.SPRING
        NetworkMediaSeason.SUMMER -> MediaSeason.SUMMER
        NetworkMediaSeason.FALL -> MediaSeason.FALL
        NetworkMediaSeason.UNKNOWN__ -> MediaSeason.UNKNOWN
    }
}

fun MediaSeason.toNetworkMediaSeason(): NetworkMediaSeason {
    return when (this) {
        MediaSeason.WINTER -> NetworkMediaSeason.WINTER
        MediaSeason.SPRING -> NetworkMediaSeason.SPRING
        MediaSeason.SUMMER -> NetworkMediaSeason.SUMMER
        MediaSeason.FALL -> NetworkMediaSeason.FALL
        MediaSeason.UNKNOWN -> NetworkMediaSeason.UNKNOWN__
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

fun MediaStatus.toNetworkMediaStatus(): com.example.core.network.type.MediaStatus {
    return when (this) {
        MediaStatus.FINISHED -> com.example.core.network.type.MediaStatus.FINISHED
        MediaStatus.RELEASING -> com.example.core.network.type.MediaStatus.RELEASING
        MediaStatus.NOT_YET_RELEASED -> com.example.core.network.type.MediaStatus.NOT_YET_RELEASED
        MediaStatus.CANCELLED -> com.example.core.network.type.MediaStatus.CANCELLED
        MediaStatus.HIATUS -> com.example.core.network.type.MediaStatus.HIATUS
    }
}

fun MediaSort.toNetworkMediaSort(): com.example.core.network.type.MediaSort {
    return when (this) {
        MediaSort.SCORE -> com.example.core.network.type.MediaSort.SCORE_DESC
        MediaSort.POPULARITY -> com.example.core.network.type.MediaSort.POPULARITY_DESC
        MediaSort.TRENDING -> com.example.core.network.type.MediaSort.TRENDING_DESC
        MediaSort.FAVOURITES -> com.example.core.network.type.MediaSort.FAVOURITES_DESC
    }
}
