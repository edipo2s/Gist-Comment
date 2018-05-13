package com.edipo2s.gistcomment.di

import com.edipo2s.gistcomment.di.modules.MainActivityModule
import com.edipo2s.gistcomment.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ViewsModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun provideMainActivity(): MainActivity

}