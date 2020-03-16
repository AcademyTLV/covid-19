package com.android_academy.covid_19.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

@MainThread
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, func)
}

@MainThread
fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}
