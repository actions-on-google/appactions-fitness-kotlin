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
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Defines the Room DB configuration for the application.
 */
@Database(entities = [FitActivity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FitDatabase : RoomDatabase() {

    /**
     * @return an instance of FitActivityDao
     */
    abstract fun fitActivityDao(): FitActivityDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: FitDatabase? = null

        fun getInstance(context: Context): FitDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): FitDatabase {
            return Room.databaseBuilder(context, FitDatabase::class.java, "FitActionsDB")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        prepopulate(context)
                    }
                })
                .build()
        }

        /**
         * Prepopulate the database with some mock activities, normally this would not be needed.
         */
        private fun prepopulate(context: Context) {
            Executors.newSingleThreadExecutor().submit {
                val fitActivityDao = getInstance(context).fitActivityDao()
                val currentTime = System.currentTimeMillis()
                repeat(10) {
                    fitActivityDao.insert(
                        FitActivity(
                            id = UUID.randomUUID().toString(),
                            date = currentTime.minus(TimeUnit.DAYS.toMillis(it.toLong())),
                            type = FitActivity.Type.RUNNING,
                            distanceMeters = Random.nextDouble(1.0, 30.0) * 1000,
                            durationMs = TimeUnit.MINUTES.toMillis(Random.nextLong(10, 90))
                        )
                    )
                }
            }
        }
    }
}

/**
 * Dao interface that defines the available DB methods/queries
 */
@Dao
interface FitActivityDao {

    /**
     * @param max define a max result count.
     * @return a list of FitActivity items ordered by date
     */
    @Query("SELECT * FROM fit_activities ORDER BY date DESC LIMIT :max")
    fun getAll(max: Int = -1): LiveData<List<FitActivity>>

    /**
     * @param max define a max result count.
     * @return a list of FitActivity items ordered by date
     */
    @Query("SELECT * FROM fit_activities WHERE type == :type ORDER BY date DESC LIMIT :max")
    fun getAllOfType(type: FitActivity.Type, max: Int = -1): LiveData<List<FitActivity>>

    /**
     * @return a FitStats of the user
     */
    @Query("SELECT COUNT(*) as totalCount, SUM(distanceMeters) as totalDistanceMeters, SUM(durationMs) as totalDurationMs FROM fit_activities")
    fun getStats(): LiveData<FitStats>

    /**
     * Get an activity by ID
     */
    @Query("SELECT * FROM fit_activities WHERE id == :id")
    fun getById(id: String): LiveData<FitActivity>

    /**
     * Insert a new FitActivity in the DB
     */
    @Insert
    fun insert(fitActivity: FitActivity)

    /**
     * Delete the given FitActivity from DB
     */
    @Delete
    fun delete(fitActivity: FitActivity)
}

/**
 * Converter class for the DB to convert from/to enum FitActivity.Type
 */
class Converters {

    @TypeConverter
    fun fromType(value: FitActivity.Type): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toType(value: Int): FitActivity.Type {
        val values = FitActivity.Type.values()
        return if (value < values.size) values[value] else FitActivity.Type.UNKNOWN
    }
}