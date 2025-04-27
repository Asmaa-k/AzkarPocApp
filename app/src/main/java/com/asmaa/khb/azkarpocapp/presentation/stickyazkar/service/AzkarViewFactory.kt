package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.asmaa.khb.azkarpocapp.R

object AzkarViewFactory {
    const val NO_IMAGE = -1

    fun createAzkarView(
        context: Context,
        textContent: String,
        @DrawableRes imgContent: Int = NO_IMAGE,
        isReminderViewType: Boolean,
        onViewClick: () -> Unit,
        onClose: () -> Unit,
    ): View {
        val inflater = LayoutInflater.from(context)
        return if (isReminderViewType) {
            inflater.inflate(R.layout.evening_morning_azkar_reminder_view, null).apply {
                setupMorningEveningAzkarReminderView(
                    textContent, imgContent, onViewClick, onClose
                )
            }
        } else {
            inflater.inflate(R.layout.normal_azkar_view, null).apply {
                setupShortAzkarView(
                    textContent, onClose
                )
            }
        }
    }

    private fun View.setupMorningEveningAzkarReminderView(
        textContent: String,
        @DrawableRes imgContent: Int = NO_IMAGE,
        onViewClick: () -> Unit,
        onClose: () -> Unit,
    ) {
        setOnClickListener { onViewClick() }
        findViewById<View>(R.id.close_button).setOnClickListener { onClose() }
        findViewById<TextView>(R.id.azkar_text).text = textContent
        if (imgContent != NO_IMAGE) {
            findViewById<ImageView>(R.id.img).setImageResource(imgContent)
        }
    }

    private fun View.setupShortAzkarView(
        textContent: String,
        onClose: () -> Unit,
    ) {
        setOnClickListener { onClose() }
        findViewById<TextView>(R.id.azkar_text).text = textContent
    }
}
