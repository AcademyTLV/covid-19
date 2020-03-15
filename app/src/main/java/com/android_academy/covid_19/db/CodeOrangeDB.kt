package com.android_academy.covid_19.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.db.util.Converters
import com.android_academy.covid_19.db.util.DBConstants.DB_NAME
import com.android_academy.covid_19.repository.model.InfectedLocationModel

@Database(
    entities = [InfectedLocationModel::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CodeOrangeDB : RoomDatabase() {

    companion object {

        /** useInMemory - used for mostly development */
        fun create(context: Context, useInMemory: Boolean = false): CodeOrangeDB {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, CodeOrangeDB::class.java)
            } else {
                databaseBuilder(context, CodeOrangeDB::class.java, DB_NAME)
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun infectionPointsDao(): InfectionLocationsDao
}
