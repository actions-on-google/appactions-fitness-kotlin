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

package com.devrel.android.fitactions.model

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Simple repository to retrieve fit activities and start/stop new ones.
 *
 * It uses a Local DB to store/read FitActivities.
 */
class FitRepository(private val fitDb: FitDatabase, private val ioExecutor: Executor) {

    companion object {

        @Volatile
        private var instance: FitRepository? = null

        fun getInstance(context: Context): FitRepository {
            return instance ?: synchronized(this) {
                instance ?: FitRepository(
                    fitDb = FitDatabase.getInstance(context),
                    ioExecutor = Executors.newSingleThreadExecutor()
                ).also {
                    instance = it
                }
            }
        }
    }

    /**
     * LiveData containing the active tracker or null if none.
     */
    private val currentTracker: MutableLiveData<Tracker?> by lazy {
        MutableLiveData<Tracker?>().apply { value = null }
    }
    /**
     * Keep the transformation as variable to avoid creating a new one
     * each time getOnGoingActivity is called.
     */
    private val _onGoingActivity: LiveData<FitActivity> by lazy {
        Transformations.switchMap(currentTracker) {
            it ?: MutableLiveData<FitActivity>().apply { value = null }
        }
    }

    /**
     * Get the last activities
     *
     * @param count maximum number of activities to return
     * @return a live data with the last FitActivity
     */
    fun getLastActivities(count: Int, type: FitActivity.Type? = null): LiveData<List<FitActivity>> {
        val dao = fitDb.fitActivityDao()
        return type?.run { dao.getAllOfType(this, count) } ?: dao.getAll(count)
    }

    /**
     * Get the current users stats
     *
     * @return a live data with the latest FitStats
     */
    fun getStats(): LiveData<FitStats> {
        return fitDb.fitActivityDao().getStats()
    }

    /**
     * Get the on going activity
     *
     * @return a live data that tracks the ongoing activity if any.
     */
    fun getOnGoingActivity(): LiveData<FitActivity> = _onGoingActivity

    /**
     * Start a new activity.
     *
     * This method will stop any previous activity and create a new one.
     *
     * @see getOnGoingActivity
     * @see stopActivity
     */
    fun startActivity() {
        stopActivity()
        currentTracker.value = Tracker()
    }

    /**
     * Stop the ongoing activity if any and store the result.
     *
     * Note: the storing will be performed async.
     */
    fun stopActivity() {
        currentTracker.value?.let { tracker ->
            currentTracker.value = null
            ioExecutor.execute {
                tracker.stop()
                fitDb.fitActivityDao().insert(tracker.value)
            }
        }
    }

    /**
     * Internal class to track a FitActivity.
     *
     * It will automatically start tracking the duration and distance on creation, updating the
     * stats and notifying observers.
     */
    private class Tracker : MutableLiveData<FitActivity>() {

        private var isRunning = true
        private val handler = Handler()
        private val runnable = object : Runnable {

            /**
             * Method that will run every second while isRunning is true,
             * updating the FitActivity and notifying the observers of the LiveData.
             */
            override fun run() {
                value = value.copy(
                    durationMs = System.currentTimeMillis() - value.date,
                    distanceMeters = value.distanceMeters + 10 // TODO get distance
                )
                if (isRunning) {
                    handler.postDelayed(this, 1000)
                }
            }

        }

        init {
            // Create FitActivity
            value = FitActivity(
                id = UUID.randomUUID().toString(),
                date = System.currentTimeMillis(),
                type = FitActivity.Type.RUNNING,
                distanceMeters = 0.0,
                durationMs = 0
            )

            // Schedule recurrent task
            handler.postDelayed(runnable, 1000)
        }

        override fun getValue(): FitActivity {
            return checkNotNull(super.getValue())
        }

        /**
         * Stop tracking activity.
         */
        fun stop() {
            isRunning = false
            handler.removeCallbacks(runnable)
        }
    }
}
