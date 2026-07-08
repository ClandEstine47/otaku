package com.example.otaku.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.core.domain.model.notification.Notification
import com.example.core.domain.repository.MainRepository
import com.example.core.domain.repository.MediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val mainRepository: MainRepository,
        private val mediaRepository: MediaRepository,
        private val notificationHelper: NotificationHelper,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            if (!mainRepository.isLoggedIn().first()) {
                return Result.success()
            }

            val lastTimestamp = mainRepository.getLastNotificationTimestamp().first()

            return mediaRepository.getNotifications(pageNumber = 1, perPage = 20, resetCount = false).fold(
                onSuccess = { page ->
                    val notifications = page.data
                    if (notifications.isEmpty()) {
                        return@fold Result.success()
                    }

                    val maxTimestamp = notifications.maxOfOrNull { it.createdAt } ?: 0

                    // First run: avoid flooding user with historic notifications
                    if (lastTimestamp == 0) {
                        mainRepository.setLastNotificationTimestamp(maxTimestamp)
                        return@fold Result.success()
                    }

                    val newNotifications = notifications.filter { it.createdAt > lastTimestamp }

                    if (newNotifications.isNotEmpty()) {
                        newNotifications.forEach { notification ->
                            showNotification(notification)
                        }

                        mainRepository.setLastNotificationTimestamp(newNotifications.maxOf { it.createdAt })
                    }

                    Result.success()
                },
            ) {
                Timber.e(it, "Failed to fetch notifications in worker")
                Result.retry()
            }
        }

        private fun showNotification(notification: Notification) {
            val title = "Otaku"
            val message: String
            val deepLink: String

            when (notification) {
                is Notification.Airing -> {
                    message = "Episode ${notification.episode} of ${notification.media.title.userPreferred} aired"
                    deepLink =
                        notificationHelper.getMediaDetailDeepLink(
                            notification.animeId,
                            notification.media.type?.name ?: "ANIME",
                        )
                }

                is Notification.RelatedMediaAddition -> {
                    message = "New related media added: ${notification.media.title.userPreferred}"
                    deepLink =
                        notificationHelper.getMediaDetailDeepLink(
                            notification.mediaId,
                            notification.media.type?.name ?: "ANIME",
                        )
                }

                is Notification.MediaDataChange -> {
                    message = "Data change for ${notification.media.title.userPreferred}: ${notification.reason ?: ""}"
                    deepLink =
                        notificationHelper.getMediaDetailDeepLink(
                            notification.mediaId,
                            notification.media.type?.name ?: "ANIME",
                        )
                }

                is Notification.MediaMerge -> {
                    message = "Media merged: ${notification.media.title.userPreferred}"
                    deepLink =
                        notificationHelper.getMediaDetailDeepLink(
                            notification.mediaId,
                            notification.media.type?.name ?: "ANIME",
                        )
                }

                is Notification.MediaDeletion -> {
                    message = "Media deleted: ${notification.deletedMediaTitle ?: "Media"}"
                    deepLink = notificationHelper.getNotificationsDeepLink()
                }

                is Notification.Unknown -> {
                    message = "You have a new notification"
                    deepLink = notificationHelper.getNotificationsDeepLink()
                }
            }

            notificationHelper.showNotification(
                id = notification.id,
                title = title,
                message = message,
                deepLinkUri = deepLink,
            )
        }

        companion object {
            const val WORK_NAME = "notification_work"

            fun schedule(context: Context) {
                val constraints =
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                val request =
                    PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
                        .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
            }

            fun cancel(context: Context) {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            }
        }
    }
