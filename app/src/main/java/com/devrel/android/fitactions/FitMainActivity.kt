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
import com.devrel.android.fitactions.home.FitStatsFragment
import com.devrel.android.fitactions.model.FitActivity
import com.devrel.android.fitactions.model.FitRepository
import com.devrel.android.fitactions.tracking.FitTrackingFragment
import com.devrel.android.fitactions.tracking.FitTrackingService


class FitMainActivity : AppCompatActivity(), FitStatsFragment.FitStatsActions, FitTrackingFragment.FitTrackingActions {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fit_activity)

        // Get the action and data from the intent to handle it
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        when (action) {
            Intent.ACTION_VIEW -> handleDeepLink(data)
            else -> showDefaultView()
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        when (fragment) {
            is FitStatsFragment -> fragment.actionsCallback = this
            is FitTrackingFragment -> fragment.actionsCallback = this
        }
    }

    override fun onStartActivity() {
        updateView(
            newFragmentClass = FitTrackingFragment::class.java,
            arguments = Bundle().apply { putSerializable(FitTrackingFragment.PARAM_TYPE, FitActivity.Type.RUNNING) },
            toBackStack = true
        )
    }

    override fun onActivityStopped(activityId: String) {
        // TODO show details
        supportFragmentManager.popBackStack()
    }

    /**
     * Use the URI provided by the intent to handle the different deep-links
     */
    private fun handleDeepLink(data: Uri?) {
        when (data?.path) {
            DeepLink.STATS -> {
                updateView(FitStatsFragment::class.java)
            }
            DeepLink.START -> {
                val exerciseType = data.getQueryParameter(DeepLink.Params.ACTIVITY_TYPE).orEmpty()
                val type = FitActivity.Type.find(exerciseType)
                val arguments = Bundle().apply { putSerializable(FitTrackingFragment.PARAM_TYPE, type) }

                updateView(FitTrackingFragment::class.java, arguments)
            }
            DeepLink.STOP -> {
                stopService(Intent(this, FitTrackingService::class.java))
                updateView(FitStatsFragment::class.java)
            }
            else -> {
                showDefaultView()
            }
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
