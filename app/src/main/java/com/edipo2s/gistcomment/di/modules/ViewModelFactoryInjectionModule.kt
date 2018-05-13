package com.edipo2s.gistcomment.di.modules

import android.arch.lifecycle.ViewModelProvider
import com.edipo2s.gistcomment.arch.ViewModelFactory
import dagger.Binds
import dagger.Module

/**
 * Created by ediposouza on 27/10/17.
 */
@Module
abstract class ViewModelFactoryInjectionModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}