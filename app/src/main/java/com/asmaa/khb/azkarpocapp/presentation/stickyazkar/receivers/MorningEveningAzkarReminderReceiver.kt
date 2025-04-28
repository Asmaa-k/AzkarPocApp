package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.domain.di.AzkarRepositoryEntryPoint
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarWidgetService
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import dagger.hilt.android.EntryPointAccessors

class MorningEveningAzkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        if (action != ACTION_SHOW_MORNING_AZKAR && action != ACTION_SHOW_EVENING_AZKAR) return
        val repository = EntryPointAccessors.fromApplication(
            context, AzkarRepositoryEntryPoint::class.java
        ).azkarRepository()

        val content = intent.getStringExtra(EXTRA_TEXT_CONTENT).orEmpty()
        val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES, -1)

        repository.setIsReminderOn(true)


        AzkarWidgetService.showAzkar(context = context, content = content, imgRes = imageResId)

        if (intent.action == ACTION_SHOW_MORNING_AZKAR) {
            repository.scheduleMorningAzkarReceiver()
        } else if (intent.action == ACTION_SHOW_EVENING_AZKAR) {
            repository.scheduleEveningAzkarReceiver()
        }
    }
}
