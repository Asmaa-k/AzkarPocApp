package com.asmaa.khb.azkarpocapp.presentation

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.databinding.ActivityTasbihBinding
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.util.Constants
import com.asmaa.khb.azkarpocapp.presentation.viewmodel.AzkarViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AzkarActivity : ComponentActivity() {
    private val viewModel: AzkarViewModel by viewModels()
    private lateinit var binding: ActivityTasbihBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasbihBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {
        setupShortAzkarRadioButton()
        setupMorningAzkarButton()
        setupEveningAzkarButton()
    }

    private fun setupEveningAzkarButton() {
        binding.btnEvening.setOnClickListener {
            showTimePicker(viewModel.getEveningTimeFormat()) { time ->
                viewModel.saveAndReScheduleEveningAzkarReminder(time)
            }
        }
    }

    private fun setupMorningAzkarButton() {
        binding.btnMorning.setOnClickListener {
            showTimePicker(viewModel.getMorningTimeFormat()) { time ->
                viewModel.saveAndReScheduleMorningAzkarReminder(time)
            }
        }
    }

    private fun setupShortAzkarRadioButton() {
        //setup radios views
        when (viewModel.getCurrentFrequency()) {
            ShortAzkarFrequency.LOW -> binding.radioGroup.check(R.id.optLow)
            ShortAzkarFrequency.MID -> binding.radioGroup.check(R.id.optMid)
            ShortAzkarFrequency.HIGH -> binding.radioGroup.check(R.id.optHigh)
        }

        //setup click
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.optLow -> {
                    viewModel.saveAndReScheduleShortAzkar(ShortAzkarFrequency.LOW)
                }

                R.id.optMid -> {
                    viewModel.saveAndReScheduleShortAzkar(ShortAzkarFrequency.MID)
                }

                R.id.optHigh -> {
                    viewModel.saveAndReScheduleShortAzkar(ShortAzkarFrequency.HIGH)
                }
            }
        }
    }

    private fun showTimePicker(
        previousSelectedTime: ReminderAzkarTimeFormat,
        onTimeSelected: (ReminderAzkarTimeFormat) -> Unit
    ) {
        val hour = previousSelectedTime.hourOfDay
        val minute = previousSelectedTime.minute
        val isAm = previousSelectedTime.period == Constants.CONST_AM

        val hourIn24 = when {
            hour == 12 && isAm -> 0
            hour == 12 && !isAm -> 12
            isAm -> hour
            else -> hour + 12
        }

        val timePicker = TimePickerDialog(
            /* context = */ this,
            /* listener = */
            { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }

                val amPm =
                    if (calendar.get(Calendar.AM_PM) == Calendar.AM) Constants.CONST_AM else Constants.CONST_PM
                val formattedHour = calendar.get(Calendar.HOUR)
                val hourToDisplay = if (formattedHour == 0) 12 else formattedHour

                val timeFormat = ReminderAzkarTimeFormat(
                    hourOfDay = hourToDisplay, minute = selectedMinute, period = amPm
                )

                onTimeSelected(timeFormat)
            },
            /* hourOfDay = */ hourIn24, /* minute = */ minute, /* is24HourView = */ false,
        )

        timePicker.show()
    }
}