package com.example.feature.manga

import androidx.lifecycle.ViewModel
import com.example.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MangaViewModel
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) : ViewModel()
