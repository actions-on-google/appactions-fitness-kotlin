/*
 * Copyright 2022 Google LLC
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

package com.devrel.android.fitactions.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.text.format.DateFormat
import android.widget.RemoteViews
import com.devrel.android.fitactions.R
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository
import com.devrel.android.fitactions.observeOnce
import java.util.*
import java.util.concurrent.TimeUnit
// Add imports


/**
 * Class that defines a Stats Widget which provides data on the last activity performed
 * or with a BII it provides the last activity performed of requested activity type
 */
class StatsWidget(
    private val context: Context,
    private val appWidgetManager: AppWidgetManager,
    private val appWidgetId: Int,
    layout: Int,
) {
    private val views = RemoteViews(context.packageName, layout)
    private val repository = FitRepository.getInstance(context)
    // Add private variables


    // Add init



    fun updateAppWidget() {
        observeAndUpdateLastExercise()
    }



    /**
     * Sets title, duration and distance data to widget
     */
    private fun setDataToWidget(
        exerciseType: String,
        distanceInKm: Float,
        durationInMin: Long
    ) {
        views.setTextViewText(
            R.id.activityType,
            context.getString(R.string.widget_activity_type, exerciseType)
        )
        views.setTextViewText(
            R.id.appwidgetDuration,
            context.getString(R.string.widget_distance, distanceInKm)
        )
        views.setTextViewText(
            R.id.appwidgetDistance,
            context.getString(R.string.widget_duration, durationInMin)
        )
    }

    // Add setTts

    /**
     * Formats and sets activity data to Widget
     */
    private fun formatDataAndSetWidget(
        activityStat: FitActivity,
    ) {
        // formats date of activity
        val datePattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEdMMMM")
        val formattedDate = DateFormat.format(datePattern, activityStat.date)


        // formats duration, distance and exercise name for widget
        val durationInMin = TimeUnit.MILLISECONDS.toMinutes(activityStat.durationMs)
        val distanceInKm = (activityStat.distanceMeters / 1000).toFloat()
        val activityExerciseType = activityStat.type.toString()
        val activityExerciseTypeFormatted = activityExerciseType.lowercase()


        setDataToWidget(activityExerciseType, distanceInKm, durationInMin)

        // Add conditional for hasBii for widget with data
    }

    /**
     * Formats and sets no activity data to Widget
     */
    private fun setNoActivityDataWidget() {
        val appwidgetTypeTitleText = context.getString((R.string.widget_no_data))
        val distanceInKm = 0F
        val durationInMin = 0L

        setDataToWidget(appwidgetTypeTitleText, distanceInKm, durationInMin)

        // Add conditional for hasBii for widget without data
    }

    /**
     * Instruct the widget manager to update the widget
     */
    private fun updateWidget() {
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // Add observeAndUpdateRequestedExercise function


    /**
     * Create and observe the last activity LiveData.
     */
    private fun observeAndUpdateLastExercise() {
        val activityData = repository.getLastActivities(1)

        activityData.observeOnce { activitiesStat ->
            if (activitiesStat.isNotEmpty()) {
                formatDataAndSetWidget(activitiesStat[0])
                updateWidget()
            } else {
                setNoActivityDataWidget()
                updateWidget()
            }
        }
    }

}

