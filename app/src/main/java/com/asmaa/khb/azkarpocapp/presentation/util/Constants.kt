package com.asmaa.khb.azkarpocapp.presentation.util

object Constants {
    //default values
    const val CONST_AM = "AM"
    const val CONST_PM = "PM"
    const val AUTO_SERVICE_STOPPING_PERIOD_IN_SEC = 10L
    const val SPLASH_DURATION_IN_SEC = 1L
    const val FOREGROUND_NOTIFICATION_ID = 1005
    const val REMINDER_RETRIES_COUNT_LIMIT = 1


    //scheduler name
    const val MORNING_REMINDER_ALARM_REQUEST_CODE = 1001
    const val EVENING_REMINDER_ALARM_REQUEST_CODE = 1002
    const val SHORT_AZKAR_ALARM_REQUEST_CODE = 1003
    const val ACTION_SHOW_MORNING_AZKAR = "action_show_morning_azkar"
    const val ACTION_SHOW_EVENING_AZKAR = "action_show_evening_azkar"
    const val ACTION_SHOW_SHORT_AZKAR = "action_show_short_azkar"
    const val WORKER_AZKAR_TYPE = "worker_azkar_type"

    //extras
    const val EXTRA_TEXT_CONTENT = "extra_text_content"
    const val EXTRA_IMAGE_RES = "extra_image_res_id"
    const val EXTRA_VIEW_TYPE = "extra_view_type"
    const val EXTRA_SHOULD_STOP_SERVICE = "extra_should_stop_service"
    const val EXTRA_SHOW_MORNING_AZKAR_SHORTER_TIME_SCHEDULER = "show_morning_azkar_shorter_time_scheduler"
    const val EXTRA_SHOW_EVENING_AZKAR_SHORTER_TIME_SCHEDULER = "show_evening_azkar_shorter_time_scheduler"
}
