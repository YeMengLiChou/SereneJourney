package com.sll.mod_imageshare.ui.item

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sll.lib_common.interfaces.FragmentScrollable
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.util.debug
import com.sll.mod_image_share.databinding.IsFragmentDiscoverBinding
import com.sll.mod_imageshare.adapter.DiscoverAdapter
import com.sll.mod_imageshare.adapter.DiscoverFooterAdapter
import com.sll.mod_imageshare.ui.vm.DiscoverViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.reflect.KClass

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

    private lateinit var adapter: DiscoverAdapter

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
        initViewPager2()
    }

    override fun initViewBinding(container: ViewGroup?) = IsFragmentDiscoverBinding.inflate(layoutInflater)

    override fun getViewModelClass() = DiscoverViewModel::class

    private fun initViewPager2() {
        launchOnCreated {
            adapter = DiscoverAdapter(requireContext())
            binding.isViewpager2Content.layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
            binding.isViewpager2Content.adapter = adapter.withLoadStateFooter(DiscoverFooterAdapter(requireContext()) {
                adapter.retry()
            })
            //下拉刷新
            binding.isSwipeRefreshContent.setOnRefreshListener {
                adapter.refresh()
            }
            viewModel.fetchDiscoverImageShares().collectLatest {
                adapter.submitData(it)
            }

        }
    }


}