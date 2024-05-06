package com.example.sawitprotest.util

import android.os.Handler
import android.os.Looper
import android.view.View

fun View.setSingleOnClickListener(callback: () -> Unit) {
    this.setOnClickListener {
        this.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ this.isEnabled = true }, 50)
        callback.invoke()
    }
}