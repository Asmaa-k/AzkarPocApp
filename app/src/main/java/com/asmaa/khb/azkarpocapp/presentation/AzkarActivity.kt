package com.asmaa.khb.azkarpocapp.presentation

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.databinding.ActivityTasbihBinding
import com.asmaa.khb.azkarpocapp.presentation.models.ReminderAzkarTimeFormat
import com.asmaa.khb.azkarpocapp.presentation.models.ShortAzkarFrequency
import com.asmaa.khb.azkarpocapp.presentation.util.Constants
import com.asmaa.khb.azkarpocapp.presentation.util.registerNotificationPermissionLauncher
import com.asmaa.khb.azkarpocapp.presentation.viewmodel.AzkarViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AzkarActivity : ComponentActivity() {
    private val viewModel: AzkarViewModel by viewModels()
    private lateinit var binding: ActivityTasbihBinding
    private val requestPermissionLauncher = registerNotificationPermissionLauncher(
        onPermissionGranted = { handleNotificationPermissionResult(true) },
        onPermissionDenied = { handleNotificationPermissionResult(false) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasbihBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
        showNotificationPermissionDialogIfNeeded()
    }

    private fun setupUi() {
        setupShortAzkarRadioButton()
        setupMorningAzkarButton()
        setupEveningAzkarButton()
        setupTestButton()
    }

    private fun setupTestButton() {
        binding.test.setOnClickListener {
            viewModel.startPopupService()
        }
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

    private fun showNotificationPermissionDialogIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog.Builder(this)
                .setTitle("Notification Permission")
                .setMessage("We need permission to show Azkar reminders.")
                .setPositiveButton("Allow") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton("No Thanks", null)
                .show()
        }
    }

    private fun handleNotificationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            // Permission granted, proceed with functionality that needs notifications
            Log.d(AzkarActivity::class.simpleName, "handleNotificationPermissionResult: isGranted")
        } else {
            // Permission denied, handle the case (maybe show a message or provide alternate behavior)
            Log.d(AzkarActivity::class.simpleName, "handleNotificationPermissionResult: isDenied")
        }
    }
}