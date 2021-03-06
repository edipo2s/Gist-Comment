package com.edipo2s.gistcomment.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.edipo2s.gistcomment.*
import com.edipo2s.gistcomment.model.entity.Gist
import com.edipo2s.gistcomment.model.entity.GistComment
import kotlinx.android.synthetic.main.activity_gist.*
import kotlinx.android.synthetic.main.item_comment.view.*
import org.threeten.bp.format.DateTimeFormatter

internal class GistActivity : BaseActivity(R.layout.activity_gist) {

    private val gistId by lazy { intent.getStringExtra(Intent.EXTRA_TEXT) }
    private val viewModel by lazy { getViewModel<GistViewModel>() }

    private val gistFilesAdapter by lazy {
        ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
    }
    private val gistCommentsAdapter by lazy {
        GistCommentsAdapter(object : DiffUtil.ItemCallback<GistComment>() {
            override fun areItemsTheSame(oldItem: GistComment?, newItem: GistComment?): Boolean {
                return oldItem?.id == newItem?.id
            }

            override fun areContentsTheSame(oldItem: GistComment?, newItem: GistComment?): Boolean {
                return oldItem == newItem
            }
        })
    }

    private var gist: Gist? = null
        set(value) {
            field = value
            if (value != null) {
                gistFilesAdapter.clear()
                gistFilesAdapter.addAll(value.files.map { it.name })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configView()
        viewModel?.let {
            it.gistLiveData.observe(this, Observer {
                onResourceReceived(it) {
                    gist = it
                }
            })
            it.gistCommentsLiveData.observe(this, Observer {
                onResourceReceived(it) {
                    gistCommentsAdapter.submitList(it)
                    list_comments.smoothScrollToPosition(it.size - 1)
                }
            })
            it.gistAuthTokenLiveData.observe(this, Observer {
                onResourceReceived(it) {
                    onReceiveOAuthToken(it.isNotEmpty())
                }
            })
            it.newGistCommentLiveData.observe(this, Observer {
                onResourceReceived(it) {
                    edit_comment.setText("")
                    viewModel?.updateComments()
                    hideKeyboard()
                }
            })
            if (gistId != null) {
                it.requestGist(gistId)
            }
        }
    }

    override fun showLoading(loading: Boolean) {
        super.showLoading(loading)
        image_sign_send.isEnabled = !loading
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            viewModel?.getGitAuthCode(it)
        }
    }

    private fun configView() {
        spinner_files.adapter = gistFilesAdapter
        spinner_files.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                text_gist.text = gist?.files?.get(position)?.content
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        with(list_comments) {
            adapter = gistCommentsAdapter
            setHasFixedSize(true)
        }
        image_sign_send.setOnClickListener {
            viewModel?.sendCommentOrStartSign(edit_comment.text.toString())
        }
    }

    private fun onReceiveOAuthToken(isValidToken: Boolean) {
        edit_comment.isEnabled = isValidToken
        edit_comment.hint = getString(R.string.comment_tip.takeIf { isValidToken }
                ?: R.string.signin_required)
        image_sign_send.setImageResource(R.drawable.svg_send.takeIf { isValidToken }
                ?: R.drawable.svg_github)
        if (isValidToken) {
            edit_comment.requestFocus()
            showKeyboard()
        }
    }

    private class GistCommentsAdapter(diffItemCallback: DiffUtil.ItemCallback<GistComment>) :
            ListAdapter<GistComment, GistCommentsViewHolder>(diffItemCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GistCommentsViewHolder {
            return GistCommentsViewHolder(parent.inflate(R.layout.item_comment))
        }

        override fun onBindViewHolder(holder: GistCommentsViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

    }

    private class GistCommentsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(gistComment: GistComment) {
            with(itemView) {
                image_user.loadFromUrl(gistComment.user_avatar)
                text_name.text = gistComment.user_name
                text_date.text = gistComment.date.toLocalDate().format(DateTimeFormatter.ISO_DATE)
                text_time.text = gistComment.date.toLocalTime().format(DateTimeFormatter.ISO_TIME)
                text_comment.text = gistComment.content
            }
        }

    }

}