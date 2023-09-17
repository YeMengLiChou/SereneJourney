package com.ssl.mod_detail.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import com.ssl.mod_detail.ui.vm.DetailViewModel
import com.sll.lib_common.constant.PATH_DETAIL_ACTIVITY_DETAIL
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.ext.view.click
import com.sll.mod_detail.databinding.DetailActivityDetailBinding
import com.sll.mod_detail.databinding.DetailLayoutBottomBarBinding
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/12
 */
@Route(path = PATH_DETAIL_ACTIVITY_DETAIL)
class DetailActivity: BaseMvvmActivity<DetailActivityDetailBinding, DetailViewModel>() {
    companion object {
        private const val TAG = "DetailActivity"

    }

    private lateinit var mBottomBarBinding: DetailLayoutBottomBarBinding

    override fun onDefCreate(savedInstanceState: Bundle?) {
        TheRouter.inject(this)
        initView()
        initData()
    }

    override fun initViewBinding(container: ViewGroup?) =  DetailActivityDetailBinding.inflate(layoutInflater)

    override fun getViewModelClass(): KClass<DetailViewModel> = DetailViewModel::class

    private fun  initView() {
        initBottomBar()
    }

    private fun initData() {

    }


    // 初始化下底部栏，用于评论、点赞、收藏等操作
    private fun initBottomBar() {
        mBottomBarBinding = DetailLayoutBottomBarBinding.bind(binding.includeBottomBar.root)
        mBottomBarBinding.tvComment.click {
            // TODO: 弹出评论
        }


    }


    private fun initImageShareDetails() {

    }


    private fun initImageShareComments() {

    }

}