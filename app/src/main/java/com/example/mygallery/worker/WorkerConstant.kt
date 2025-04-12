package com.example.mygallery.worker



// Notification Channel constants
// Name of Notification Channel for verbose notifications of background work

val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notification whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

// keys
const val KEY_MEDIA_URI = "KEY_MEDIA_URI"
const val DELAY_TIME_MILLIS: Long = 3000
