package com.asmaa.khb.azkarpocapp.domain.util

import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency

object Constants {
    //default values
    const val DEFAULT_MORNING_REMINDER = 5
    const val DEFAULT_EVENING_REMINDER = 6
    val DEFAULT_AZKAR_FREQUENCY = ShortAzkarFrequency.HIGH
    val STATIC_AZKAR_LIST = listOf(
        "لَا حَوْل وَلَا قُوَّة إِلَّا بِاَلله",
        "اللَّهُ أَكْبَرُ",
        "لَا إِلَهَ إِلَّا اللَّهُ",
        "الْحَمْدُ لِلَّهِ",
        "سُبْحَانَ اللَّه"
    )

    //prefs key
    const val PREFS_KEY_NAME = "azkar_prefs"
    const val PREFS_KEY_EVENING_REMINDER_TIME = "key_evening_reminder_time"
    const val PREFS_KEY_MORNING_REMINDER_TIME = "key_morning_reminder_time"
    const val PREFS_KEY_FREQUENCY = "azkar_frequency"
    const val PREFS_KEY_IS_REMINDER_ON = "is_reminder_on"
}
