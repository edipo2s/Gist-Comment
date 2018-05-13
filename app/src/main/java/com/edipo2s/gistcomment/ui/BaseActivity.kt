package com.edipo2s.gistcomment.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.toast
import com.edipo2s.gistcomment.R
import com.edipo2s.gistcomment.network.resource.Resource
import com.edipo2s.gistcomment.network.resource.Status
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

internal abstract class BaseActivity(@LayoutRes private val layoutRes: Int) :
        AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModelProvider: ViewModelProvider? by lazy { ViewModelProvider(this, viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        configActionBar()
    }

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> = dispatchingAndroidInjector

    protected inline fun <reified T : ViewModel> getViewModel(): T? {
        return viewModelProvider?.get(T::class.java)
    }

    protected fun <T> onResourceReceived(resource: Resource<T>?, onErrorMsgRes: Int? = null, onSuccess: (T) -> Unit) {
        resource?.let {
            showLoading(it.status == Status.LOADING)
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> it.data?.let(onSuccess)
                Status.ERROR -> {
                    val errorMessage = onErrorMsgRes?.let { getString(it) } ?: it.errorMsg ?: ""
                    toast(errorMessage)
                }
            }
        }
    }

    private fun configActionBar() {
        findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            title = ""
            supportActionBar?.title = ""
        }
    }

    protected open fun showLoading(loading: Boolean) {
        findViewById<ProgressBar>(R.id.text_title).isVisible = !loading
        findViewById<ProgressBar>(R.id.progress_loading).isVisible = loading
    }

}