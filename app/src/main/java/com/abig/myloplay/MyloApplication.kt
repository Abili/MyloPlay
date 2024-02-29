package com.abig.myloplay

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyloApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()

        //registerActivityLifecycleCallbacks(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        //Enable Firebase database persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)



    }
}