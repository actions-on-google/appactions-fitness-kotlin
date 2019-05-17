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

package com.devrel.android.fitactions.slices

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.slice.Slice
import androidx.slice.builders.*
import com.devrel.android.fitactions.DeepLink
import com.devrel.android.fitactions.R
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository
import java.util.*

/**
 * Slice that loads the last user activities and stats, and displays them once loaded.
 *
 * This class shows how to deal with Slices that needs to load content asynchronously.
 */
class FitStatsSlice(
    context: Context,
    sliceUri: Uri,
    fitRepo: FitRepository
) : FitSlice(context, sliceUri) {

    /**
     * Get the activity type from the uri and map it to our enum types.
     */
    private val activityType = FitActivity.Type.find(
        sliceUri.getQueryParameter(DeepLink.Params.ACTIVITY_TYPE).orEmpty()
    )

    /**
     * Observer that will refresh the slice once data is available
     */
    private val observer = Observer<List<FitActivity>> {
        if (it != null) {
            refresh()
        }
    }

    /**
     * Create and observe the last activities LiveData.
     */
    private val activitiesLiveData = fitRepo.getLastActivities(count = 5, type = activityType).apply {
        // The ContentProvider is called in a different thread and liveData
        // only works with MainThread
        handler.post {
            observeForever(observer)
        }
    }

    override fun getSlice(): Slice {
        // If the data is still loading, return a loading slice, otherwise create the stats slice.
        val data = activitiesLiveData.value
        return if (data != null) {
            // data is available, remove attached observer
            handler.post {
                activitiesLiveData.removeObserver(observer)
            }

            createStatsSlice(data)
        } else {
            createLoadingSlice()
        }
    }

    /**
     * Simple loading Slice while the DB is still loading the last activities.
     */
    private fun createLoadingSlice(): Slice = list(context, sliceUri, ListBuilder.INFINITY) {
        header {
            setTitle(
                context.getString(R.string.slice_stats_loading, context.getString(activityType.nameId)),
                /* isLoading */ true
            )
        }
    }

    /**
     * Create the stats slices showing the data provided by the DB.
     */
    private fun createStatsSlice(data: List<FitActivity>): Slice {
        // Create the list content
        return list(context, sliceUri, ListBuilder.INFINITY) {
            // Add the header of the slice
            header {
                title = context.getString(R.string.slice_stats_title, context.getString(activityType.nameId))
                subtitle = if (data.isEmpty()) {
                    context.getString(R.string.slice_stats_subtitle_no_data)
                } else {
                    context.getString(R.string.slice_stats_subtitle)
                }
                // Defines the primary action when slice is clicked
                primaryAction = createActivityAction()
            }
            // Add a grid row to handle multiple cells
            gridRow {
                data.forEach { fitActivity ->
                    // For each activity add a cell with the fit data
                    cell {
                        setFitActivity(fitActivity)
                    }
                }
            }
        }
    }

    /**
     * Given a Slice cell, setup the content to display the given FitActivity.
     */
    private fun CellBuilderDsl.setFitActivity(value: FitActivity) {
        val distanceInKm = String.format("%.2f", value.distanceMeters / 1000)
        val distance = context.getString(R.string.slice_stats_distance, distanceInKm)
        addText(distance)

        val calendar = Calendar.getInstance().apply { timeInMillis = value.date }
        addTitleText(
            calendar.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault()
            )
        )
    }
}
