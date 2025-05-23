package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.domain.di.AzkarRepositoryEntryPoint
import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarWidgetService
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleShortAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_SHORT_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.isDeviceNotLocked
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShortAzkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != ACTION_SHOW_SHORT_AZKAR) return

        val repository = EntryPointAccessors.fromApplication(
            context, AzkarRepositoryEntryPoint::class.java
        ).azkarRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val content = repository.fetchRandomZker()?.content

            if (shouldStartService(context, repository) && !content.isNullOrBlank()) {
                AzkarWidgetService.showAzkar(
                    context = context, content = content, viewType = ACTION_SHOW_SHORT_AZKAR
                )
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            scheduleShortAzkarScheduleWorker(context)
        }
    }

    private fun shouldStartService(context: Context, repository: AzkarRepository): Boolean {
        return !repository.isReminderOn() && isDeviceNotLocked(context)
    }
}
