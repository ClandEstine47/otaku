package com.example.otaku

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.repository.MainRepository
import com.example.feature.OTAKU_SCHEME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val mainRepository: MainRepository,
    ) : ViewModel() {
        val isLoggedIn = mainRepository.isLoggedIn()

        fun onIntentDataReceived(data: Uri?) {
            if (data?.scheme == OTAKU_SCHEME) {
                viewModelScope.launch {
                    mainRepository.parseRedirectUri(data)
                }
            }
        }
    }
