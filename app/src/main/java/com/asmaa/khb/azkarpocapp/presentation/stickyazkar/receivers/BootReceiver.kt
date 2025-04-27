package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// This receiver is kept as a case study for handling device reboots,
// where we may need to re-schedule alarms or reminders after the device is turned back on.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule alarms or services here if needed
        }
    }
}
