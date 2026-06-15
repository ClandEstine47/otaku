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

        fun loadNotifications(isRefresh: Boolean = false) {
            val currentTab = _state.value.selectedTab
            val tabIndex = currentTab.ordinal

            if (_state.value.isLoading) return

            _state.update {
                if (isRefresh) {
                    val newNotificationsByTab = it.notificationsByTab.toMutableList()
                    newNotificationsByTab[tabIndex] = emptyList()
                    val newCurrentPageByTab = it.currentPageByTab.toMutableList()
                    newCurrentPageByTab[tabIndex] = 1

                    it.copy(
                        isLoading = true,
                        notificationsByTab = newNotificationsByTab,
                        currentPageByTab = newCurrentPageByTab,
                    )
                } else {
                    it.copy(isLoading = true)
                }
            }

            viewModelScope.launch {
                val currentState = _state.value
                val currentPage = currentState.currentPageByTab[tabIndex]
                val selectedTab = currentState.selectedTab

                mediaRepository
                    .getNotifications(
                        pageNumber = currentPage,
                        perPage = 20,
                        resetCount = true,
                        types = selectedTab.types,
                    ).onSuccess { page ->
                        _state.update { updatedState ->
                            val newNotificationsByTab = updatedState.notificationsByTab.toMutableList()
                            newNotificationsByTab[tabIndex] = newNotificationsByTab[tabIndex] + page.data

                            val newHasNextPageByTab = updatedState.hasNextPageByTab.toMutableList()
                            newHasNextPageByTab[tabIndex] = page.pageInfo?.hasNextPage ?: false

                            val newCurrentPageByTab = updatedState.currentPageByTab.toMutableList()
                            newCurrentPageByTab[tabIndex] = updatedState.currentPageByTab[tabIndex] + 1

                            updatedState.copy(
                                isLoading = false,
                                notificationsByTab = newNotificationsByTab,
                                hasNextPageByTab = newHasNextPageByTab,
                                currentPageByTab = newCurrentPageByTab,
                                error = null,
                            )
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
            }
        }

        fun onTabSelected(tab: NotificationTab) {
            if (_state.value.selectedTab == tab) return
            _state.update { it.copy(selectedTab = tab) }
            // Only load if the tab hasn't been loaded yet
            if (_state.value.notificationsByTab[tab.ordinal].isEmpty()) {
                loadNotifications()
            }
        }
    }
