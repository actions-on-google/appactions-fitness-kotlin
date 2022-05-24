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

package com.devrel.android.fitactions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devrel.android.fitactions.BiiIntents.START_EXERCISE
import com.devrel.android.fitactions.BiiIntents.STOP_EXERCISE
import com.devrel.android.fitactions.home.FitStatsFragment
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository
import com.devrel.android.fitactions.tracking.FitTrackingFragment
import com.devrel.android.fitactions.tracking.FitTrackingService

/**
 * Main activity responsible for the app navigation and handling Android intents.
 */
class FitMainActivity : AppCompatActivity(), FitStatsFragment.FitStatsActions, FitTrackingFragment.FitTrackingActions {

    /**
     * Handle the intent this activity was launched with.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fit_activity)

        intent?.handleIntent()
    }

    /**
     * Handle new intents that are coming while the activity is on foreground since we set the
     * launchMode to be singleTask, avoiding multiple instances of this activity to be created.
     *
     * See [launchMode](https://developer.android.com/guide/topics/manifest/activity-element#lmode)
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.handleIntent()
    }

    /**
     * Handles the action from the intent base on the type.
     *
     * @receiver the intent to handle
     */
    private fun Intent.handleIntent() {
        when (action) {
            // When the BII is matched, Intent.Action_VIEW will be used
            Intent.ACTION_VIEW -> handleIntent(data)
            // Otherwise start the app as you would normally do.
            else -> showDefaultView()
        }
    }

    /**
     * Use extras provided by the intent to handle the different BIIs
     */
    private fun handleIntent(data: Uri?) {
        // path is normally used to indicate which view should be displayed
        // i.e https://fit-actions.firebaseapp.com/start?exerciseType="Running" -> path = "start"

        val startExercise = intent?.extras?.getString(START_EXERCISE)
        val stopExercise = intent?.extras?.getString(STOP_EXERCISE)

        if (startExercise != null){
            val type = FitActivity.Type.find(startExercise)
            val arguments = Bundle().apply {
                putSerializable(FitTrackingFragment.PARAM_TYPE, type)
            }
            updateView(FitTrackingFragment::class.java, arguments)
        } else if(stopExercise != null){
            // Stop the tracking service if any and return to home screen.
            stopService(Intent(this, FitTrackingService::class.java))
            updateView(FitStatsFragment::class.java)
        } else{
            // path is not supported or invalid, start normal flow.
            showDefaultView()

        }
    }

    /**
     * When a fragment is attached add the required callback methods.
     */
    override fun onAttachFragment(fragment: Fragment) {
        when (fragment) {
            is FitStatsFragment -> fragment.actionsCallback = this
            is FitTrackingFragment -> fragment.actionsCallback = this
        }
    }

    /**
     * Callback method from the FitStatsFragment to indicate that the tracking activity flow should be shown.
     */
    override fun onStartActivity() {
        updateView(
            newFragmentClass = FitTrackingFragment::class.java,
            arguments = Bundle().apply { putSerializable(FitTrackingFragment.PARAM_TYPE, FitActivity.Type.RUNNING) },
            toBackStack = true
        )
    }

    /**
     * Callback method when an activity stops.
     * We could show a details screen, for now just go back to home screen.
     */
    override fun onActivityStopped(activityId: String) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            updateView(FitStatsFragment::class.java)
        }
    }

    /**
     * Show ongoing activity or stats if none
     */
    private fun showDefaultView() {
        val fragmentClass = if (FitRepository.getInstance(this).getOnGoingActivity().value != null) {
            FitTrackingFragment::class.java
        } else {
            FitStatsFragment::class.java
        }
        updateView(fragmentClass)
    }

    /**
     * Utility method to update the Fragment with the given arguments.
     */
    private fun updateView(
        newFragmentClass: Class<out Fragment>,
        arguments: Bundle? = null,
        toBackStack: Boolean = false
    ) {
        val currentFragment = supportFragmentManager.fragments.firstOrNull()
        if (currentFragment != null && currentFragment::class.java == newFragmentClass) {
            return
        }

        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            newFragmentClass.classLoader!!,
            newFragmentClass.name
        )
        fragment.arguments = arguments

        supportFragmentManager.beginTransaction().run {
            replace(R.id.fitActivityContainer, fragment)
            if (toBackStack) {
                addToBackStack(null)
            }
            commit()
        }
    }
}
