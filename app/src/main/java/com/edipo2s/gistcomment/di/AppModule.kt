package com.edipo2s.gistcomment.di

import android.app.Application
import android.content.Context
import com.edipo2s.gistcomment.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AppModule {

    @Singleton
    @Provides
    fun provideApplication(app: App): Application = app

    @Singleton
    @Provides
    fun provideContext(app: Application): Context = app.applicationContext

}