package com.example.feature.screens.notifications

import com.example.core.domain.model.notification.Notification
import com.example.core.domain.model.notification.NotificationType

data class NotificationTabState(
    val notifications: List<Notification> = emptyList(),
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class NotificationsUiState(
    val selectedTab: NotificationTab = NotificationTab.ALL,
    val tabs: Map<NotificationTab, NotificationTabState> =
        NotificationTab.entries.associateWith { NotificationTabState() },
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
