package com.asmaa.khb.azkarpocapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleEveningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleMorningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleShortAzkarScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AzkarRepository,
) : ViewModel() {

    fun insertInitialAzkarIfNeeded() {
        repository.insertInitialAzkarIfNeeded()
    }

    fun scheduleEveningMorningReminderAzkar(context: Context) {
        scheduleEveningAzkarScheduleWorker(context)
        scheduleMorningAzkarScheduleWorker(context)
    }

    fun scheduleShortAzkar(context: Context) {
        scheduleShortAzkarScheduleWorker(context)
    }
}