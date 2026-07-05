package com.example.feature.screens.medialist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.CalendarTab
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.MediaListItem
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.medialistcollection.MediaListSort
import com.example.core.domain.repository.MediaRepository
import com.example.feature.Utils.currentAnimeSeason
import com.example.feature.Utils.getDayTimestamp
import com.example.feature.Utils.nextAnimeSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private data class LoadSignature(
            val mediaId: Int?,
            val mediaType: MediaType,
            val contentType: MediaListContentType,
            val userId: Int?,
            val status: MediaListStatus?,
        )

        private val currentTime = LocalDateTime.now()
        private val currentAnimeSeason = currentTime.currentAnimeSeason()
        private val nextAnimeSeason = currentTime.nextAnimeSeason()
        private var lastInitialLoadSignature: LoadSignature? = null

        private val _state =
            MutableStateFlow(
                MediaListUiState(),
            )
        val state = _state.asStateFlow()

        fun loadMediaList(
            mediaId: Int?,
            mediaType: MediaType,
            contentType: MediaListContentType,
            userId: Int? = null,
            status: MediaListStatus? = null,
            loadMore: Boolean = false,
            targetPageNumber: Int? = null,
            skipIfAlreadyLoaded: Boolean = false,
        ) {
            viewModelScope.launch {
                val signature = LoadSignature(mediaId, mediaType, contentType, userId, status)

                // Avoid re-fetching the same initial screen state when coming back from a detail screen.
                if (!loadMore && skipIfAlreadyLoaded && _state.value.hasLoadedInitialData && lastInitialLoadSignature == signature) {
                    return@launch
                }

                val pageToLoad =
                    when {
                        targetPageNumber != null -> targetPageNumber.coerceAtLeast(1)
                        loadMore -> _state.value.pageNumber + 1
                        else -> 1
                    }

                if (!loadMore) {
                    lastInitialLoadSignature = signature

                    // User lists can either be split into status tabs or filtered down to one status.
                    val userTabCount =
                        if (contentType == MediaListContentType.USER_CURRENT_ANIME || contentType == MediaListContentType.USER_CURRENT_MANGA) {
                            if (status == null) getUserListTabStatuses(mediaType).size else 1
                        } else {
                            7
                        }

                    _state.update {
                        it.copy(
                            mediaListByPage = List(userTabCount) { emptyList() },
                            userListTabCounts = if (userTabCount == 7) emptyList() else List(userTabCount) { 0 },
                            userListMedia = emptyList(),
                            pageNumber = pageToLoad,
                            hasNextPage = false,
                            isLoading = true,
                            isLoadingMore = false,
                            hasLoadedInitialData = false,
                            error = null,
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoadingMore = true,
                            error = null,
                        )
                    }
                }

                val mediaListDeferred =
                    async {
                        when (contentType) {
                            MediaListContentType.RECENTLY_UPDATED -> {
                                val startOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = false)
                                val endOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = true)

                                mediaRepository.getRecentlyUpdatedAnimeList(
                                    pageNumber = pageToLoad,
                                    perPage = 30,
                                    airingAtLesser = endOfDay.toInt(),
                                    airingAtGreater = startOfDay.toInt(),
                                )
                            }

                            MediaListContentType.CURRENT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    seasonYear = currentAnimeSeason.year,
                                    season = currentAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }

                            MediaListContentType.POPULAR_NOW -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    mediaType = mediaType,
                                )
                            }

                            MediaListContentType.NEXT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    seasonYear = nextAnimeSeason.year,
                                    season = nextAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }

                            MediaListContentType.POPULAR_MANGA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    countryOfOrigin = "JP",
                                )
                            }

                            MediaListContentType.POPULAR_MANHWA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    countryOfOrigin = "KR",
                                )
                            }

                            MediaListContentType.POPULAR_NOVEL -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.NOVEL,
                                )
                            }

                            MediaListContentType.ONE_SHOT -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = pageToLoad,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.ONE_SHOT,
                                )
                            }

                            MediaListContentType.RECOMMENDED -> {
                                mediaRepository.getMediaRecommendationsById(
                                    id = mediaId!!,
                                )
                            }

                            MediaListContentType.USER_CURRENT_ANIME -> {
                                mediaRepository.getUserListCollection(
                                    pageNumber = pageToLoad,
                                    perPage = 30,
                                    mediaType = mediaType,
                                    userId = userId,
                                    sortBy = listOf(MediaListSort.UPDATED_TIME_DESC),
                                )
                            }

                            MediaListContentType.USER_CURRENT_MANGA -> {
                                mediaRepository.getUserListCollection(
                                    pageNumber = pageToLoad,
                                    perPage = 30,
                                    mediaType = mediaType,
                                    userId = userId,
                                    sortBy = listOf(MediaListSort.UPDATED_TIME_DESC),
                                )
                            }
                        }
                    }

                val mediaListResult = mediaListDeferred.await()

                _state.update { currentState ->
                    when {
                        mediaListResult.isSuccess -> {
                            val mediaListResultData = mediaListResult.getOrNull()?.data.orEmpty()

                            if (contentType == MediaListContentType.USER_CURRENT_ANIME || contentType == MediaListContentType.USER_CURRENT_MANGA) {
                                @Suppress("UNCHECKED_CAST")
                                val userMediaListData = mediaListResult.getOrNull()?.data as? List<Media> ?: emptyList()
                                val userTabStatuses = getUserListTabStatuses(mediaType)
                                val accumulatedUserMedia =
                                    if (loadMore) {
                                        currentState.userListMedia + userMediaListData
                                    } else {
                                        userMediaListData
                                    }
                                val userTabPages =
                                    if (status == null) {
                                        // Build one page per status tab, with the first tab showing all media.
                                        val groupedMedia = accumulatedUserMedia.groupBy { it.mediaListEntry?.status }
                                        userTabStatuses.map { tabStatus ->
                                            val tabMedia =
                                                if (tabStatus == null) {
                                                    accumulatedUserMedia
                                                } else {
                                                    groupedMedia[tabStatus].orEmpty()
                                                }

                                            tabMedia.map { media ->
                                                MediaListItem.MediaListType(media)
                                            }
                                        }
                                    } else {
                                        // When tabs are hidden, keep only the selected status in a single page.
                                        val filteredMedia = accumulatedUserMedia.filter { it.mediaListEntry?.status == status }
                                        listOf(
                                            filteredMedia.map { media ->
                                                MediaListItem.MediaListType(media)
                                            },
                                        )
                                    }

                                currentState.copy(
                                    mediaListByPage = userTabPages,
                                    userListTabCounts = userTabPages.map { it.size },
                                    userListMedia = accumulatedUserMedia,
                                    pageNumber = pageToLoad,
                                    isLoading = false,
                                    isLoadingMore = false,
                                    hasLoadedInitialData = true,
                                    hasNextPage = mediaListResult.getOrNull()?.pageInfo?.hasNextPage == true,
                                    error = null,
                                )
                            } else {
                                val mediaListByPage = currentState.mediaListByPage.toMutableList()
                                mediaListByPage[0] =
                                    mediaListResultData.map { media ->
                                        when (media) {
                                            is Media -> {
                                                MediaListItem.MediaListType(media)
                                            }

                                            is AiringSchedule -> {
                                                MediaListItem.ScheduleType(media)
                                            }

                                            else -> {
                                                MediaListItem.MediaListType(Media())
                                            }
                                        }
                                    }

                                currentState.copy(
                                    mediaListByPage = mediaListByPage,
                                    pageNumber = pageToLoad,
                                    isLoading = false,
                                    isLoadingMore = false,
                                    hasLoadedInitialData = true,
                                    hasNextPage = mediaListResult.getOrNull()?.pageInfo?.hasNextPage,
                                    error = null,
                                )
                            }
                        }

                        mediaListResult.isFailure -> {
                            if (contentType == MediaListContentType.USER_CURRENT_ANIME || contentType == MediaListContentType.USER_CURRENT_MANGA) {
                                currentState.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    hasLoadedInitialData = true,
                                    hasNextPage = false,
                                    error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                                )
                            } else {
                                currentState.copy(
                                    mediaListByPage = emptyList(),
                                    isLoading = false,
                                    isLoadingMore = false,
                                    hasLoadedInitialData = true,
                                    error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                                )
                            }
                        }

                        else -> {
                            currentState.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                hasLoadedInitialData = true,
                                error = "Unexpected result state",
                            )
                        }
                    }
                }
            }
        }

        fun loadMediaListByDay(dayIndex: Int) {
            viewModelScope.launch {
                if (_state.value.mediaListByPage[dayIndex]?.isEmpty() == true) {
                    _state.update { it.copy(isLoading = true) }
                    val mediaListDeferred =
                        async {
                            val startOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = false)
                            val endOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = true)

                            mediaRepository.getRecentlyUpdatedAnimeList(
                                pageNumber = 1,
                                perPage = 30,
                                airingAtLesser = endOfDay.toInt(),
                                airingAtGreater = startOfDay.toInt(),
                            )
                        }

                    val mediaListResult = mediaListDeferred.await()

                    _state.update { currentState ->
                        when {
                            mediaListResult.isSuccess -> {
                                val mediaListByPage = currentState.mediaListByPage.toMutableList()

                                mediaListByPage[dayIndex] =
                                    mediaListResult.getOrNull()?.data?.map { media ->
                                        MediaListItem.ScheduleType(media)
                                    }

                                currentState.copy(
                                    mediaListByPage = mediaListByPage,
                                    isLoading = false,
                                    hasNextPage = mediaListResult.getOrNull()?.pageInfo?.hasNextPage,
                                    error = null,
                                )
                            }

                            mediaListResult.isFailure -> {
                                currentState.copy(
                                    mediaListByPage = emptyList(),
                                    isLoading = false,
                                    error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                                )
                            }

                            else -> {
                                currentState.copy(
                                    isLoading = false,
                                    error = "Unexpected result state",
                                )
                            }
                        }
                    }
                }
            }
        }

        fun setDayOffset(value: Int) =
            _state.update {
                it.copy(
                    dayOffset = value,
                )
            }

        @Suppress("unused")
        fun setSelectedStatus(value: MediaListStatus?) {
            _state.update {
                it.copy(
                    selectedStatus = value,
                    pageNumber = 1,
                    mediaListByPage = List(7) { emptyList() },
                )
            }
        }

        fun getUserListTabLabels(mediaType: MediaType): List<String> =
            when (mediaType) {
                MediaType.ANIME -> listOf("ALL", "WATCHING", "PLANNING", "COMPLETED", "DROPPED", "PAUSED", "REWATCHING")
                MediaType.MANGA -> listOf("ALL", "READING", "PLANNING", "COMPLETED", "DROPPED", "PAUSED", "REREADING")
            }

        private fun getUserListTabStatuses(mediaType: MediaType): List<MediaListStatus?> =
            when (mediaType) {
                MediaType.ANIME -> {
                    listOf(
                        null,
                        MediaListStatus.CURRENT,
                        MediaListStatus.PLANNING,
                        MediaListStatus.COMPLETED,
                        MediaListStatus.DROPPED,
                        MediaListStatus.PAUSED,
                        MediaListStatus.REPEATING,
                    )
                }

                MediaType.MANGA -> {
                    listOf(
                        null,
                        MediaListStatus.CURRENT,
                        MediaListStatus.PLANNING,
                        MediaListStatus.COMPLETED,
                        MediaListStatus.DROPPED,
                        MediaListStatus.PAUSED,
                        MediaListStatus.REPEATING,
                    )
                }
            }

        fun increasePageNumber() =
            _state.update {
                it.copy(
                    pageNumber = _state.value.pageNumber + 1,
                )
            }

        fun decreasePageNumber() =
            _state.update {
                it.copy(
                    pageNumber = _state.value.pageNumber - 1,
                )
            }

        fun getSortedCalendarTabs(): List<CalendarTab> {
            val today = LocalDate.now().dayOfWeek
            // Rotate the week so the current day becomes the first tab.
            val sortedDays = DayOfWeek.entries.sortedBy { (it.value - today.value + 7) % 7 }

            return sortedDays.map { dayOfWeek ->
                when (dayOfWeek) {
                    DayOfWeek.SUNDAY -> CalendarTab.SUNDAY
                    DayOfWeek.MONDAY -> CalendarTab.MONDAY
                    DayOfWeek.TUESDAY -> CalendarTab.TUESDAY
                    DayOfWeek.WEDNESDAY -> CalendarTab.WEDNESDAY
                    DayOfWeek.THURSDAY -> CalendarTab.THURSDAY
                    DayOfWeek.FRIDAY -> CalendarTab.FRIDAY
                    DayOfWeek.SATURDAY -> CalendarTab.SATURDAY
                }
            }
        }
    }
