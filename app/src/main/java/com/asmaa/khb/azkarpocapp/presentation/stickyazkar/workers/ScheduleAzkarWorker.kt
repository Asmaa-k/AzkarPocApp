package com.asmaa.khb.azkarpocapp.presentation.stickyazkar.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.asmaa.khb.azkarpocapp.domain.di.AzkarRepositoryEntryPoint
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_EVENING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_MORNING_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.ACTION_SHOW_SHORT_AZKAR
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_SHOW_EVENING_AZKAR_SHORTER_TIME_SCHEDULER
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.EXTRA_SHOW_MORNING_AZKAR_SHORTER_TIME_SCHEDULER
import com.asmaa.khb.azkarpocapp.presentation.util.Constants.WORKER_AZKAR_TYPE
import dagger.hilt.android.EntryPointAccessors

class ScheduleAzkarWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val type = inputData.getString(WORKER_AZKAR_TYPE) ?: return Result.failure()

        val repository = EntryPointAccessors.fromApplication(
            applicationContext, AzkarRepositoryEntryPoint::class.java
        ).azkarRepository()

        when (type) {
            ACTION_SHOW_SHORT_AZKAR -> {
                repository.scheduleShortAzkarReceiver()
            }

            ACTION_SHOW_MORNING_AZKAR -> {
                repository.scheduleMorningAzkarReceiver()
            }

            ACTION_SHOW_EVENING_AZKAR -> {
                repository.scheduleEveningAzkarReceiver()
            }

            EXTRA_SHOW_MORNING_AZKAR_SHORTER_TIME_SCHEDULER -> {
                repository.reScheduleMorningAzkarReceiverWithinShortTime()
            }

            EXTRA_SHOW_EVENING_AZKAR_SHORTER_TIME_SCHEDULER -> {
                repository.reScheduleEveningAzkarReceiverWithinShortTime()
            }
        }

        return Result.success()
    }
}

fun scheduleEveningAzkarScheduleWorker(context: Context) {
    val input = workDataOf(WORKER_AZKAR_TYPE to ACTION_SHOW_EVENING_AZKAR)

    val mWorker = OneTimeWorkRequestBuilder<ScheduleAzkarWorker>()
        .setInputData(input)
        .build()

    WorkManager.getInstance(context).enqueue(mWorker)
}

fun scheduleMorningAzkarScheduleWorker(context: Context) {
    val input = workDataOf(WORKER_AZKAR_TYPE to ACTION_SHOW_MORNING_AZKAR)

    val mWorker = OneTimeWorkRequestBuilder<ScheduleAzkarWorker>()
        .setInputData(input)
        .build()

    WorkManager.getInstance(context).enqueue(mWorker)
}

fun reScheduleMorningAzkarScheduleWorkerForShorterTime(context: Context) {
    val input = workDataOf(WORKER_AZKAR_TYPE to EXTRA_SHOW_MORNING_AZKAR_SHORTER_TIME_SCHEDULER)

    val mWorker = OneTimeWorkRequestBuilder<ScheduleAzkarWorker>()
        .setInputData(input)
        .build()

    WorkManager.getInstance(context).enqueue(mWorker)
}

fun reScheduleEveningAzkarScheduleWorkerForShorterTime(context: Context) {
    val input = workDataOf(WORKER_AZKAR_TYPE to EXTRA_SHOW_EVENING_AZKAR_SHORTER_TIME_SCHEDULER)

    val mWorker = OneTimeWorkRequestBuilder<ScheduleAzkarWorker>()
        .setInputData(input)
        .build()

    WorkManager.getInstance(context).enqueue(mWorker)
}


fun scheduleShortAzkarScheduleWorker(context: Context) {
    val input = workDataOf(WORKER_AZKAR_TYPE to ACTION_SHOW_SHORT_AZKAR)

    val mWorker = OneTimeWorkRequestBuilder<ScheduleAzkarWorker>()
        .setInputData(input)
        .build()

    WorkManager.getInstance(context).enqueue(mWorker)
}