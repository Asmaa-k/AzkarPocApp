package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.domain.preferences.AzkarPreferences
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarViewFactory.NO_IMAGE
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.AUTO_SERVICE_STOPPING_PERIOD_IN_SEC
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.FOREGROUND_NOTIFICATION_ID
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.PERSISTENT_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AzkarWidgetService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private lateinit var azkarPreferences: AzkarPreferences
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        azkarPreferences = AzkarPreferences(applicationContext)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    companion object {
        fun showAzkar(context: Context, content: String, imgRes: Int = -1) {
            val intent = Intent(context, AzkarWidgetService::class.java).apply {
                putExtra(EXTRA_TEXT_CONTENT, content)
                putExtra(EXTRA_IMAGE_RES, imgRes)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val textContent = it.getStringExtra(EXTRA_TEXT_CONTENT).orEmpty()
            val imgContent = it.getIntExtra(EXTRA_IMAGE_RES, NO_IMAGE)
            showAzkarView(textContent, imgContent, azkarPreferences.isReminderOnScreen())
        }

        // Start as foreground service (required for overlay permissions)
        startForegroundServiceWithNotification()

        // Show separate persistent notification that will remain
        if (azkarPreferences.isReminderOnScreen()) showPersistentNotification()

        return START_STICKY
    }

    private fun showAzkarView(textContent: String, imgContent: Int, isReminder: Boolean) {
        removeExistingView()

        val layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.END
            format = PixelFormat.TRANSLUCENT
            x = 50
            y = 50
        }

        overlayView = AzkarViewFactory.createAzkarView(
            context = this,
            textContent = textContent,
            imgContent = imgContent,
            isReminderViewType = isReminder,
            onClose = {
                stopSelf()
                reversReminderIfNeeded()
            },
            onViewClick = {}
        )

        windowManager.addView(overlayView, layoutParams)

        stopTheServiceAutomaticallyInCaseNotReminder(isReminder)
    }

    private fun reversReminderIfNeeded() {
        if (azkarPreferences.isReminderOnScreen()) azkarPreferences.setIsReminderOn(false)
    }

    private fun startForegroundServiceWithNotification() {
        val notification = NotificationCompat.Builder(this, "overlay_channel")
            .setContentTitle("Azkar Service Running")
            .setContentText("Showing your azkar")
            .setSmallIcon(R.drawable.ic_exclamation_mark)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                FOREGROUND_NOTIFICATION_ID,
                notification,
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }
    }

    private fun showPersistentNotification() {
        val notification = NotificationCompat.Builder(this, "overlay_channel")
            .setContentTitle("Azkar Service Running")
            .setContentText("Tap to open azkar")
            .setSmallIcon(R.drawable.ic_exclamation_mark)
            .setAutoCancel(false)
            .setContentIntent(createPendingIntent())
            .build()

        notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "overlay_channel",
                "Sticky Azkar",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, AzkarWidgetService::class.java).apply {
            putExtra(EXTRA_TEXT_CONTENT, "Default Azkar")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun stopTheServiceAutomaticallyInCaseNotReminder(isReminder: Boolean) {
        if (!isReminder) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(TimeUnit.SECONDS.toMillis(AUTO_SERVICE_STOPPING_PERIOD_IN_SEC))
                removeExistingView()
                stopSelf()
            }
        }
    }

    private fun removeExistingView() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    override fun onDestroy() {
        removeExistingView()
        super.onDestroy()
    }
}
