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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devrel.android.fitactions.R

/**
 * Entity that describes an activity performed by the user.
 *
 * This entity is used for the Room DB in the fit_activities table.
 */
@Entity(
    tableName = "fit_activities",
    indices = [Index("id")]
)
data class FitActivity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "type") val type: Type = Type.UNKNOWN,
    @ColumnInfo(name = "distanceMeters") val distanceMeters: Double,
    @ColumnInfo(name = "durationMs") val durationMs: Long
) {

    /**
     * Defines the type of activity
     */
    enum class Type(val nameId: Int) {
        UNKNOWN(R.string.activity_unknown),
        RUNNING(R.string.activity_running),
        WALKING(R.string.activity_walking),
        CYCLING(R.string.activity_cycling);

        companion object {

            /**
             * @return a FitActivity.Type that matches the given name
             */
            fun find(type: String): Type {
                return values().find { it.name.equals(other = type, ignoreCase = true) } ?: UNKNOWN
            }
        }
    }
}