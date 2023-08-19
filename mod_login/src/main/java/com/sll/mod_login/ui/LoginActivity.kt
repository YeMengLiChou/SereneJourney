package com.sll.mod_login.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.addCallback
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sll.lib_common.constant.PATH_LOGIN_ACTIVITY_LOGIN
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.res.addUpdateFloatListener
import com.sll.lib_framework.ext.view.invisible
import com.sll.lib_framework.ext.view.setCurrentItem
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.ImeHeightUtils
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.viewpager.GalleryTransformer
import com.sll.mod_login.databinding.LoginActivityLoginBinding
import com.therouter.router.Route

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/09
 */
@Route(path = PATH_LOGIN_ACTIVITY_LOGIN)
class LoginActivity : BaseMvvmActivity<LoginActivityLoginBinding, LoginViewModel>() {
    companion object {
        private val TAG = LoginActivity::class.simpleName

    }

    // TODO: 保存该fragment的状态
    private val fragments = mutableListOf(
        LoginFragment(),
        RegisterFragment()
    )

    // 测量 Ime弹出后需要移动的高度
    private var mImeDeltaHeight = 0f

    override fun onDefCreate(savedInstanceState: Bundle?) {
        SystemBarUtils.immersiveStatusBar(this)
        onBackPressedDispatcher.addCallback(this, true) {
            // 如果是在注册界面，先返回到登录界面，然后再退出当前 Activity
            if (viewModel.curItemPosition.value == LoginViewModel.POSITION_REGISTER) {
                viewModel.switchToLoginFragment()
            } else {
                finish()
            }
        }
        initView()
    }


    override fun initViewBinding(container: ViewGroup?) = LoginActivityLoginBinding.inflate(layoutInflater)

    override fun getViewModelClass() = LoginViewModel::class

    private fun initView() {
        initViewPager2()
        initImeAnimation()
        initData()
    }

    // 初始化输入法的动画
    private fun initImeAnimation() {
        // 监听输入法高度
        ImeHeightUtils.registerImeListener(this) { height ->
            val up: Boolean
            val valueAnimator = if (height > 0) {
                // 需要移动到输入法上方32dp处
                val delta = height + 32.dp - (window.decorView.bottom - binding.loginConstraintLayoutContent.bottom)
                mImeDeltaHeight = if (delta <= 0) 0f else delta.toFloat()
                // 弹出还是收起 ime
                up = true
                ValueAnimator.ofFloat(0f, mImeDeltaHeight).apply {
                    addListener(
                        onStart = {
                            // 开始时下方开始可见，但是透明
                            binding.loginTvTitleBottom.visible()
                            binding.loginTvTitleBottom.alpha = 0f
                        },
                        onEnd = {
                            // 结束时上方为不可见，不能用gone，布局会出现闪动
                            binding.loginTvTitleTop.invisible()
                        }
                    )
                }
            } else {
                up = false
                ValueAnimator.ofFloat(0f, -mImeDeltaHeight).apply {
                    addListener(
                        onStart = {
                            binding.loginTvTitleTop.visible()
                            binding.loginTvTitleTop.alpha = 0f
                        },
                        onEnd = {
                            binding.loginTvTitleBottom.invisible()
                        }
                    )
                }
            }.apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }
            // 更新值
            valueAnimator.addUpdateFloatListener { _, fraction, offset ->
                binding.loginConstraintLayoutContent.y -= offset
                binding.loginTvTitleTop.alpha = if (up) 1 - fraction else fraction
                binding.loginTvTitleBottom.alpha = if (up) fraction else 1 - fraction
            }
            valueAnimator.start()
        }
    }

    private fun initViewPager2() {
        // TODO: 状态保存
        binding.loginViewPager2Content.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        binding.loginViewPager2Content.apply {
            // 禁止操作
            isUserInputEnabled = false
            offscreenPageLimit = 1
            setPageTransformer(GalleryTransformer())
        }
        // 监听切换
        launchOnCreated {
            viewModel.curItemPosition.collect {
                if (it != binding.loginViewPager2Content.currentItem) {
                    binding.loginViewPager2Content.setCurrentItem(it, 300)
                }
            }
        }
    }

    private fun initData() {
        launchOnCreated {
            viewModel.loginState.collect { res ->
                res
                    ?.onSuccess {  // 登录成功时，结束当前Activity，无需其他操作
                        it?.let {
                            finish()
                            ToastUtils.success("登陆成功~")
                        }
                    }
            }
        }
    }


}