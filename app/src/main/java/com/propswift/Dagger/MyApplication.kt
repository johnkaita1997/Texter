package com.propswift.Dagger

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    var currentActivity: Activity? = null

    override fun onCreate() {

        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                currentActivity = null
            }

            override fun onActivityStopped(activity: Activity) {
                currentActivity = null
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                currentActivity = activity
            }

            override fun onActivityDestroyed(activity: Activity) {
                currentActivity = null
            }

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                currentActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

        })
    }

}



