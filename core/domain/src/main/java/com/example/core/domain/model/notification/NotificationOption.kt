package com.example.core.domain.model.notification

data class NotificationOption(
    val type: NotificationType? = null,
    var enabled: Boolean = false
)