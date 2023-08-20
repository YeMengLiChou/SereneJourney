package com.sll.mod_imageshare.ui.fragment

import android.transition.Fade
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sll.lib_common.downloadOriginPictures
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_image_share.databinding.IsFragmentPreviewBinding
import com.sll.mod_imageshare.adapter.PreviewAdapter
import com.sll.mod_imageshare.ui.vm.ImageShareViewModel
import java.io.File
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/20
 */
class PreviewFragment: BaseMvvmFragment<IsFragmentPreviewBinding, ImageShareViewModel>() {
    companion object {
        private const val TAG = "PreviewFragment"

        const val KEY_PREVIEW_LIST = "list"
    }

    private var item: ImageShare? = null
    // 初始位置
    private var initPosition: Int = 0
    // 当前位置
    private var currentPosition: Int =  0

    private var itemSize = 0

    private val backCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

    }
    override fun getViewModelClass(): KClass<ImageShareViewModel> = ImageShareViewModel::class

    override fun initViewBinding(container: ViewGroup?) = IsFragmentPreviewBinding.inflate(layoutInflater)

    override fun onDefCreateView() {

        ViewCompat.setTransitionName(binding.isViewpager2Preview, "preview")
        enterTransition = Fade()
        exitTransition = Fade()

        // 返回事件分发
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)

        initViewPager2()
        initEventListeners()
    }

    fun setItemPosition(item: ImageShare, position: Int) {
        this.item = item
        this.initPosition = position
        this.currentPosition = position
        this.itemSize = item.imageUrlList.size
    }

    private fun initViewPager2() {
        // 更新数据
        val adapter = PreviewAdapter(requireContext()).apply {
            submitList(item?.imageUrlList)
        }
        binding.isViewpager2Preview.adapter = adapter
        // 如果不是当前页面，切换
        if (initPosition != binding.isViewpager2Preview.currentItem) {
            binding.isViewpager2Preview.setCurrentItem(initPosition, true)
        }
        // 下部分的页码切换
        binding.isViewpager2Preview.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                binding.isTvCount.text = "%5d/%-5d".format(position + 1, itemSize)
            }
        })
        binding.isTvCount.text =  "%5d/%-5d".format(currentPosition + 1, itemSize)
    }

    private fun initEventListeners() {
        binding.apply {
            isIvBack.click {
                backCallback.handleOnBackPressed()
            }
            // 下载图片
            isIvDownload.throttleClick(500) { view ->
                ToastUtils.success("开始下载...")
                downloadOriginPictures(requireContext(), item!!.imageUrlList[currentPosition]) { uri ->
                    uri?.let { ToastUtils.success("保存到 ${it.toFile().absolutePath}") }
                        ?: ToastUtils.error("保存失败！")
                }
            }
        }
    }

}