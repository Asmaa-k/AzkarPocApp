package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleEveningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleMorningAzkarScheduleWorker
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers.scheduleShortAzkarScheduleWorker

// This receiver is kept as a case study for handling device reboots,
// where we may need to re-schedule alarms or reminders after the device is turned back on.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleEveningAzkarScheduleWorker(context)
            scheduleMorningAzkarScheduleWorker(context)
            scheduleShortAzkarScheduleWorker(context)
        }
    }
}
