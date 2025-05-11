package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.service

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.asmaa.khb.azkarpocapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AzkarViewFactory {
    const val NO_IMAGE = -1
    private const val HIGHLIGHT_COLOR = Color.RED
    private const val ANIMATION_DELAY = 300L

    fun createAzkarView(
        context: Context,
        textContent: String,
        @DrawableRes imgContent: Int = NO_IMAGE,
        isReminderViewType: Boolean,
        lifecycleOwner: LifecycleOwner,
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
                animateWordHighlights(lifecycleOwner.lifecycleScope)
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
        textContent: String, onClose: () -> Unit,
    ) {
        setOnClickListener { onClose() }
        findViewById<TextView>(R.id.azkar_text).text = textContent
    }

    private fun View.animateWordHighlights(scope: CoroutineScope) {
        val textView = findViewById<TextView>(R.id.azkar_text)
        scope.launch {
            textView?.let {
                doTextHighlighting(textView)
                restTextColor(textView)
            }
        }
    }

    private suspend fun doTextHighlighting(textView: TextView) {
        val originalTextColor = textView.currentTextColor
        val text = textView.text.toString()
        val words = text.split(" ").filter { it.isNotBlank() }

        if (words.isEmpty()) return

        words.foldIndexed(0) { index, currentPos, word ->
            val start = text.indexOf(word, currentPos)
            val end = start + word.length

            delay(index + ANIMATION_DELAY)

            withContext(Dispatchers.Main) {
                val spannable = SpannableString(text).apply {
                    setSpan(
                        ForegroundColorSpan(originalTextColor),
                        0,
                        text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    setSpan(
                        ForegroundColorSpan(HIGHLIGHT_COLOR),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                textView.text = spannable
            }
            end
        }
    }

    private suspend fun restTextColor(textView: TextView) {
        withContext(Dispatchers.Main) {
            delay(ANIMATION_DELAY)
            val resetSpannable = SpannableString(textView.text).apply {
                setSpan(
                    ForegroundColorSpan(textView.currentTextColor),
                    0,
                    textView.text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.text = resetSpannable
        }
    }
}
