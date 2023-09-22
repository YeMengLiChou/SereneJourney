package com.sll.mod_detail.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.res.layoutInflater
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.util.debug
import com.sll.mod_detail.databinding.DetailItemCommentBinding
import com.sll.mod_detail.databinding.DetailItemCommentSecondBinding
import com.sll.mod_detail.repository.DetailRepository
import com.sll.mod_detail.ui.fragment.CommentBottomFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/19
 */
class DetailSecondCommentAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val parentComment: Comment
) : PagingDataAdapter<Comment, DetailSecondCommentAdapter.CommentViewHolder>(comparator) {
    companion object {
        private const val TAG = "DetailSecondCommentAdapter"

        private val comparator = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * 用于处理网络加载
     * */
    private var mCoroutineScope: CoroutineScope? = null

    // 评论的 Fragment
    private var mCommentFragment: CommentBottomFragment? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            DetailItemCommentSecondBinding.inflate(context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.apply {
                // 时间
                includeUserProfile.tvTime.text = item.createTime
                // 用户名
                includeUserProfile.tvUsername.text = item.username
                // 头像
                mCoroutineScope?.launch(Dispatchers.IO) {
                    // 加载图片
                    DetailRepository.requestResponse {
                        DetailRepository.getUserByName(item.username)
                    }.collect { res ->
                        res.onSuccess {
                            launch(Dispatchers.Main) {
                                includeUserProfile.ivUserIcon.setAvatar(it?.avatar)
                            }
                        }
                    }
                }
                tvContent.text = item.content

                root.throttleClick {
                    showCommentBottomFragment(holder, item)
                }
            }
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mCoroutineScope = CoroutineScope(EmptyCoroutineContext)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mCoroutineScope?.cancel() // 当
    }

    // 弹出底部评论
    private fun showCommentBottomFragment(holder: CommentViewHolder, item: Comment) {
        val titleSpannableString = SpannableString("评论 ${item.username}").apply {
            setSpan(ForegroundColorSpan(Color.BLUE), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(1.1f), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        CommentBottomFragment().apply {
            mCommentFragment = this
            show(this@DetailSecondCommentAdapter.fragmentManager, "comment")
            setTitle(titleSpannableString)
            setOnActionListener(object : CommentBottomFragment.OnCommentActionListener {
                override fun onStart(root: View) {
                    SystemBarUtils.showIme(context as Activity, root)
                }

                override fun onDismiss(root: View) {
                    SystemBarUtils.hideIme(context as Activity, root)
                }

                override fun onConfirm() {
                    mCoroutineScope?.launch(Dispatchers.IO) {
                        DetailRepository.requestResponse {
                            DetailRepository.addSecondLevelComment(
                                commentContent,
                                parentComment.id.toLong(),
                                parentComment.pUserId.toLong(),
                                item.id.toLong(),
                                item.pUserId.toLong(),
                                item.shareId.toLong(),
                            )
                        }.collect { res ->
                            debug(TAG, res)
                            res.onSuccess {
                                refresh() // 刷新
                                mCommentFragment?.dismiss()
                                mCommentFragment = null
                                ToastUtils.success("发送成功~")
                            }.onError {
                                ToastUtils.error("发送失败~ ${it.message}")
                            }.onLoading {
                                // TODO: 加载一个弹窗
                            }
                        }
                    }
                }
            })
        }
    }


    inner class CommentViewHolder(
        binding: DetailItemCommentSecondBinding
    ) : BaseBindViewHolder<DetailItemCommentSecondBinding>(binding)
}