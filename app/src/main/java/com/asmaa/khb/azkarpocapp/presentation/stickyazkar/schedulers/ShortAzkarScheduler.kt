package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers.ShortAzkarReceiver
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_SHORT_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.SHORT_AZKAR_ALARM_REQUEST_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ShortAzkarScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleShortAzkar(frequency: ShortAzkarFrequency) {
        val intent = Intent(context, ShortAzkarReceiver::class.java).apply {
            action = ACTION_SHOW_SHORT_AZKAR
        }
        val pendingIntent = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ SHORT_AZKAR_ALARM_REQUEST_CODE,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val startAt =
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(frequency.intervalInSec.toLong())

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            startAt,
            pendingIntent
        )
    }

    fun cancelShortAzkar() {
        val intent = Intent(context, ShortAzkarReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
