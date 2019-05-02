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

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.service.voice.VoiceInteractionService
import androidx.slice.SliceManager
import com.devrel.android.fitactions.slices.FitSliceProvider


class FitApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Grant the assistant permission when the application is create, it's okay to grant it each time.
        grantAssistantPermissions()
    }

    /**
     * Grant slice permissions to the assistance in order to display slices without user input.
     *
     * Note: this is needed when using AndroidX SliceProvider.
     */
    private fun grantAssistantPermissions() {
        getAssistantPackage()?.let { assistantPackage ->
            val sliceProviderUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(FitSliceProvider.SLICE_AUTHORITY)
                .build()

            SliceManager.getInstance(this).grantSlicePermission(assistantPackage, sliceProviderUri)
        }
    }

    /**
     * Find the assistant package name
     */
    private fun getAssistantPackage(): String? {
        val resolveInfoList = packageManager?.queryIntentServices(
            Intent(VoiceInteractionService.SERVICE_INTERFACE), 0
        )
        return resolveInfoList?.firstOrNull()?.serviceInfo?.packageName
    }
}