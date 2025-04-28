package com.asmaa.khb.azkarpocapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository
) : ViewModel() {

    fun saveAndReScheduleEveningAzkarReminder(time: ReminderAzkarTimeFormat) {
        viewModelScope.launch {
            repository.saveAndReScheduleEveningAzkarReminder(time)
        }
    }

    fun saveAndReScheduleMorningAzkarReminder(time: ReminderAzkarTimeFormat) {
        viewModelScope.launch {
            repository.saveAndReScheduleMorningAzkarReminder(time)
        }
    }

    fun saveAndReScheduleShortAzkar(value: ShortAzkarFrequency) {
        repository.saveAndReScheduleShortAzkar(value)
    }

    fun getCurrentFrequency(): ShortAzkarFrequency {
        return repository.getCurrentFrequency()
    }

    fun getEveningTimeFormat(): ReminderAzkarTimeFormat {
        return repository.getEveningTimeFormat()
    }

    fun getMorningTimeFormat(): ReminderAzkarTimeFormat {
        return repository.getMorningTimeFormat()
    }

    fun startPopupService() {
        viewModelScope.launch {
            repository.startPopupService()
        }
    }
}