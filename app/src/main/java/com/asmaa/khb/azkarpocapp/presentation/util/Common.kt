package com.asmaa.khb.azkarpocapp.presentation.util

import android.content.Context
import android.os.PowerManager
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import java.util.Calendar

fun isDeviceNotLocked(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isInteractive
}

fun isWithinReminderTimeSpan(time: ReminderAzkarTimeFormat, spanHours: Int = 4): Boolean {
    val hour = time.hourOfDay
    val isAm = time.period == Constants.CONST_AM

    val reminderMinute = time.minute
    val reminderHour = when {
        hour == 12 && isAm -> 0
        hour == 12 && !isAm -> 12
        isAm -> hour
        else -> hour + 12
    }

    val now = Calendar.getInstance()

    val reminderStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, reminderHour)
        set(Calendar.MINUTE, reminderMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val reminderEnd = Calendar.getInstance().apply {
        timeInMillis = reminderStart.timeInMillis
        add(Calendar.HOUR_OF_DAY, spanHours)
    }

    return now.timeInMillis in reminderStart.timeInMillis..reminderEnd.timeInMillis
}

fun ReminderAzkarTimeFormat.incrementByTwoMinutes(): ReminderAzkarTimeFormat {
    var newHour = this.hourOfDay
    var newMinute = this.minute + 2

    if (newMinute >= 60) {
        newMinute -= 60
        newHour += 1
    }

    // AM/PM switch logic
    var newPeriod = this.period
    if (newHour == 12 && this.hourOfDay < 12) {
        newPeriod =
            if (this.period == Constants.CONST_AM) Constants.CONST_PM else Constants.CONST_AM
    } else if (newHour > 12) {
        newHour -= 12
    }

    return ReminderAzkarTimeFormat(newHour, newMinute, newPeriod)
}


