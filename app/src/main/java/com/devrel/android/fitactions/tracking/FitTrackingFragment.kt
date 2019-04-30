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

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devrel.android.fitactions.R
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository
import kotlinx.android.synthetic.main.fit_tracking_fragment.*
import java.util.concurrent.TimeUnit

/**
 * Fragment that handles the starting of an activity and tracks the status.
 *
 * When the fragments starts, it will start a countdown and launch the foreground service
 * that will keep track of the status.
 *
 * The view will observe the status and update its content.
 */
class FitTrackingFragment : Fragment() {

    companion object {
        /**
         * Parameter used when creating the fragment to add the type of activity.
         */
        const val PARAM_TYPE = "type"
    }

    lateinit var actionsCallback: FitTrackingActions

    private val fitRepository: FitRepository by lazy {
        FitRepository.getInstance(requireContext())
    }
    private val fitServiceIntent: Intent by lazy {
        Intent(requireContext(), FitTrackingService::class.java)
    }

    private var countDownMs = TimeUnit.SECONDS.toMillis(5)

    private val countDownTimer = object : CountDownTimer(countDownMs, 1000) {

        override fun onFinish() {
            // Countdown finished, start tracking service
            countDownMs = 0
            startTrackingService()
        }

        override fun onTick(millisUntilFinished: Long) {
            // Keep track of the remaining count
            countDownMs = millisUntilFinished

            val secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString()
            startActivityCountDown.text = secondsLeft
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fit_tracking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startActivityButton.setOnClickListener {
            // Handle the two states of the activity button
            // If activity started, stop it and notify activity.
            // Otherwise start the tracking service.
            val fitActivity = fitRepository.getOnGoingActivity().value
            if (fitActivity == null) {
                startTrackingService()
            } else {
                requireActivity().stopService(fitServiceIntent)
                actionsCallback.onActivityStopped(fitActivity.id)
            }
        }

        // Observe the ongoing activity and update the view accordingly.
        fitRepository.getOnGoingActivity().observe(this, Observer {
            if (it == null) {
                // When no ongoing activity only start the countdown if we haven't already.
                if (countDownMs > 0) {
                    countDownTimer.start()
                    onCountDown()
                }
            } else {
                onTracking(it)
            }
        })
    }

    override fun onDestroyView() {
        countDownTimer.cancel()
        super.onDestroyView()
    }

    /**
     * Stop the countdown if running, and start a foreground service
     */
    private fun startTrackingService() {
        countDownMs = 0
        countDownTimer.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(fitServiceIntent)
        } else {
            requireActivity().startService(fitServiceIntent)
        }
    }

    /**
     * Update the count down view
     */
    private fun onCountDown() {
        val type = arguments?.getSerializable(PARAM_TYPE) as? FitActivity.Type
            ?: FitActivity.Type.UNKNOWN
        startActivityTitle.text = getString(R.string.start_activity_title, type.name.toLowerCase())
        startActivityCountDown.text = TimeUnit.MILLISECONDS.toSeconds(countDownMs).toString()
        startActivityButton.isSelected = false
    }

    /**
     * Update the tracking view
     */
    private fun onTracking(activity: FitActivity) {
        startActivityTitle.setText(R.string.tracking_notification_title)
        startActivityCountDown.text = getString(
            R.string.stats_tracking_distance,
            activity.distanceMeters.toInt()
        )
        startActivityButton.isSelected = true
    }

    interface FitTrackingActions {
        /**
         * Called when the activity has stopped.
         */
        fun onActivityStopped(activityId: String)
    }
}
