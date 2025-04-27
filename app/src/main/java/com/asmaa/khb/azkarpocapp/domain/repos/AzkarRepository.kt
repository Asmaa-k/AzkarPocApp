package com.asmaa.khb.azkarpocapp.domain.repos

import com.asmaa.khb.azkarpocapp.data.databases.AzkarDao
import com.asmaa.khb.azkarpocapp.data.databases.AzkarEntity
import com.asmaa.khb.azkarpocapp.domain.preferences.AzkarPreferences
import com.asmaa.khb.azkarpocapp.domain.util.Constants.STATIC_AZKAR_LIST
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.schedulers.MorningEveningAzkarReminderScheduler
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.schedulers.ShortAzkarScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AzkarRepository @Inject constructor(
    private val dao: AzkarDao,
    private val azkarPreferences: AzkarPreferences,
    private val morningEveningAzkarReminderScheduler: MorningEveningAzkarReminderScheduler,
    private val shortScheduler: ShortAzkarScheduler,
    private val appScope: CoroutineScope
) {

    fun insertInitialAzkarIfNeeded() {
        appScope.launch {
            withContext(Dispatchers.IO) {
                val isEmpty = dao.getAllAzkar().first().isEmpty()
                if (isEmpty) {
                    STATIC_AZKAR_LIST.forEach {
                        dao.insert(
                            AzkarEntity(content = it)
                        )
                    }
                }
            }
        }
    }

    fun scheduleMorningAzkarReceiver() {
        appScope.launch {
            withContext(Dispatchers.IO) {
                morningEveningAzkarReminderScheduler.scheduleMorningAzkar(
                    azkarPreferences.getMorningTimeFormat(),
                )
            }
        }
    }

    fun scheduleEveningAzkarReceiver() {
        appScope.launch {
            withContext(Dispatchers.IO) {
                morningEveningAzkarReminderScheduler.scheduleEveningAzkar(
                    azkarPreferences.getEveningTimeFormat(),
                )
            }
        }
    }

    fun scheduleShortAzkarReceiver() {
        appScope.launch {
            withContext(Dispatchers.IO) {
                shortScheduler.scheduleShortAzkar(
                    azkarPreferences.getAzkarFrequency(),
                )
            }
        }
    }

    fun saveAndReScheduleEveningAzkarReminder(time: ReminderAzkarTimeFormat) {
        azkarPreferences.saveEveningTimeFormat(time)
        scheduleEveningAzkarReceiver()
    }

    fun saveAndReScheduleMorningAzkarReminder(time: ReminderAzkarTimeFormat) {
        azkarPreferences.saveMorningTimeFormat(time)
        scheduleMorningAzkarReceiver()
    }

    fun saveAndReScheduleShortAzkar(value: ShortAzkarFrequency) {
        azkarPreferences.saveAzkarFrequency(value)
        scheduleShortAzkarReceiver()
    }

    fun getCurrentFrequency(): ShortAzkarFrequency = azkarPreferences.getAzkarFrequency()

    fun getEveningTimeFormat(): ReminderAzkarTimeFormat = azkarPreferences.getEveningTimeFormat()

    fun getMorningTimeFormat(): ReminderAzkarTimeFormat = azkarPreferences.getMorningTimeFormat()

    suspend fun fetchRandomZker(): AzkarEntity? = dao.getRandomAzkar()
}