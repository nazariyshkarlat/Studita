package com.example.studita.presentation.view_model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.collections.forEach
import kotlin.collections.set

class LiveEvent<T> : MediatorLiveData<T>() {

    private val liveDataToObserve: LiveData<T>
    private val pendingMap: MutableMap<Int, Boolean>

    init {
        val outputLiveData = MediatorLiveData<T>()
        outputLiveData.addSource(this) { currentValue ->
            outputLiveData.value = currentValue
        }
        liveDataToObserve = outputLiveData
        pendingMap = HashMap()
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        pendingMap[observer.hashCode()] = false

        // Observe the internal LiveData
        liveDataToObserve.observe(owner, Observer { t ->
            if (pendingMap[observer.hashCode()] == true) { // don't trigger if the observer wasn't registered
                observer.onChanged(t)
                pendingMap[observer.hashCode()] = false
            }
        })
    }

    override fun setValue(t: T?) {
        pendingMap.forEach { pendingMap[it.key] = true }
        super.setValue(t)
    }

    override fun postValue(value: T?) {
        pendingMap.forEach { pendingMap[it.key] = true }
        super.postValue(value)
    }
}