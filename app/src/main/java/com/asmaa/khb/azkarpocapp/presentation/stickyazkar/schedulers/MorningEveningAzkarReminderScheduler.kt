package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.schedulers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.receivers.MorningEveningAzkarReminderReceiver
import com.asmaa.khb.azkarpocapp.presentation.util.Constants
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EVENING_REMINDER_ALARM_REQUEST_CODE
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.MORNING_REMINDER_ALARM_REQUEST_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class MorningEveningAzkarReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun scheduleMorningAzkar(timeFormat: ReminderAzkarTimeFormat) {
        val (hour, minute) = convertTo24Hour(timeFormat)
        val intent = createReminderIntent(
            requestCode = MORNING_REMINDER_ALARM_REQUEST_CODE,
            alarmAction = ACTION_SHOW_MORNING_AZKAR,
            text = context.getString(R.string.morning_azkar_reminder_txt),
            imageRes = R.drawable.img_sunrises
        )
        scheduleExactDailyAlarm(
            hour = hour,
            minute = minute,
            intent = intent
        )
    }

    fun scheduleEveningAzkar(timeFormat: ReminderAzkarTimeFormat) {
        val (hour, minute) = convertTo24Hour(timeFormat)
        val intent = createReminderIntent(
            requestCode = EVENING_REMINDER_ALARM_REQUEST_CODE,
            alarmAction = ACTION_SHOW_EVENING_AZKAR,
            text = context.getString(R.string.evening_azkar_reminder_txt),
            imageRes = R.drawable.img_sunset
        )
        scheduleExactDailyAlarm(
            hour = hour,
            minute = minute,
            intent = intent
        )
    }

    private fun createReminderIntent(
        requestCode: Int,
        alarmAction: String,
        text: String, imageRes: Int
    ): PendingIntent {
        val intent = Intent(context, MorningEveningAzkarReminderReceiver::class.java).apply {
            action = alarmAction
            putExtra(EXTRA_TEXT_CONTENT, text)
            putExtra(EXTRA_IMAGE_RES, imageRes)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleExactDailyAlarm(
        hour: Int,
        minute: Int,
        intent: PendingIntent
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intent
        )
    }

    private fun convertTo24Hour(timeFormat: ReminderAzkarTimeFormat): Pair<Int, Int> {
        val hour = timeFormat.hourOfDay
        val minute = timeFormat.minute
        val isAm = timeFormat.period == Constants.CONST_AM
        val hour24 = when {
            hour == 12 && isAm -> 0
            hour == 12 && !isAm -> 12
            isAm -> hour
            else -> hour + 12
        }
        return Pair(hour24, minute)
    }

    fun cancelMorningAzkar() {
        val intent = createReminderIntent(
            MORNING_REMINDER_ALARM_REQUEST_CODE,
            ACTION_SHOW_MORNING_AZKAR,
            "",
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intent)
    }

    fun cancelEveningAzkar() {
        val intent = createReminderIntent(
            EVENING_REMINDER_ALARM_REQUEST_CODE,
            ACTION_SHOW_EVENING_AZKAR,
            "",
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intent)
    }
}
