package com.edwardlee259.reflectapp

import android.app.Application
import com.edwardlee259.reflectapp.di.ApplicationComponent
import com.edwardlee259.reflectapp.di.ApplicationModule
import com.edwardlee259.reflectapp.di.DaggerApplicationComponent
import com.edwardlee259.reflectapp.di.RoomModule

class ReflectApplication : Application() {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent =
            DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this))
                .roomModule(RoomModule(this)).build()
    }

    fun getApplicationComponent(): ApplicationComponent = applicationComponent

}