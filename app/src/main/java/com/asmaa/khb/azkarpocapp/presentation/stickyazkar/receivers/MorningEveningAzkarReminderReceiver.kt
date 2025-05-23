package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.domain.di.AzkarRepositoryEntryPoint
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarWidgetService
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.reScheduleEveningAzkarScheduleWorkerForShorterTime
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.reScheduleMorningAzkarScheduleWorkerForShorterTime
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleEveningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleMorningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import com.asmaa.khb.azkarpocapp.presentation.util.isDeviceNotLocked
import com.asmaa.khb.azkarpocapp.presentation.util.isWithinReminderTimeSpan
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MorningEveningAzkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != ACTION_SHOW_MORNING_AZKAR && action != ACTION_SHOW_EVENING_AZKAR) {
            return
        }

        val repository = EntryPointAccessors.fromApplication(
            context, AzkarRepositoryEntryPoint::class.java
        ).azkarRepository()

        val isManualDismiss = shouldSkipReminderDueToManualDismissal(action, repository)
        val isDeviceUnlocked = isDeviceNotLocked(context)
        val canRetryReminder = repository.canStartReminderWithinRetryLimit()

        if ((!isManualDismiss) && (isDeviceUnlocked || canRetryReminder)) {
            val content = intent.getStringExtra(EXTRA_TEXT_CONTENT).orEmpty()
            val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES, -1)
            repository.setIsReminderOn(true)
            AzkarWidgetService.showAzkar(
                context = context, content = content, imgRes = imageResId, viewType = action
            )
        }

        reScheduleReceivers(
            context,
            repository,
            isManualDismiss,
            isDeviceUnlocked,
            intent.action!!,
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reScheduleReceivers(
        context: Context,
        repository: AzkarRepository,
        isManualDismiss: Boolean,
        isDeviceUnlocked: Boolean,
        action: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val isMorning = action == ACTION_SHOW_MORNING_AZKAR
            val reminderTime = repository.getCurrentCumulativeReminderTime(
                if (isMorning) repository.getMorningTimeFormat()
                else repository.getEveningTimeFormat()
            )

            val shouldRetry =
                !isDeviceUnlocked && !isManualDismiss && isWithinReminderTimeSpan(reminderTime)

            if (shouldRetry) {
                repository.setReminderRetriesCount(repository.getReminderRetriesCount() + 1)
                rescheduleShorterTime(context, isMorning)
            } else {
                restReminderFlags(repository)
                scheduleFullReminder(context, isMorning)
            }
        }
    }

    private fun restReminderFlags(repository: AzkarRepository) {
        repository.resetReminderRetriesCount()
        repository.restCumulativeReminderTime()
        repository.onManuallyEveningReminderDismissed(false)
        repository.onManuallyMorningReminderDismissed(false)
    }

    private fun rescheduleShorterTime(context: Context, isMorning: Boolean) {
        if (isMorning) {
            reScheduleMorningAzkarScheduleWorkerForShorterTime(context)
        } else {
            reScheduleEveningAzkarScheduleWorkerForShorterTime(context)
        }
    }

    private fun scheduleFullReminder(context: Context, isMorning: Boolean) {
        if (isMorning) {
            scheduleMorningAzkarScheduleWorker(context)
        } else {
            scheduleEveningAzkarScheduleWorker(context)
        }
    }

    private fun shouldSkipReminderDueToManualDismissal(
        action: String, repository: AzkarRepository
    ): Boolean {
        return when (action) {
            ACTION_SHOW_MORNING_AZKAR -> {
                repository.isManuallyMorningReminderDismissed()
            }

            ACTION_SHOW_EVENING_AZKAR -> {
                repository.isManuallyEveningReminderDismissed()
            }

            else -> false
        }
    }
}