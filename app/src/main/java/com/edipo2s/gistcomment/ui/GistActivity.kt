package com.edipo2s.gistcomment.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.edipo2s.gistcomment.R
import com.edipo2s.gistcomment.model.entity.Gist
import kotlinx.android.synthetic.main.activity_gist.*

internal class GistActivity : BaseActivity(R.layout.activity_gist) {

    private val gistId by lazy { intent.getStringExtra(Intent.EXTRA_TEXT) }
    private val viewModel by lazy { getViewModel<GistViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel?.let {
            it.gistLiveData.observe(this, Observer {
                onResourceReceived(it) {
                    showGist(it)
                }
            })
            it.requestGist(gistId)
        }
    }

    private fun showGist(gist: Gist) {
        spinner_files.adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, gist.files.map { it.name })
        spinner_files.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                text_gist.text = gist.files[position].content
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }

}