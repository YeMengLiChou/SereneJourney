package com.sll.mod_user.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.util.SparseArray
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout.GRAVITY_CENTER
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.sll.lib_common.constant.PATH_USER_ACTIVITY_USER
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.base.adapter.ViewPage2FragmentAdapter
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.res.drawable
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.mod_user.R
import com.sll.mod_user.databinding.UserActivityUserBinding
import com.sll.mod_user.ui.modify.ModifyFragment
import com.therouter.router.Route
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
@Route(path = PATH_USER_ACTIVITY_USER)
class UserActivity : BaseMvvmActivity<UserActivityUserBinding, UserViewModel>() {
    companion object {
        private val TAG = UserActivity::class.simpleName
    }

    private var isCollapsed = false

    private val tabTexts = listOf("发布", "关注", "点赞", "收藏", "草稿")

    private val textViews = mutableListOf<TextView>()

    private val fragments = SparseArray<Fragment>(5).apply {
        this.append(0, ServiceManager.isService.navigatePublishFragment(R.id.user_fragmentContainerView))
        this.append(1, ServiceManager.isService.navigateFocusFragment(R.id.user_fragmentContainerView))
        this.append(2, ServiceManager.isService.navigateLikeFragment(R.id.user_fragmentContainerView))
        this.append(3, ServiceManager.isService.navigateCollectFragment(R.id.user_fragmentContainerView))
        this.append(4, ServiceManager.isService.navigateDraftFragment(R.id.user_fragmentContainerView))
    }

    override fun onDefCreate(savedInstanceState: Bundle?) {
        SystemBarUtils.immersiveStatusBar(this)
        initTransition()
        initTopView()
        initViewPager2()
        initViewData()
    }

    override fun initViewBinding(container: ViewGroup?) = UserActivityUserBinding.inflate(layoutInflater)

    override fun getViewModelClass() = UserViewModel::class

    private fun initTransition() {
        ViewCompat.setTransitionName(binding.userIvAvatar, "avatar")
        ViewCompat.setTransitionName(binding.userTvUsername, "username")

        window.enterTransition = Fade()
        window.exitTransition = Fade()

        val transitionSet = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTarget(binding.userIvAvatar)
            addTarget(binding.userTvUsername)
        }
        window.sharedElementEnterTransition = transitionSet
        window.sharedElementExitTransition = transitionSet
    }

    // 上部分的 view 初始化
    private fun initTopView() {
        binding.apply {
            // 圆形裁剪
            userIvAvatar.post {
                userIvAvatar.setClipViewCornerRadius(binding.userIvAvatar.width / 2)
            }
            userIvAvatarTopBar.post {
                userIvAvatarTopBar.setClipViewCornerRadius(binding.userIvAvatarTopBar.width / 2)
            }

            userIvBack.click { onBackPressed() }
            userIvAvatar.click {
                navigateModifyFragment()
            }
            userIvAvatar.click {
                navigateModifyFragment()
            }


            // 设置滑动
            userAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val fraction = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
                // 设置
                userViewBackgroundMask.alpha = fraction
                userIvAvatarTopBar.alpha = fraction
                userTvUsernameTopBar.alpha = fraction
                userIvAvatar.alpha = 1 - fraction

                // 准备收缩
                if (!isCollapsed && fraction >= 0.9) {
                    isCollapsed = true
                    userTvUsernameTopBar.setTextColor(Color.BLACK)
                    userIvBack.setColorFilter(Color.BLACK)
                    for (tv in textViews) {
                        tv.setTextColor(Color.BLACK)
                    }
                    userTabLayout.setSelectedTabIndicatorColor(Color.BLACK)
                }
                // 准备展开
                if (isCollapsed && fraction < 0.9) {
                    isCollapsed = false
                    userTvUsernameTopBar.setTextColor(Color.WHITE)
                    userIvBack.setColorFilter(Color.WHITE)
                    for (tv in textViews) {
                        tv.setTextColor(Color.WHITE)
                    }
                    userTabLayout.setSelectedTabIndicatorColor(Color.WHITE)
                }
            }


        }
    }

    private fun initViewPager2() {
        binding.apply {
            userViewpager2Content.adapter = ViewPage2FragmentAdapter(supportFragmentManager, lifecycle, fragments)

            TabLayoutMediator(userTabLayout, userViewpager2Content) { tab, position ->
                setTabTextView(tab, position)
            }.attach()
        }
    }

    // 初始化数据
    private fun initViewData() {
        launchOnCreated {
            launch {
                viewModel.userInfo.collect {
                    if (it != null) {
                        setUserInfo(it)
                    } else {
                        clearUserInfo()
                    }
                }
            }
        }
    }

    // 加载用户信息
    private fun setUserInfo(user: User) {
        (object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.userIvAvatar.setImageDrawable(resource)
                binding.userIvAvatarTopBar.setImageDrawable(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }).setAvatar(this, user.avatar)

        binding.apply {
            userTvUsername.text = user.username
            userTvUsernameTopBar.text = user.username
        }
    }

    // 清空用户信息
    private fun clearUserInfo() {
        val defaultIcon = drawable(R.drawable.user_ic_default_avatar)
        binding.apply {
            userTvUsername.text = "未登录"
            userTvUsernameTopBar.text = ""
            userIvAvatarTopBar.setImageDrawable(defaultIcon)
            userIvAvatar.setImageDrawable(defaultIcon)
        }
    }

    private fun setTabTextView(tab: Tab, position: Int) {
        tab.customView = TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            text = tabTexts[position]
            setTextColor(color(R.color.user_white))
            gravity = GRAVITY_CENTER
            textViews.add(this)
        }
    }

    private fun navigateModifyFragment() {
        val fragment = ModifyFragment().apply {
            enterTransition = Fade(Fade.MODE_IN)
            exitTransition = Fade(Fade.MODE_OUT)
            val transition = TransitionSet().apply {
                addTransition(ChangeBounds())
                addTransition(ChangeTransform())
                addTarget(binding.userIvAvatar)
                addTarget(binding.userTvUsername)
            }
            sharedElementEnterTransition = transition
        }
        supportFragmentManager.commit {
            add(binding.userFragmentContainerView.id, fragment)
            addSharedElement(binding.userIvAvatar, "avatar")
            addSharedElement(binding.userTvUsername, "username")
            setReorderingAllowed(true)
            addToBackStack("modify")
        }
    }
}