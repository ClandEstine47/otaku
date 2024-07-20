package com.example.core.domain.model.notification

enum class NotificationType {
    ACTIVITY_MESSAGE,           // A user has sent you message
    ACTIVITY_REPLY,             // A user has replied to your activity
    FOLLOWING,                  // A user has followed you
    ACTIVITY_MENTION,           // A user has mentioned you in their activity
    THREAD_COMMENT_MENTION,     // A user has mentioned you in a forum comment
    THREAD_SUBSCRIBED,          // A user has commented in one of your subscribed forum threads
    THREAD_COMMENT_REPLY,       // A user has replied to your forum comment
    AIRING,                     // An anime you are currently watching has aired
    ACTIVITY_LIKE,              // A user has liked your activity
    ACTIVITY_REPLY_LIKE,        // A user has liked your activity reply
    THREAD_LIKE,                // A user has liked your forum thread
    THREAD_COMMENT_LIKE,        // A user has liked your forum comment
    ACTIVITY_REPLY_SUBSCRIBED,  // A user has replied to activity you have also replied to
    RELATED_MEDIA_ADDITION,     // A new anime or manga has been added to the site where its related media is on the user's list
    MEDIA_DATA_CHANGE,          // An anime or manga has had a data change that affects how a user may track it in their lists
    MEDIA_MERGE,                // Anime or manga entries on the user's list have been merged into a single entry
    MEDIA_DELETION              // An anime or manga on the user's list has been deleted from the site
}