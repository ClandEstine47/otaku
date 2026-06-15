package com.example.feature.screens.notifications

import com.example.core.domain.model.notification.Notification
import com.example.core.domain.model.notification.NotificationType

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notificationsByTab: List<List<Notification>> = List(NotificationTab.entries.size) { emptyList() },
    val error: String? = null,
    val hasNextPageByTab: List<Boolean> = List(NotificationTab.entries.size) { false },
    val currentPageByTab: List<Int> = List(NotificationTab.entries.size) { 1 },
    val selectedTab: NotificationTab = NotificationTab.ALL,
)

enum class NotificationTab(
    val title: String,
    val types: List<NotificationType>?,
) {
    ALL("All", null),
    AIRING("Airing", listOf(NotificationType.AIRING)),
    MEDIA(
        "Media",
        listOf(
            NotificationType.RELATED_MEDIA_ADDITION,
            NotificationType.MEDIA_DATA_CHANGE,
            NotificationType.MEDIA_MERGE,
            NotificationType.MEDIA_DELETION,
        ),
    ),
}
