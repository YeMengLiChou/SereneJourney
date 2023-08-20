package com.sll.mod_imageshare.ui.fragment

import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.commit
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
import com.sll.lib_framework.util.debug
import com.sll.mod_image_share.R
import com.sll.mod_image_share.databinding.IsFragmentDiscoverBinding
import com.sll.mod_imageshare.adapter.DiscoverAdapter
import com.sll.mod_imageshare.adapter.DiscoverFooterAdapter
import com.sll.mod_imageshare.ui.vm.ImageShareViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class DiscoverFragment(
    private val containerId: Int
) : BaseMvvmFragment<IsFragmentDiscoverBinding, ImageShareViewModel>(), FragmentScrollable {

    companion object {
        private const val TAG = "DiscoverFragment"
    }

    private lateinit var pagingAdapter: DiscoverAdapter

    override fun scrollToTop() {
        binding.isRecyclerViewContent.stopScroll()
        binding.isRecyclerViewContent.smoothScrollToPosition(0)
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
        initRecyclerView()
    }

    override fun initViewBinding(container: ViewGroup?) = IsFragmentDiscoverBinding.inflate(layoutInflater)

    override fun getViewModelClass() = ImageShareViewModel::class

    private fun initRecyclerView() {
        launchOnCreated {
            // 获取数据源
            pagingAdapter = DiscoverAdapter(requireContext(), requireActivity().lifecycleScope) { iv, imageShare, position ->
                // 过渡动画
                ViewCompat.setTransitionName(iv, "preview")
                exitTransition = Fade().apply { duration = 300 }
                requireActivity().supportFragmentManager.commit {
                    add(containerId, PreviewFragment().apply {
                        setItemPosition(imageShare, position)
                    }, "preview")
                    addSharedElement(iv, "preview")
                    sharedElementEnterTransition = TransitionSet().apply {
                        addTransition(ChangeTransform())
                        addTransition(ChangeImageTransform())
                    }
                    setReorderingAllowed(true)
                    addToBackStack("preview")
                }
            }.apply {
                addLoadStateListener {
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
            }
            // 线性布局
            binding.isRecyclerViewContent.layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
            binding.isRecyclerViewContent.adapter = pagingAdapter.withLoadStateFooter(DiscoverFooterAdapter(requireContext()) {
                pagingAdapter.retry()
            })
            // 取消动画，解决闪烁问题
            binding.isRecyclerViewContent.itemAnimator?.As<SimpleItemAnimator>()?.supportsChangeAnimations = false

            // 下拉刷新
            binding.isSwipeRefreshContent.setOnRefreshListener {
                pagingAdapter.refresh()
            }
            // 更新数据
            viewModel.fetchDiscoverImageShares().collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }


}