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

package com.devrel.android.fitactions.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.devrel.android.fitactions.R
import com.devrel.android.fitactions.databinding.FitStatsFragmentBinding
import com.devrel.android.fitactions.model.FitRepository
import java.util.concurrent.TimeUnit

/**
 * Fragment to show the last users activities and stats.
 *
 * It observes the total count stats and the last activities updating the view upon changes
 */
class FitStatsFragment : Fragment() {

    lateinit var actionsCallback: FitStatsActions

    private var _binding: FitStatsFragmentBinding? =null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FitStatsFragmentBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FitStatsAdapter()
        binding.statsList.adapter = adapter

        val repository = FitRepository.getInstance(requireContext())
        repository.getStats().observe(viewLifecycleOwner) { fitStats ->
            binding.statsActivityCount.text = getString(
                R.string.stats_total_count,
                fitStats.totalCount
            )
            binding.statsDistanceCount.text = getString(
                R.string.stats_total_distance,
                fitStats.totalDistanceMeters.toInt()
            )
            val durationInMin = TimeUnit.MILLISECONDS.toMinutes(fitStats.totalDurationMs)
            binding.statsDurationCount.text =
                getString(R.string.stats_total_duration, durationInMin)
        }

        repository.getLastActivities(10).observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.statsList.smoothScrollToPosition(0)
        }

        binding.statsStartButton.setOnClickListener {
            actionsCallback.onStartActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface FitStatsActions {
        fun onStartActivity()
    }
}
