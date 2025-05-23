package com.asmaa.khb.azkarpocapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleEveningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleMorningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleShortAzkarScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository,
    private val appScope: CoroutineScope,
) : ViewModel() {

    fun saveAndReScheduleEveningAzkarReminder(context: Context, time: ReminderAzkarTimeFormat) {
        appScope.launch {
            repository.saveEveningAzkarReminder(time)
            repository.onManuallyEveningReminderDismissed(false)
            scheduleEveningAzkarScheduleWorker(context)
        }
    }

    fun saveAndReScheduleMorningAzkarReminder(context: Context, time: ReminderAzkarTimeFormat) {
        appScope.launch {
            repository.saveMorningAzkarReminder(time)
            repository.onManuallyMorningReminderDismissed(false)
            scheduleMorningAzkarScheduleWorker(context)
        }
    }

    fun saveAndReScheduleShortAzkar(context: Context, value: ShortAzkarFrequency) {
        repository.saveShortAzkarFrequency(value)
        scheduleShortAzkarScheduleWorker(context)
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