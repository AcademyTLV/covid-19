package com.android_academy.covid_19.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations

@MainThread
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, func)
}

@MainThread
fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}

/**
 * Inspired from https://medium.com/@gauravgyal/combine-results-from-multiple-async-requests-90b6b45978f7
 * */
fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

@MainThread
fun <X> LiveData<X>.filter(test: (X) -> Boolean): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        if (it != null && test(it)) {
            result.value = it
        }
    }

    return result
}
