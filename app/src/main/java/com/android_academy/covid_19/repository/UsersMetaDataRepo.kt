package com.android_academy.covid_19.repository

import com.android_academy.covid_19.db.dao.RoomUserMetaDataEntity
import com.android_academy.covid_19.db.dao.UserMetaDataDao

val USER_TYPES = arrayOf("positive", "was_positive", "not_positive")

interface IUserMetaDataRepo {
    suspend fun getUserMetaData(): RoomUserMetaDataEntity
    suspend fun setUserMetaData(user: RoomUserMetaDataEntity)
    suspend fun getUserType(): String
    suspend fun setUserType(userType: String)
}

class UserMetaDataRepo(
    private val usersMetaDataDao: UserMetaDataDao
) : IUserMetaDataRepo {

    override suspend fun getUserMetaData(): RoomUserMetaDataEntity = usersMetaDataDao.getUser()

    override suspend fun getUserType(): String = getUserMetaData().type

    override suspend fun setUserMetaData(user: RoomUserMetaDataEntity) =
        usersMetaDataDao.setUser(user)

    override suspend fun setUserType(userType: String) {
        if (userType !in USER_TYPES) return
        return usersMetaDataDao.setUser(RoomUserMetaDataEntity(type = userType))
    }
}