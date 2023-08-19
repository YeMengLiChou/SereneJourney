package com.sll.mod_main.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sll.lib_framework.base.adapter.BaseRecyclerAdapter
import com.sll.lib_framework.base.fragment.BaseBindingFragment
import com.sll.lib_framework.base.viewholder.BaseBindViewHolder
import com.sll.lib_framework.base.viewholder.BaseViewHolder
import com.sll.lib_framework.ext.uncheckAs
import com.sll.lib_framework.ext.view.ScrollDistance
import com.sll.lib_framework.ext.view.checkReachTop
import com.sll.lib_framework.ext.view.scrollDistance
import com.sll.lib_framework.util.LogUtils
import com.sll.mod_main.databinding.MainFragmentTestBinding
import com.sll.mod_main.databinding.MainTabIconTitleBinding
import com.sll.mod_main.ui.interfaces.FragmentScrollable
import kotlin.properties.Delegates

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/07
 */
class TestFragment: BaseBindingFragment<MainFragmentTestBinding>(), FragmentScrollable {

    private var position : Int = 0

    private val distance = ScrollDistance()

    companion object {
        private val TAG = TestFragment::class.simpleName
    }

    override fun onDefCreateView() {
        binding.tvTest.text = position.toString()
        binding.recyclerViewTest.layoutManager = LinearLayoutManager(requireContext()).apply { orientation = LinearLayoutManager.VERTICAL }
        val list = mutableListOf<String>()
        repeat(100) {
            list.add(position.toString())
        }
        binding.recyclerViewTest.adapter = object : BaseRecyclerAdapter<MainTabIconTitleBinding, String>(requireContext(), list) {
            override fun bindData(holder: BaseViewHolder, item: String, position: Int) {
                holder.uncheckAs<BaseBindViewHolder<MainTabIconTitleBinding>>().apply {
                    binding.tvTitle.text = getItem(position)
                }
            }

            override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): MainTabIconTitleBinding {
                return MainTabIconTitleBinding.inflate(layoutInflater, parent, false)
            }
        }
    }

    override fun initViewBinding(container: ViewGroup?): MainFragmentTestBinding {
        return MainFragmentTestBinding.inflate(requireActivity().layoutInflater, container, false)
    }

    fun setPosition(position: Int): TestFragment {
        this.position = position
        return this
    }

    override fun scrollToTop() {
        binding.recyclerViewTest.stopScroll()
        binding.recyclerViewTest.smoothScrollToPosition(0)
    }

    override fun scrollTo(x: Int, y: Int) {
    }

    override fun checkReachTop(): Boolean {
        return checkAttachedActivity() && binding.recyclerViewTest.checkReachTop()
    }

    override fun scrollBy(dx: Int, dy: Int) {

    }
}