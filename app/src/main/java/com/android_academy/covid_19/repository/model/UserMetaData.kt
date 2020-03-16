package com.android_academy.covid_19.repository.model

import java.util.InputMismatchException

enum class UserType(val strValue: String) {
    POSITIVE("positive"),
    WAS_POSITIVE("was_positive"),
    NEGATIVE("negative");

    companion object {
        fun from(value: String): UserType = values().find { it.strValue == value }
            ?: throw InputMismatchException("Can not create enum PersonalChatMessageAdapter.ViewType from Int: $value")
    }
}

data class UserMetaData(
    val id: String,
    val type: UserType
)
