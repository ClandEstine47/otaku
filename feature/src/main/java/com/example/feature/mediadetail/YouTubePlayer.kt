package com.example.feature.mediadetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.feature.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayer(
    videoId: String,
    thumbnailUrl: String,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by rememberSaveable { mutableStateOf(false) }

    val youTubePlayerView =
        remember {
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
            }
        }

    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    LaunchedEffect(youTubePlayerView) {
        val iFramePlayerOptions =
            IFramePlayerOptions.Builder()
                .controls(1)
                .build()
        youTubePlayerView.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player
                }
            },
            iFramePlayerOptions,
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    youTubePlayerView.release()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
    ) {
        if (!isPlaying) {
            Image(
                painter = rememberAsyncImagePainter(thumbnailUrl),
                contentDescription = "Video Thumbnail",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            isPlaying = true
                            youTubePlayer?.loadVideo(videoId, 0f)
                        },
                contentScale = ContentScale.Crop,
            )

            Image(
                painter = painterResource(id = R.drawable.youtube_play),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(72.dp)
                        .align(Alignment.Center),
            )
        } else {
            AndroidView(
                factory = {
                    youTubePlayerView
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
