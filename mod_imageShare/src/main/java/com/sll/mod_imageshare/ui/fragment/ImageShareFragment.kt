package com.sll.mod_imageshare.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sll.lib_common.interfaces.FragmentScrollable
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.view.checkReachTop
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_image_share.databinding.IsFragmentDiscoverBinding
import com.sll.mod_imageshare.adapter.FooterAdapter
import com.sll.mod_imageshare.adapter.ImageShareAdapter
import com.sll.mod_imageshare.ui.vm.ImageShareViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class ImageShareFragment() :
    BaseMvvmFragment<IsFragmentDiscoverBinding, ImageShareViewModel>(),
    FragmentScrollable {

    companion object {
        private const val TAG = "ImageShareFragment"
        const val TYPE_LIKE = 1
        const val TYPE_COLLECT = 2
        const val TYPE_FOCUS = 3
        const val TYPE_DISCOVER = 4

        const val KEY_CONTAINER_ID = "container_id"
        const val KEY_TYPE = "type"
        fun newInstance(containerId: Int, type: Int): ImageShareFragment {
            return ImageShareFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_CONTAINER_ID, containerId)
                    putInt(KEY_TYPE, type)
                }
            }
        }
    }

    private var containerId: Int = 0

    private var type = 0

    override fun scrollToTop() {
        binding.isRecyclerViewContent.stopScroll()
        binding.isRecyclerViewContent.smoothScrollToPosition(0) // 平滑滚动上去
    }

    override fun scrollTo(x: Int, y: Int) {
        // not support
    }

    override fun scrollBy(dx: Int, dy: Int) {
        // not support
    }

    override fun checkReachTop(): Boolean {
        return checkAttachedActivity() && binding.isRecyclerViewContent.checkReachTop()
    }

    override fun onDefCreateView() {
        arguments?.let {
            containerId = it.getInt(KEY_CONTAINER_ID)
            type = it.getInt(KEY_TYPE)
        }
        initRecyclerView()

    }

    override fun initViewBinding(container: ViewGroup?) = IsFragmentDiscoverBinding.inflate(layoutInflater)

    override fun getViewModelClass() = ImageShareViewModel::class

    private fun initRecyclerView() {
        launchOnCreated {
            // 获取数据源
            val pagingAdapter = ImageShareAdapter(requireContext(), requireActivity().lifecycleScope) { iv, imageShare, position ->
                PreviewFragment.commit(
                    this@ImageShareFragment,
                    requireActivity().supportFragmentManager,
                    containerId,
                    imageShare, position, iv
                )
            }
            pagingAdapter.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        binding.isSwipeRefreshContent.isRefreshing = true
                    }

                    is LoadState.NotLoading -> {
                        binding.isSwipeRefreshContent.isRefreshing = false
                    }

                    is LoadState.Error -> {
                        binding.isSwipeRefreshContent.isRefreshing = false
                        ToastUtils.error("加载失败: ${(it.refresh as LoadState.Error).error.message}")
                    }
                }
            }
            // 线性布局
            binding.isRecyclerViewContent.layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
            binding.isRecyclerViewContent.adapter = pagingAdapter.withLoadStateFooter(FooterAdapter(requireContext()) {
                pagingAdapter.retry()
            })
            // 取消动画，解决闪烁问题
            binding.isRecyclerViewContent.itemAnimator?.As<SimpleItemAnimator>()?.supportsChangeAnimations = false

            // 下拉刷新
            binding.isSwipeRefreshContent.setOnRefreshListener {
                pagingAdapter.refresh()
            }

            when (type) {
                TYPE_DISCOVER -> {
                    // 更新数据
                    viewModel.fetchDiscoverImageShares().collectLatest {
                        pagingAdapter.submitData(it)
                    }
                }
                TYPE_LIKE -> {
                    // 更新数据
                    viewModel.fetchLikeImageShares().collectLatest {
                        pagingAdapter.submitData(it)
                    }
                }
                TYPE_COLLECT -> {
                    pagingAdapter.setFocus(false)
                    // 更新数据
                    viewModel.fetchCollectImageShares().collectLatest {
                        pagingAdapter.submitData(it)
                    }
                }
                TYPE_FOCUS -> {
                    // 更新数据
                    viewModel.fetchFocusImageShares().collectLatest {
                        pagingAdapter.submitData(it)
                    }
                }
            }


        }
    }


}