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
import android.view.animation.DecelerateInterpolator
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.asmaa.khb.azkarpocapp.R
import com.asmaa.khb.azkarpocapp.domain.preferences.AzkarPreferences
import com.asmaa.khb.azkarpocapp.presentation.SplashActivity
import com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service.AzkarViewFactory.NO_IMAGE
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.AUTO_SERVICE_STOPPING_PERIOD_IN_SEC
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_IMAGE_RES
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_SHOULD_STOP_SERVICE
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_TEXT_CONTENT
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_VIEW_TYPE
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.FOREGROUND_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AzkarWidgetService : Service(), LifecycleOwner {
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private lateinit var azkarPreferences: AzkarPreferences
    private lateinit var notificationManager: NotificationManager
    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var viewType: String

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        azkarPreferences = AzkarPreferences(applicationContext)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    companion object {
        fun showAzkar(
            context: Context, content: String, imgRes: Int = -1, viewType: String
        ) {
            val intent = Intent(context, AzkarWidgetService::class.java).apply {
                putExtra(EXTRA_TEXT_CONTENT, content)
                putExtra(EXTRA_IMAGE_RES, imgRes)
                putExtra(EXTRA_VIEW_TYPE, viewType)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        intent?.let {
            val textContent = it.getStringExtra(EXTRA_TEXT_CONTENT).orEmpty()
            val imgContent = it.getIntExtra(EXTRA_IMAGE_RES, NO_IMAGE)
            viewType = it.getStringExtra(EXTRA_VIEW_TYPE).orEmpty()
            showAzkarView(textContent, imgContent, viewType)
        }

        // Start as foreground service (required for overlay permissions)
        startForegroundServiceWithNotification()

        return START_STICKY
    }

    private fun showAzkarView(textContent: String, imgContent: Int, viewType: String) {
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
            viewType = viewType,
            lifecycleOwner = this,
            onClose = {
                setDismissReminderManually()
                stopSelf()
            },
            onViewClick = {
                setDismissReminderManually()
                startActivity(getAppIntent())
                stopSelf()
            }
        ).apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        windowManager.addView(overlayView, layoutParams)

        stopTheServiceAutomaticallyInCaseNotReminder(azkarPreferences.isReminderOnScreen())
    }

    private fun getAppIntent(): Intent {
        return Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_SHOULD_STOP_SERVICE, true)
        }
    }

    private fun reversReminderFlagsIfNeeded() {
        if (azkarPreferences.isReminderOnScreen()) {
            azkarPreferences.setIsReminderOn(false)
        }
    }

    private fun setDismissReminderManually() {
        if (azkarPreferences.isReminderOnScreen()) {
            azkarPreferences.onManuallyEveningReminderDismissed(viewType == ACTION_SHOW_EVENING_AZKAR)
            azkarPreferences.onManuallyMorningReminderDismissed(viewType == ACTION_SHOW_MORNING_AZKAR)
        }
    }

    private fun startForegroundServiceWithNotification() {
        val notification = NotificationCompat.Builder(this, "overlay_channel")
            .setContentTitle("Azkar Service Running")
            .setContentText("Showing your azkar")
            .setContentIntent(createPendingIntent())
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
        if (!azkarPreferences.isReminderOnScreen()) return
        val notification = NotificationCompat.Builder(this, "overlay_channel")
            .setContentTitle("Azkar Service Running")
            .setContentText("Tap to open azkar")
            .setSmallIcon(R.drawable.ic_exclamation_mark)
            .setAutoCancel(false)
            .setContentIntent(createPendingIntent())
            .build()

        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "overlay_channel",
                "Sticky Azkar",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            getAppIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        showPersistentNotification()
        reversReminderFlagsIfNeeded()
        removeExistingView()
        super.onDestroy()
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}
