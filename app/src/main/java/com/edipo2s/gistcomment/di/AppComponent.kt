package com.edipo2s.gistcomment.di

import com.edipo2s.gistcomment.App
import com.edipo2s.gistcomment.di.modules.AppModule
import com.edipo2s.gistcomment.di.modules.NetworkModule
import com.edipo2s.gistcomment.di.modules.ViewModelFactoryInjectionModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    NetworkModule::class,
    ViewsModule::class,
    ViewModelFactoryInjectionModule::class])
internal interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {

        abstract override fun build(): AppComponent

    }

}