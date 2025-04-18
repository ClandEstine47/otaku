package com.example.core.domain.model.user

import com.example.core.domain.model.ListActivityOption
import com.example.core.domain.model.notification.NotificationOption

data class UserOptions(
    var titleLanguage: UserTitleLanguage? = null,
    var displayAdultContent: Boolean = false,
    var airingNotifications: Boolean = false,
    val notificationOptions: List<NotificationOption> = listOf(),
    val timezone: String? = null,
    var activityMergeTime: Int = 0,
    var staffNameLanguage: UserStaffNameLanguage? = null,
    var restrictMessagesToFollowing: Boolean = false,
    var disabledListActivity: List<ListActivityOption> = listOf(),
)
