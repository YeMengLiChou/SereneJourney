package com.sll.mod_detail.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.res.layoutInflater
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.longClick
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.ClipboardUtils
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_detail.R
import com.sll.mod_detail.databinding.DetailItemCommentBinding
import com.sll.mod_detail.repository.DetailRepository
import com.sll.mod_detail.ui.fragment.CommentBottomFragment
import com.sll.mod_detail.ui.fragment.CommentDetailFragment
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
class DetailFirstCommentAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val onCommentClickAction: (item: Comment, commentCount: Int) -> Unit
) : PagingDataAdapter<Comment, DetailFirstCommentAdapter.CommentViewHolder>(comparator) {
    companion object {
        private const val TAG = "DetailFirstCommentAdapter"

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
            DetailItemCommentBinding.inflate(context.layoutInflater, parent, false)
        ).apply {
            binding.apply {
                linearLayoutReply.setClipViewCornerRadius(8.dp)
            }
        }
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
                // 复制内容
                root.longClick {
                    val text = item.content
                    ClipboardUtils.copyString(context, text, "comment")
                    ToastUtils.success("复制 ${item.username} 评论成功~")
                    return@longClick true
                }
                // 加载二级评论
                loadSecondLevelComment(holder, item)

                ivComment.throttleClick {
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

    // 加载二级评论
    private fun loadSecondLevelComment(holder: CommentViewHolder, parentComment: Comment) {
        holder.binding.linearLayoutReply.gone()
        mCoroutineScope?.launch(Dispatchers.IO) {
            DetailRepository.requestResponse {
                DetailRepository.getSecondLevelComments(parentComment.id.toLong(), 0, 3, parentComment.shareId.toLong())
            }.collect { res ->
                res.onSuccess { data ->
                    data?.let {
                        mCoroutineScope?.launch(Dispatchers.Main) {
                            // 如果二级评论不为空，展示前三个评论
                            if (it.total != 0) {
                                holder.binding.apply {
                                    linearLayoutReply.visible()
                                    recyclerViewReply.layoutManager = object : LinearLayoutManager(context) {
                                        // 禁止滑动
                                        override fun canScrollVertically(): Boolean {
                                            return false
                                        }
                                    }.apply { orientation = LinearLayoutManager.VERTICAL }

                                    recyclerViewReply.adapter = DetailSecondCommentPreviewAdapter(context, it.records.toMutableList(), parentComment).apply {
                                        setOnItemClickListener { item, position ->
                                            onCommentClickAction.invoke(parentComment, it.total)
                                            showCommentDetailFragment()
                                        }
                                    }
                                    // 显示更多回复
                                    if (it.total > 3) {
                                        tvMore.visible()
                                        tvMore.text = "共${it.total}条回复 >"
                                        tvMore.click { _ ->
                                            onCommentClickAction.invoke(parentComment, it.total)
                                            showCommentDetailFragment()
                                        }
                                    } else {
                                        tvMore.gone()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    // 弹出底部评论
    private fun showCommentBottomFragment(holder: CommentViewHolder, item: Comment) {

        val titleSpannableString = SpannableString("评论 ${item.username}").apply {
            setSpan(ForegroundColorSpan(Color.BLUE), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(1.2f), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        CommentBottomFragment().apply {
            mCommentFragment = this
            show(this@DetailFirstCommentAdapter.fragmentManager, "comment")
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
                                item.id.toLong(),
                                item.pUserId.toLong(),
                                item.id.toLong(),
                                item.pUserId.toLong(),
                                item.shareId.toLong(),
                            )
                        }.collect { res ->
                            res.onSuccess {
                                // 成功后刷新
                                launch(Dispatchers.Main) {
                                    loadSecondLevelComment(holder, item)
                                }
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

    private fun showCommentDetailFragment() {
        fragmentManager.commit {
            add(R.id.fragment_container_view, CommentDetailFragment())
            addToBackStack("detail_comment")
            setReorderingAllowed(true)
        }
    }

    inner class CommentViewHolder(
        binding: DetailItemCommentBinding
    ) : BaseBindViewHolder<DetailItemCommentBinding>(binding)
}