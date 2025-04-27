package com.asmaa.khb.azkarpocapp.presentation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.SPLASH_DURATION_IN_SEC
import com.asmaa.khb.azkarpocapp.presentation.util.registerExactAlarmPermissionLauncher
import com.asmaa.khb.azkarpocapp.presentation.util.registerOverlayPermissionLauncher
import com.asmaa.khb.azkarpocapp.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    private val overlayServicePermissionLauncher by lazy {
        registerOverlayPermissionLauncher(
            onPermissionGranted = (::requestExactAlarmPermissionIfNeededAndStartReceivers),
            onPermissionDenied = (::startAzkarActivity)
        )
    }

    private val exactAlarmPermissionLauncher by lazy {
        registerExactAlarmPermissionLauncher(
            onPermissionGranted = (::startSchedulingAzkarReceivers),
            onPermissionDenied = (::startAzkarActivity)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestOverlayPermissionIfNeeded()
    }

    private fun requestOverlayPermissionIfNeeded() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
            overlayServicePermissionLauncher.launch(intent)
        } else {
            requestExactAlarmPermissionIfNeededAndStartReceivers()
        }
    }

    private fun requestExactAlarmPermissionIfNeededAndStartReceivers() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                startSchedulingAzkarReceivers()
            }

            alarmManager.canScheduleExactAlarms() -> {
                startSchedulingAzkarReceivers()
            }

            else -> {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                exactAlarmPermissionLauncher.launch(intent)
            }
        }
    }

    private fun startSchedulingAzkarReceivers() {
        lifecycleScope.launch {
            viewModel.insertInitialAzkarIfNeeded()
            viewModel.scheduleShortAzkar()
            viewModel.scheduleEveningMorningReminderAzkar()
            startAzkarActivity()
        }
    }

    private fun startAzkarActivity() {
        lifecycleScope.launch {
            delay(TimeUnit.SECONDS.toMillis(SPLASH_DURATION_IN_SEC))
            startActivity(Intent(this@SplashActivity, AzkarActivity::class.java))
            finish()
        }
    }
}
