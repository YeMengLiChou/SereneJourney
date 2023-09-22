package com.sll.mod_detail.ui.fragment

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.sll.lib_common.entity.dto.Comment
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.launchIO
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.height
import com.sll.lib_framework.ext.view.longClick
import com.sll.lib_framework.util.ClipboardUtils
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.util.debug
import com.sll.mod_detail.adapter.DetailSecondCommentAdapter
import com.sll.mod_detail.adapter.FooterAdapter
import com.sll.mod_detail.databinding.DetailFragmentCommentDetailBinding
import com.sll.mod_detail.repository.DetailRepository
import com.sll.mod_detail.ui.vm.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/21
 */
class CommentDetailFragment : BaseMvvmFragment<DetailFragmentCommentDetailBinding, DetailViewModel>() {
    companion object {
        private const val TAG = "CommentDetailFragment"
    }

    private lateinit var mCommentAdapter: DetailSecondCommentAdapter

    private lateinit var parentComment: Comment

    private var mCommentFragment: CommentBottomFragment? = null

    override fun onDefCreateView() {
        parentComment = viewModel.detailParentComment!!
        val height = SystemBarUtils.getStatusBarHeight(requireActivity())
        binding.spaceMargin.height(height)
        binding.ivBack.click {
            parentFragmentManager.popBackStackImmediate()
        }
        loadParentComment(parentComment)
        loadSecondComments()
        initView()
    }

    override fun initViewBinding(container: ViewGroup?): DetailFragmentCommentDetailBinding {
        return DetailFragmentCommentDetailBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModelClass(): KClass<DetailViewModel> {
        return DetailViewModel::class
    }

    /**
     * 加载上方的一级评论
     * */
    private fun loadParentComment(parentComment: Comment) {
        binding.includeParentComment.apply {
            // 时间
            includeUserProfile.tvTime.text = parentComment.createTime
            // 用户名
            includeUserProfile.tvUsername.text = parentComment.username
            // 头像
            launchIO {
                // 加载图片
                DetailRepository.requestResponse {
                    DetailRepository.getUserByName(parentComment.username)
                }.collect { res ->
                    res.onSuccess {
                        launch(Dispatchers.Main) {
                            includeUserProfile.ivUserIcon.setAvatar(it?.avatar)
                        }
                    }
                }
            }
            tvContent.text = parentComment.content
            val titleSpannableString = SpannableString("评论 ${parentComment.username}").apply {
                setSpan(ForegroundColorSpan(Color.BLUE), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(1.2f), 3, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            // 弹出
            root.click {
                CommentBottomFragment().apply {
                    mCommentFragment = this
                    show(this@CommentDetailFragment.childFragmentManager, "comment")
                    setTitle(titleSpannableString)
                    setOnActionListener(object : CommentBottomFragment.OnCommentActionListener {
                        override fun onStart(root: View) {
                            SystemBarUtils.showIme(requireActivity(), root)
                        }

                        override fun onDismiss(root: View) {
                            SystemBarUtils.hideIme(requireActivity(), root)
                        }

                        override fun onConfirm() {
                            launchIO {
                                DetailRepository.requestResponse {
                                    DetailRepository.addSecondLevelComment(
                                        commentContent,
                                        parentComment.id.toLong(),
                                        parentComment.pUserId.toLong(),
                                        parentComment.id.toLong(),
                                        parentComment.pUserId.toLong(),
                                        parentComment.shareId.toLong(),
                                    )
                                }.collect { res ->
                                    res.onSuccess {
                                        // 成功后刷新
                                        launch(Dispatchers.Main) {
                                            mCommentAdapter.refresh()
                                        }
                                        mCommentFragment?.dismiss()
                                        mCommentFragment = null
                                        viewModel.setNeedRefresh(true)
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
            // 长按复制评论
            root.longClick {
                val text = tvContent.text.toString()
                return@longClick if (text.isEmpty()) false
                else {
                    ClipboardUtils.copyString(requireContext(), text, "comment")
                    ToastUtils.success("复制 ${parentComment.username} 评论成功")
                    true
                }
            }
        }
    }

    /**
     * 加载下方的二级评论
     * */
    private fun loadSecondComments() {
        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.VERTICAL }
            adapter = DetailSecondCommentAdapter(requireContext(), childFragmentManager, parentComment).apply {
                mCommentAdapter = this
                // 底部加载栏
                withLoadStateFooter(FooterAdapter(context) {
                    this.retry() // 失败重试
                })
                // 状态栏加载
                addLoadStateListener {
                    when (it.refresh) {
                        is LoadState.Loading -> {
                            binding.refreshContent.isRefreshing = true
                            viewModel.setNeedRefresh(true)
                        }

                        is LoadState.NotLoading -> {
                            binding.refreshContent.isRefreshing = false
                        }

                        is LoadState.Error -> {
                            binding.refreshContent.isRefreshing = false
                            ToastUtils.error("加载失败:${(it.refresh as LoadState.Error).error.message}")
                        }

                    }
                }
            }
        }
        launchOnCreated {
            viewModel.getSecondLevelComments(parentComment.id.toLong()).collect {
                mCommentAdapter.submitData(it)
            }
        }
    }

    private fun initView() {
        binding.refreshContent.setOnRefreshListener {
            mCommentAdapter.refresh()
        }
    }

}
