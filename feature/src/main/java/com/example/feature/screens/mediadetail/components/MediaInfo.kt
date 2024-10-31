package com.example.feature.screens.mediadetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.feature.R
import com.example.feature.Utils
import com.example.feature.common.OtakuTitle

@Composable
fun MediaInfo(
    media: Media,
) {
    val meanScore = (media.meanScore.toDouble() / 10)
    val isReleasing = media.status == MediaStatus.RELEASING
    val episodes = ((if (isReleasing) "${media.nextAiringEpisode?.episode?.minus(1) ?: "?"} | ${media.episodes ?: "?"}" else media.episodes) ?: "?").toString()
    val startDate =
        if (media.startDate?.isNull() == true) {
            "?"
        } else {
            Utils.formatDateToText(
                year = media.startDate?.year ?: 0,
                month = media.startDate?.month ?: 0,
                day = media.startDate?.day ?: 0,
            )
        }
    val endDate =
        if (media.endDate?.isNull() == true) {
            "?"
        } else {
            Utils.formatDateToText(
                year = media.endDate?.year ?: 0,
                month = media.endDate?.month ?: 0,
                day = media.endDate?.day ?: 0,
            )
        }
    val season =
        if (media.season != null && media.seasonYear != null) {
            "${media.season?.name} ${media.seasonYear}"
        } else {
            "?"
        }
    val popularity = (media.popularity ?: "?").toString()
    val favourites = (media.favourites ?: "?").toString()
    val studios = mutableListOf<String>()
    val producers = mutableListOf<String>()

    media.studios?.edges?.forEach { edge ->
        if (edge.node.name.isNotBlank()) {
            if (edge.isMain) studios.add(edge.node.name) else producers.add(edge.node.name)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TagItem(
            key = R.string.title_romaji,
            value = media.title.romaji.ifBlank(defaultValue = { "-" }),
        )
        TagItem(
            key = R.string.title_english,
            value = media.title.english.ifBlank(defaultValue = { "-" }),
        )
        TagItem(
            key = R.string.title_native,
            value = media.title.native.ifBlank(defaultValue = { "-" }),
        )
        media.synonyms?.let { synonymsList ->
            TagItem(
                key = R.string.synonyms,
                value = synonymsList.joinToString(separator = ", "),
            )
        }

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
            thickness = 0.2.dp,
            color = MaterialTheme.colorScheme.onBackground,
        )

        TagItem(key = R.string.mean_score, value = "$meanScore | 10")
        TagItem(key = R.string.status, value = media.status?.name ?: "-")
        TagItem(key = R.string.format, value = media.format?.name ?: "-")
        if (media.type == MediaType.ANIME) {
            TagItem(key = R.string.season, value = season)
            TagItem(key = R.string.episodes, value = episodes)
            TagItem(key = R.string.episode_duration, value = "${media.duration ?: "?"} mins")
        } else {
            TagItem(key = R.string.chapters, value = "${media.chapters ?: "?"}")
        }
        TagItem(key = R.string.source, value = media.source?.name ?: "-")
        TagItem(key = R.string.start_date, value = startDate)
        TagItem(key = R.string.end_date, value = endDate)
        TagItem(key = R.string.popularity, value = popularity)
        TagItem(key = R.string.favourites, value = favourites)

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
            thickness = 0.2.dp,
            color = MaterialTheme.colorScheme.onBackground,
        )

        TagItem(key = R.string.studios, value = studios.joinToString(separator = "\n"))
        TagItem(key = R.string.producers, value = producers.joinToString(separator = "\n"))
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    key: Int,
    value: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OtakuTitle(id = key, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontWeight = FontWeight.Light, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.width(50.dp))
        OtakuTitle(title = value, textAlign = TextAlign.End, fontWeight = FontWeight.Light, style = MaterialTheme.typography.bodyMedium)
    }
}
