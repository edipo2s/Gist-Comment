package com.edipo2s.gistcomment.di

import android.arch.lifecycle.ViewModel
import com.edipo2s.gistcomment.arch.ViewModelKey
import com.edipo2s.gistcomment.di.modules.MainActivityModule
import com.edipo2s.gistcomment.di.modules.ViewModelFactoryInjectionModule
import com.edipo2s.gistcomment.ui.GistActivity
import com.edipo2s.gistcomment.ui.GistViewModel
import com.edipo2s.gistcomment.ui.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelFactoryInjectionModule::class])
internal abstract class ViewsModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideGistActivity(): GistActivity

    @ActivityScope
    @Binds
    @IntoMap
    @ViewModelKey(GistViewModel::class)
    internal abstract fun bindGistViewModel(viewModel: GistViewModel): ViewModel

}