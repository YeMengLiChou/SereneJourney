package com.sll.mod_detail.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.csd.lib_framework.ext.clickSpan
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_framework.base.adapter.BaseRecyclerAdapter
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.base.viewholder.BaseViewHolder
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.view.click
import com.sll.mod_detail.databinding.DetailItemSecondCommentPreviewBinding
import com.sll.mod_detail.ui.fragment.CommentDetailFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/21
 */
class DetailSecondCommentPreviewAdapter(
    context: Context,
    data: MutableList<Comment>,
    private val parentComment: Comment,
): BaseRecyclerAdapter<DetailItemSecondCommentPreviewBinding, Comment>(context, data) {
    companion object {
        private const val TAG = "DetailSecondCommentPreviewAdapter"
    }

    private var onItemClickListener: ((item: Comment, position: Int) -> Unit)? = null
    override fun bindData(holder: BaseViewHolder, item: Comment, position: Int) {
        // 被回复的用户名
        val replyUsername = getMatchCommentUsername(item.replyCommentUserId.toString())
        // 该评论的用户名
        val username = item.username
        val spannableStringBuilder = SpannableStringBuilder().apply {
            append(SpannableString(username).apply {
                setSpan(ForegroundColorSpan(Color.BLUE), 0, username.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            })
            append(" 回复 ")
            append(SpannableString(replyUsername).apply {
                setSpan(ForegroundColorSpan(Color.BLUE), 0, replyUsername.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            })
            append(": ${item.content}")
        }

        holder.As<BaseBindViewHolder<DetailItemSecondCommentPreviewBinding>>()?.apply {
            binding.root.apply {
                text = spannableStringBuilder
                click {
                    onItemClickListener?.invoke(item, position)
                }
            }

        }
    }

    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): DetailItemSecondCommentPreviewBinding {
        return DetailItemSecondCommentPreviewBinding.inflate(layoutInflater, parent, false)
    }

    private fun getMatchCommentUsername(replyUserId: String): String {
        if (parentComment.pUserId == replyUserId) return parentComment.username
        for (comment in data) {
            if (comment.pUserId == replyUserId) return comment.username
        }
        return ""
    }


    fun setOnItemClickListener(listener: (item: Comment, position: Int) -> Unit) {
        this.onItemClickListener = listener
    }
}