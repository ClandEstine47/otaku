package com.example.feature.anime

import androidx.lifecycle.ViewModel
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {


}