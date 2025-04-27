package com.asmaa.khb.azkarpocapp.presentation.util

import android.app.AlarmManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

private const val TAG = "PermissionExtensions"

// Extension for overlay permission
fun ComponentActivity.registerOverlayPermissionLauncher(
    onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit
) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (Settings.canDrawOverlays(this)) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}


// Extension for exact alarm permission
fun ComponentActivity.registerExactAlarmPermissionLauncher(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) {
    val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager?.canScheduleExactAlarms() == true) {
        onPermissionGranted()
    } else {
        Log.d(TAG, "Exact alarms permission is not granted")
        onPermissionDenied()
    }
}
