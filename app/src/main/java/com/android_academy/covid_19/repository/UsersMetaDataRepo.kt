package com.android_academy.covid_19.repository

import com.android_academy.covid_19.db.dao.UserMetaDataDao
import com.android_academy.covid_19.db.dao.toDB
import com.android_academy.covid_19.repository.model.UserMetaData
import com.android_academy.covid_19.repository.model.UserType
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

interface UserMetaDataRepo {
    suspend fun getCurrentUser(): UserMetaData?
    suspend fun setCurrentUser(user: UserMetaData)
    suspend fun setUserType(userType: UserType)
}

class UserMetaDataRepoImpl(
    private val usersMetaDataDao: UserMetaDataDao
) : UserMetaDataRepo {

    override suspend fun getCurrentUser(): UserMetaData? = withContext(IO) {
        return@withContext usersMetaDataDao
            .getUser()
            ?.toUserMetadata()
    }

    override suspend fun setCurrentUser(user: UserMetaData) = withContext(IO) {
        usersMetaDataDao
            .getUser()
            ?.toUserMetadata()
        Unit
    }

    override suspend fun setUserType(userType: UserType) = withContext(IO) {
        val currentUser = getCurrentUser()
        currentUser?.let {
            if (it.type == userType) {
                return@withContext
            } else {
                val updated = it.copy(type = userType)
                usersMetaDataDao.setUser(updated.toDB())
            }
        } ?: throw UnsupportedOperationException("No user found in DB")
    }
}
