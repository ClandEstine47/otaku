package com.example.core.domain.model.media

data class MediaExternalLink(
    val id: Int = 0,
    val url: String = "",
    val site: String = "",
    val siteId: Int = 0,
    val type: ExternalLinkType? = null,
    val language: String = "",
    val color: String = "",
    val icon: String = "",
)
