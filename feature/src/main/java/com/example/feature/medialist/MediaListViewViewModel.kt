package com.example.feature.medialist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.CalendarTab
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaType
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
class MediaListViewViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val currentTime = LocalDateTime.now()
        private val currentAnimeSeason = currentTime.currentAnimeSeason()
        private val nextAnimeSeason = currentTime.nextAnimeSeason()

        private val _state =
            MutableStateFlow(
                MediaListUiState(),
            )
        val state = _state.asStateFlow()

        fun loadMediaList(
            mediaType: MediaType,
            contentType: MediaListContentType,
        ) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }

                val mediaListDeferred =
                    async {
                        when (contentType) {
                            MediaListContentType.RECENTLY_UPDATED -> {
                                val startOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = false)
                                val endOfDay = currentTime.getDayTimestamp(dayOffset = _state.value.dayOffset, isEndOfDay = true)

                                mediaRepository.getRecentlyUpdatedAnimeList(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 30,
                                    airingAtLesser = endOfDay.toInt(),
                                    airingAtGreater = startOfDay.toInt(),
                                )
                            }
                            MediaListContentType.CURRENT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    seasonYear = currentAnimeSeason.year,
                                    season = currentAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.POPULAR_NOW -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.NEXT_SEASON -> {
                                mediaRepository.getSeasonalMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    seasonYear = nextAnimeSeason.year,
                                    season = nextAnimeSeason.season,
                                    mediaType = mediaType,
                                )
                            }
                            MediaListContentType.POPULAR_MANGA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    countryOfOrigin = "JP",
                                )
                            }
                            MediaListContentType.POPULAR_MANHWA -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    countryOfOrigin = "KR",
                                )
                            }
                            MediaListContentType.POPULAR_NOVEL -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.NOVEL,
                                )
                            }
                            MediaListContentType.ONE_SHOT -> {
                                mediaRepository.getPopularMedia(
                                    pageNumber = _state.value.pageNumber,
                                    perPage = 21,
                                    mediaType = mediaType,
                                    mediaFormat = MediaFormat.ONE_SHOT,
                                )
                            }
                        }
                    }

                val mediaListResult = mediaListDeferred.await()

                _state.update { currentState ->
                    when {
                        mediaListResult.isSuccess -> {
                            val mediaListByPage = currentState.mediaListByPage.toMutableList()
                            mediaListByPage[0] =
                                mediaListResult.getOrNull()?.data?.map { media ->
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
                                isLoading = false,
                                hasNextPage = mediaListResult.getOrNull()?.pageInfo?.hasNextPage,
                                error = null,
                            )
                        }

                        mediaListResult.isFailure ->
                            currentState.copy(
                                mediaListByPage = emptyList(),
                                isLoading = false,
                                error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                            )

                        else ->
                            currentState.copy(
                                isLoading = false,
                                error = "Unexpected result state",
                            )
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

                            mediaListResult.isFailure ->
                                currentState.copy(
                                    mediaListByPage = emptyList(),
                                    isLoading = false,
                                    error = mediaListResult.exceptionOrNull()?.message ?: "An unknown error occurred",
                                )

                            else ->
                                currentState.copy(
                                    isLoading = false,
                                    error = "Unexpected result state",
                                )
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
