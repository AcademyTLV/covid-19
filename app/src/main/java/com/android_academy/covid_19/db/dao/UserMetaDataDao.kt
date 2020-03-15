package com.android_academy.covid_19.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_academy.covid_19.db.util.DBConstants

@Dao
interface UserMetaDataDao {
    /* get single user metadata */
    @Query("SELECT * FROM ${DBConstants.USERS_METADATA_TABLE_NAME} limit 1")
    fun getUser(): RoomUserMetaDataEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setUser(user: RoomUserMetaDataEntity)
}