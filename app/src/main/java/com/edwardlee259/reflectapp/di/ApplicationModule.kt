package com.edwardlee259.reflectapp.di

import android.app.Application
import com.edwardlee259.reflectapp.ReflectApplication
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(application: ReflectApplication) {
    private val mApplication = application

    @Provides
    fun provideApplication(): Application = mApplication
}