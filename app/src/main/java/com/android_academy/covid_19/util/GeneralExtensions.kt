package com.android_academy.covid_19.util

val Any.logTag: String
    get() = this::class.java.simpleName

/**
 *  Multiple let check conditions - https://stackoverflow.com/a/35522422/4707356
 *  */
fun <T1 : Any, T2 : Any, R : Any> lets(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}


