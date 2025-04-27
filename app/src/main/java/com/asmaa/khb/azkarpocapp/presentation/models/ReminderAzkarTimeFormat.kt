package com.asmaa.khb.azkarpocapp.presentation.models

data class ReminderAzkarTimeFormat(
    val hourOfDay: Int,
    val minute: Int,
    val period: String // "AM" or "PM"
)
