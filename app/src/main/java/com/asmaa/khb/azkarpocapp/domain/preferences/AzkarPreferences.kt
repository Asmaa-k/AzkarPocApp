package com.asmaa.khb.azkarpocapp.domain.preferences

import android.content.Context
import com.asmaa.khb.azkarpocapp.domain.util.Constants.DEFAULT_AZKAR_FREQUENCY
import com.asmaa.khb.azkarpocapp.domain.util.Constants.DEFAULT_EVENING_REMINDER
import com.asmaa.khb.azkarpocapp.domain.util.Constants.DEFAULT_MORNING_REMINDER
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_KEY_EVENING_REMINDER_TIME
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_KEY_FREQUENCY
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_KEY_IS_REMINDER_ON
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_KEY_MORNING_REMINDER_TIME
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_KEY_NAME
import com.asmaa.khb.azkarpocapp.domain.util.Constants.PREFS_REMINDER_RETRIES_COUNT_LIMIT
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.CONST_AM
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.CONST_PM
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AzkarPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs = context.getSharedPreferences(PREFS_KEY_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveEveningTimeFormat(time: ReminderAzkarTimeFormat) {
        val json = gson.toJson(time)
        prefs.edit().putString(PREFS_KEY_EVENING_REMINDER_TIME, json).apply()
    }

    fun getEveningTimeFormat(): ReminderAzkarTimeFormat {
        val json = prefs.getString(PREFS_KEY_EVENING_REMINDER_TIME, null)
        return json?.let { gson.fromJson(it, ReminderAzkarTimeFormat::class.java) }
            ?: ReminderAzkarTimeFormat(DEFAULT_EVENING_REMINDER, 0, CONST_PM)

    }

    fun saveMorningTimeFormat(time: ReminderAzkarTimeFormat) {
        val json = gson.toJson(time)
        prefs.edit().putString(PREFS_KEY_MORNING_REMINDER_TIME, json).apply()
    }

    fun getMorningTimeFormat(): ReminderAzkarTimeFormat {
        val json = prefs.getString(PREFS_KEY_MORNING_REMINDER_TIME, null)
        return json?.let { gson.fromJson(it, ReminderAzkarTimeFormat::class.java) }
            ?: ReminderAzkarTimeFormat(DEFAULT_MORNING_REMINDER, 0, CONST_AM)
    }

    fun saveAzkarFrequency(frequency: ShortAzkarFrequency) {
        val json = gson.toJson(frequency)
        prefs.edit().putString(PREFS_KEY_FREQUENCY, json).apply()
    }

    fun getAzkarFrequency(): ShortAzkarFrequency {
        val json = prefs.getString(PREFS_KEY_FREQUENCY, null)
        return json?.let { gson.fromJson(it, ShortAzkarFrequency::class.java) }
            ?: DEFAULT_AZKAR_FREQUENCY
    }

    fun setIsReminderOn(isOnScreen: Boolean) {
        prefs.edit()
            .putBoolean(PREFS_KEY_IS_REMINDER_ON, isOnScreen)
            .apply()
    }

    fun isReminderOnScreen(): Boolean {
        return prefs.getBoolean(PREFS_KEY_IS_REMINDER_ON, false)
    }

    fun getReminderRetriesCount(): Int {
        return prefs.getInt(PREFS_REMINDER_RETRIES_COUNT_LIMIT, 0)
    }

    fun setReminderRetriesCount(retries: Int) {
        prefs.edit()
            .putInt(PREFS_REMINDER_RETRIES_COUNT_LIMIT, retries)
            .apply()
    }
}
