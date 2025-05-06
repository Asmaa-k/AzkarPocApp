package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.domain.di.AzkarRepositoryEntryPoint
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarWidgetService
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import com.asmaa.khb.azkarpocapp.presentation.util.isDeviceNotLocked
import com.asmaa.khb.azkarpocapp.presentation.util.isWithinReminderTimeSpan
import dagger.hilt.android.EntryPointAccessors

class MorningEveningAzkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        if (action != ACTION_SHOW_MORNING_AZKAR && action != ACTION_SHOW_EVENING_AZKAR) return
        val repository = EntryPointAccessors.fromApplication(
            context, AzkarRepositoryEntryPoint::class.java
        ).azkarRepository()

        val isDeviceUnlocked = isDeviceNotLocked(context)
        val canRetryReminder = repository.canStartReminderWithinRetryLimit()

        if (isDeviceUnlocked || canRetryReminder) {
            val content = intent.getStringExtra(EXTRA_TEXT_CONTENT).orEmpty()
            val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES, -1)
            repository.setIsReminderOn(true)
            AzkarWidgetService.showAzkar(
                context = context,
                content = content,
                imgRes = imageResId,
                reminderViewType = true
            )
        }

        reScheduleReceivers(intent.action!!, isDeviceUnlocked, repository)
    }

    private fun reScheduleReceivers(
        action: String,
        isDeviceUnlocked: Boolean,
        repository: AzkarRepository
    ) {
        val reminderTime = when (action) {
            ACTION_SHOW_MORNING_AZKAR -> repository.getMorningTimeFormat()
            ACTION_SHOW_EVENING_AZKAR -> repository.getEveningTimeFormat()
            else -> return
        }

        val isValidTimeSpan = isWithinReminderTimeSpan(reminderTime)

        if (!isDeviceUnlocked && isValidTimeSpan) {
            repository.setReminderRetriesCount(repository.getReminderRetriesCount() + 1)
            when (action) {
                ACTION_SHOW_MORNING_AZKAR -> repository.reScheduleMorningAzkarReceiverWithinShortTime(
                    reminderTime
                )

                ACTION_SHOW_EVENING_AZKAR -> repository.reScheduleEveningAzkarReceiverWithinShortTime(
                    reminderTime
                )
            }
        } else {
            repository.resetReminderRetriesCount()
            when (action) {
                ACTION_SHOW_MORNING_AZKAR -> repository.scheduleMorningAzkarReceiver()
                ACTION_SHOW_EVENING_AZKAR -> repository.scheduleEveningAzkarReceiver()
            }
        }
    }
}