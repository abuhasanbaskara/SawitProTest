package com.example.sawitprotest

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                setWindowInsetsListener(activity.findViewById(android.R.id.content))
            }
            override fun onActivityStarted(activity: Activity) {
                // Noncompliant - method is empty
            }
            override fun onActivityResumed(activity: Activity) {
                // Noncompliant - method is empty
            }
            override fun onActivityPaused(activity: Activity) {
                // Noncompliant - method is empty
            }
            override fun onActivityStopped(activity: Activity) {
                // Noncompliant - method is empty
            }
            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                // Noncompliant - method is empty
            }
            override fun onActivityDestroyed(activity: Activity) {
                // Noncompliant - method is empty
            }
        })
    }

    private fun setWindowInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}