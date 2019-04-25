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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.list
import androidx.slice.builders.row
import com.devrel.android.fitactions.FitMainActivity
import com.devrel.android.fitactions.R

/**
 * Base class that defines a Slice for the app.
 *
 * Every slice implementation should extend this class and implement getSlice method.
 */
abstract class FitSlice(val context: Context, val sliceUri: Uri) {

    /**
     * @return the specific slice implementation to be used by SliceProvider
     */
    abstract fun getSlice(): Slice

    /**
     * Call refresh to notify the SliceProvider to load again.
     */
    protected fun refresh() {
        context.contentResolver.notifyChange(sliceUri, null)
    }

    /**
     * Utility method to create a SliceAction that launches the main activity.
     */
    protected fun createActivityAction(): SliceAction {
        val intent = Intent(context, FitMainActivity::class.java)
        return SliceAction.create(
            PendingIntent.getActivity(context, 0, intent, 0),
            IconCompat.createWithResource(context, R.mipmap.ic_launcher),
            ListBuilder.SMALL_IMAGE,
            context.getString(R.string.slice_enter_app_hint)
        )
    }

    /**
     * Default implementation of FitSlice when the uri could not be handled.
     */
    class Unknown(context: Context, sliceUri: Uri) : FitSlice(context, sliceUri) {

        override fun getSlice(): Slice = list(context, sliceUri, ListBuilder.INFINITY) {
            // Adds a row to the slice
            row {
                // Set the title of the row
                title = context.getString(R.string.slice_uri_not_found)
                // Defines the action when slice is clicked.
                primaryAction = createActivityAction()
            }

            // Mark the slice as error type slice.
            setIsError(true)
        }
    }
}