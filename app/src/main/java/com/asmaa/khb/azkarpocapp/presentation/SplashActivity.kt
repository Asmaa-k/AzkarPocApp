package com.asmaa.khb.azkarpocapp.presentation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarWidgetService
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_SHOULD_STOP_SERVICE
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
            onPermissionDenied = {
                lifecycleScope.launch {
                    startAzkarActivity()
                }
            }
        )
    }

    private val exactAlarmPermissionLauncher by lazy {
        registerExactAlarmPermissionLauncher(
            onPermissionGranted = (::startSchedulingAzkarReceivers),
            onPermissionDenied = {
                lifecycleScope.launch {
                    startAzkarActivity()
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        stopRunningService()
        requestOverlayPermissionIfNeeded()
    }

    private fun stopRunningService() {
        if (intent?.getBooleanExtra(EXTRA_SHOULD_STOP_SERVICE, false) == true) {
            stopAzkarService()
        }
    }

    private fun stopAzkarService() {
        val serviceIntent = Intent(this, AzkarWidgetService::class.java)
        stopService(serviceIntent)
    }

    private fun requestOverlayPermissionIfNeeded() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri()
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
            viewModel.scheduleShortAzkar(this@SplashActivity)
            viewModel.scheduleEveningMorningReminderAzkar(this@SplashActivity)
            startAzkarActivity()
        }
    }

    private suspend fun startAzkarActivity() {
        delay(TimeUnit.SECONDS.toMillis(SPLASH_DURATION_IN_SEC))
        startActivity(Intent(this@SplashActivity, AzkarActivity::class.java))
        finish()
    }
}
