/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.devrel.android.fitactions.tracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.devrel.android.fitactions.FitMainActivity
import com.devrel.android.fitactions.R
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository

/**
 * Foreground Android Service that starts an activity and keep tracks of the status showing
 * a notification.
 */
class FitTrackingService : Service() {

    private companion object {
        private const val ONGOING_NOTIFICATION_ID = 999

        private const val CHANNEL_ID = "TrackingChannel"
    }

    private val fitRepository: FitRepository by lazy {
        FitRepository.getInstance(this)
    }

    /**
     * Create a notification builder that will be used to create and update the stats notification
     */
    private val notificationBuilder: NotificationCompat.Builder by lazy {
        val pendingIntent = Intent(this, FitMainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getText(R.string.tracking_notification_title))
            .setSmallIcon(R.drawable.ic_run)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
    }

    /**
     * Observer that will update the notification with the ongoing activity status.
     */
    private val trackingObserver: Observer<FitActivity> = Observer { fitActivity ->
        fitActivity?.let {
            val km = String.format("%.2f", it.distanceMeters / 1000)
            val notification = notificationBuilder
                .setContentText(getString(R.string.stat_distance, km))
                .build()
            NotificationManagerCompat.from(this).notify(ONGOING_NOTIFICATION_ID, notification)
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(ONGOING_NOTIFICATION_ID, notificationBuilder.build())

        // Start a new activity and attach the observer
        fitRepository.startActivity()
        fitRepository.getOnGoingActivity().observeForever(trackingObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the ongoing activity and detach the observer
        fitRepository.stopActivity()
        fitRepository.getOnGoingActivity().removeObserver(trackingObserver)
    }

    /**
     * Creates a Notification channel needed for new version of Android
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
