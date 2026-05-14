package com.example.feature

const val OTAKU_SCHEME = "com.example.otaku"
const val ANILIST_URL = "https://anilist.co"
const val ANILIST_API_URL = "$ANILIST_URL/api/v2"
const val ANILIST_AUTH_URL = "$ANILIST_API_URL/oauth/authorize"
const val OTAKU_AUTH_URL =
    "${ANILIST_AUTH_URL}?client_id=${BuildConfig.CLIENT_ID}&response_type=token"
