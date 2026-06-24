package com.example.feature.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(NotificationsUiState())
        val state = _state.asStateFlow()

        init {
            loadNotifications()
        }

        fun loadNotifications(
            isRefresh: Boolean = false,
            tab: NotificationTab? = null,
        ) {
            val targetTab = tab ?: _state.value.selectedTab
            val currentTabState = _state.value.tabs[targetTab] ?: NotificationTabState()

            if (currentTabState.isLoading) return

            _state.update { currentState ->
                val updatedTabState =
                    if (isRefresh) {
                        NotificationTabState(isLoading = true)
                    } else {
                        currentTabState.copy(isLoading = true)
                    }

                currentState.copy(
                    tabs = currentState.tabs + (targetTab to updatedTabState),
                )
            }

            viewModelScope.launch {
                val pageToLoad = if (isRefresh) 1 else currentTabState.currentPage

                mediaRepository
                    .getNotifications(
                        pageNumber = pageToLoad,
                        perPage = 20,
                        resetCount = true,
                        types = targetTab.types,
                    ).onSuccess { page ->
                        _state.update { currentState ->
                            val latestTabState = currentState.tabs[targetTab] ?: NotificationTabState()
                            val updatedNotifications =
                                if (isRefresh) page.data else latestTabState.notifications + page.data

                            val updatedTabState =
                                latestTabState.copy(
                                    notifications = updatedNotifications,
                                    currentPage = (if (isRefresh) 1 else latestTabState.currentPage) + 1,
                                    hasNextPage = page.pageInfo?.hasNextPage ?: false,
                                    isLoading = false,
                                    error = null,
                                )

                            currentState.copy(
                                tabs = currentState.tabs + (targetTab to updatedTabState),
                            )
                        }
                    }.onFailure { error ->
                        _state.update { currentState ->
                            val latestTabState = currentState.tabs[targetTab] ?: NotificationTabState()
                            currentState.copy(
                                tabs = currentState.tabs + (targetTab to latestTabState.copy(isLoading = false, error = error.message)),
                            )
                        }
                    }
            }
        }

        fun onTabSelected(tab: NotificationTab) {
            if (_state.value.selectedTab == tab) return
            _state.update { it.copy(selectedTab = tab) }

            val tabState = _state.value.tabs[tab]
            if (tabState == null || tabState.notifications.isEmpty()) {
                loadNotifications(tab = tab)
            }
        }
    }
