package com.greetify.ecwidtest

import android.app.Application

class App : Application() {


    companion object {
        val instance: App by lazy { Holder.INSTANCE }
    }

    object Holder {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        Holder.INSTANCE = this
    }
}