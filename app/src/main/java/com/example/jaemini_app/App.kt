package com.example.jaemini_app

import android.app.Application
import android.content.Context
import androidx.core.view.accessibility.AccessibilityEventCompat

class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        val context: Context
            get() = instance!!.applicationContext
    }
}