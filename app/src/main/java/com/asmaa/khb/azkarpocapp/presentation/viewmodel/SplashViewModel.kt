package com.asmaa.khb.azkarpocapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: AzkarRepository) : ViewModel() {

    fun insertInitialAzkarIfNeeded() {
        viewModelScope.launch {
            repository.insertInitialAzkarIfNeeded()
        }
    }

    fun scheduleEveningMorningReminderAzkar() {
        viewModelScope.launch {
            repository.scheduleMorningAzkarReceiver()
            repository.scheduleEveningAzkarReceiver()
        }
    }

    fun scheduleShortAzkar() {
        viewModelScope.launch {
            repository.scheduleShortAzkarReceiver()
        }
    }
}