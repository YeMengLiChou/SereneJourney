package com.sll.mod_imageshare.ui.item

import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sll.lib_common.interfaces.FragmentScrollable
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_image_share.databinding.IsFragmentDiscoverBinding
import com.sll.mod_imageshare.adapter.DiscoverAdapter
import com.sll.mod_imageshare.adapter.DiscoverFooterAdapter
import com.sll.mod_imageshare.ui.vm.DiscoverViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class DiscoverFragment: BaseMvvmFragment<IsFragmentDiscoverBinding, DiscoverViewModel>(), FragmentScrollable{

    companion object {
        private const val TAG = "DiscoverFragment"
    }

    private lateinit var pagingAdapter: DiscoverAdapter

    override fun scrollToTop() {

    }

    override fun scrollTo(x: Int, y: Int) {
        // not support
    }

    override fun scrollBy(dx: Int, dy: Int) {
        // not support
    }

    override fun checkReachTop(): Boolean {
        return false
    }

    override fun onDefCreateView() {
        initRecyclerView()
    }

    override fun initViewBinding(container: ViewGroup?) = IsFragmentDiscoverBinding.inflate(layoutInflater)

    override fun getViewModelClass() = DiscoverViewModel::class

    private fun initRecyclerView() {
        launchOnCreated {
            // 获取数据源
            pagingAdapter = DiscoverAdapter(requireContext(), requireActivity().lifecycleScope).apply {
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