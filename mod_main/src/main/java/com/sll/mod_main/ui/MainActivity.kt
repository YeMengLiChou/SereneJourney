package com.sll.mod_main.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import android.transition.Slide
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.sll.lib_common.entity.dto.User
import com.sll.lib_common.interfaces.FragmentScrollable
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_common.setAvatar
import com.sll.lib_common.setLocalBackground
import com.sll.lib_common.setRemoteBackground
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.launchIO
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.res.drawable
import com.sll.lib_framework.ext.res.filter
import com.sll.lib_framework.ext.res.tint
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.height
import com.sll.lib_framework.ext.view.longClick
import com.sll.lib_framework.ext.view.marginWidth
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.setClipViewCornerTopRadius
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.manager.AppManager
import com.sll.lib_framework.util.FileUtils
import com.sll.lib_framework.util.StatusBarUtils
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_main.R
import com.sll.mod_main.TestActivity
import com.sll.mod_main.databinding.MainActivityMainBinding
import com.sll.mod_main.databinding.MainLayoutDrawerBinding
import com.sll.mod_main.databinding.MainLayoutDrawerHeaderBinding
import com.therouter.TheRouter
import com.therouter.router.matchRouteMap
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
class MainActivity : BaseMvvmActivity<MainActivityMainBinding, MainViewModel>() {

    private val tabTexts = listOf("发现", "关注")

    private val tabIcons = listOf(
        R.drawable.main_ic_discover,
        R.drawable.main_ic_focus,
    )

    // TODO: 保存 fragments 的状态
    private val fragments = mutableListOf<Fragment>()

    // drawer的布局
    private lateinit var headerBinding: MainLayoutDrawerHeaderBinding

    private lateinit var footerBinding: MainLayoutDrawerBinding

    // drawer 部分的菜单
    private lateinit var headerPopupMenu: PopupMenu


    // =========================== override ======================

    override fun onDefCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        fragments.add(ServiceManager.isService.navigateDiscoverFragment(binding.mainFragmentContainerView.id))
        fragments.add(ServiceManager.isService.navigateFocusFragment(binding.mainFragmentContainerView.id))
        initMainUI()
        initDrawerUI()
        fitSystemBar()
    }


    override fun initViewBinding(container: ViewGroup?): MainActivityMainBinding =
        MainActivityMainBinding.inflate(layoutInflater)

    override fun getViewModelClass(): KClass<MainViewModel> = MainViewModel::class

    // =========================== private ======================

    // 当存在系统栏挡住界面时，需要调整位置
    private fun fitSystemBar() {
        SystemBarUtils.immersiveStatusBar(this)
        SystemBarUtils.immersiveNavigationBar(this)
        val statusBarHeight = SystemBarUtils.getStatusBarHeight(this, true)
        val navigationBarHeight = SystemBarUtils.getNavigationBarHeight(this, true)

        // toolbar 防止重叠
        binding.toolbar.y += statusBarHeight
        // 适配高度
        binding.spaceBar.height(statusBarHeight)
        // 防止与导航栏重叠
        binding.root.bottom -= navigationBarHeight
        // 防止与状态栏重
        headerBinding.mainIvMore.y += AppManager.statusBarHeight
    }

    // 初始化主布局
    private fun initMainUI() {
        initToolbar()
        initViewPager2()
        initTabLayout()
        initTopBar()
        initButton()
    }

    // 初始化抽屉布局
    private fun initDrawerUI() {
        // drawer 上面部分的 header
        val header = binding.includeDrawer.mainNavigateViewDrawer.getHeaderView(0)
        headerBinding = MainLayoutDrawerHeaderBinding.bind(header)
        footerBinding = binding.includeDrawer
        initDrawerView()
        initDrawerMenu()
        initDrawerData()
    }

    // Toolbar
    private fun initToolbar() {
        // toolbar 和 drawerLayout 联动
        val toggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            0, 0
        )
        binding.root.addDrawerListener(toggle)
        toggle.syncState()

        binding.toolbar.inflateMenu(R.menu.main_menu_toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_item_switch_cover -> {
                    // TODO 切换
                }

                R.id.main_item_download -> {
                    // TODO 下载
                }

                R.id.main_item_custom -> {
                    selectMainCustomBackground() // 自定义配图
                }

                R.id.main_item_suggestion -> {
                    // TODO 建议
                }
            }
            return@setOnMenuItemClickListener true
        }
        binding.toolbar.menu.findItem(R.id.main_item_download)?.isEnabled = ServiceManager.settingService.getIsMainBackgroundFromRemote()

        // 长按背景图打开溢出菜单
        binding.ivToolbarBackground.longClick {
            if (!binding.toolbar.isOverflowMenuShowing) {
                binding.toolbar.showOverflowMenu()
                return@longClick true
            }
            return@longClick false
        }
        // 如果是从网络加载
        if (ServiceManager.settingService.getIsMainBackgroundFromRemote()) {
            // TODO 网络加载
            binding.ivToolbarBackground.setRemoteBackground(ServiceManager.settingService.getMainBackgroundPath())
        } else {
            binding.ivToolbarBackground.setLocalBackground(ServiceManager.settingService.getMainBackgroundPath())
        }
    }

    // 顶部背景
    private fun initTopBar() {
        // 状态栏模式白字黑底
        var statusBarWhite = true
        // 状态栏切换临界值
        val stateBarThreshold = 0.5

        binding.appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            appBarLayout?.apply {
                // appBarLayout 底部和 屏幕顶端的距离
                val distance = totalScrollRange - abs(verticalOffset)
                // distance 比例：1 表示完全展开， 0 表示完全收缩
                val rate = distance * 1f / totalScrollRange
                // 背景图的变化，逐渐透明 和 视差
                binding.ivToolbarBackground.apply {
                    alpha = rate
                    translationY = totalScrollRange.shr(1) * (rate - 1)
                }
                // tabLayout 向右位移给出现的图标腾出位置
                val offset = binding.btDrawerMenu.width * (1 - rate)
                binding.tabLayout.apply {
                    translationX = offset
                    setPadding(0, 0, offset.toInt(), 0) // TabLayout 整体已经超出布局，后面的 tab 无法看到，因此需要对应的 paddingEnd
                }

                // 图标出现
                binding.btDrawerMenu.alpha = 1 - rate

                // 切换为 黑字
                if (statusBarWhite && rate >= stateBarThreshold) {
                    StatusBarUtils.setStatusBarDarkMode(this@MainActivity)
                    statusBarWhite = false // TODO: bug 无法切换
                }
                // 切换为 白字
                if (!statusBarWhite && rate < stateBarThreshold) {
                    StatusBarUtils.setStatusBarLightMode(this@MainActivity)
                    statusBarWhite = true
                }
            }
        }

    }

    // tabLoayout 和 viewpager 初始化
    private fun initTabLayout() {
        // 监听tab选中，设置选中tab样式以及调整fab的位置
        binding.tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: Tab?) {
                    tab?.let {
                        setTabsStyle(it, true)
                        if (checkReachTop(it.position)) {
                            binding.fabBackToTop.hide()
                        } else {
                            binding.fabBackToTop.show()
                        }
                    }
                }

                override fun onTabUnselected(tab: Tab?) {
                    tab?.let { setTabsStyle(it, false) }
                }

                override fun onTabReselected(tab: Tab?) {
                    tab?.let { setTabsStyle(it, true) }
                }
            })
        }
        // tabLayout 和 viewPager2 联动
        TabLayoutMediator(binding.tabLayout, binding.viewpager2Content) { tab: Tab, position: Int ->
            tab.apply {
                setCustomView(R.layout.main_tab_icon_title)
                    .apply {
                        customView?.findViewById<TextView>(R.id.tv_title)?.apply {
                            text = tabTexts[position]
                            // custom style for this view
                        }
                        customView?.findViewById<ImageView>(R.id.iv_icon)?.apply {
                            setImageDrawable(this@MainActivity.drawable(tabIcons[position]))
                        }
                    }
            }
        }.attach()
    }

    // viewPager的设置
    private fun initViewPager2() {
        binding.viewpager2Content.offscreenPageLimit = 2
        binding.viewpager2Content.apply {
            adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount(): Int = fragments.size

                override fun createFragment(position: Int): Fragment = fragments[position]
            }
        }
    }

    // 初始化 button 的点击事件
    private fun initButton() {
        // 点击打开抽屉
        binding.btDrawerMenu.apply {
            throttleClick {
                binding.root.open()
            }
        }
        // 点击返回最顶上
        binding.fabBackToTop.throttleClick {
            // 调用 fragment 中的方法，
            val position = binding.viewpager2Content.currentItem
            fragments[position]
                .takeIf { it is FragmentScrollable }
                ?.As<FragmentScrollable>()
                ?.scrollToTop()

            if (binding.appbarLayout.isLiftOnScroll) {
                binding.appbarLayout.stopNestedScroll()
            }
            binding.appbarLayout.setExpanded(true, true)
        }
        // TODO: 清除测试信息
        //跳转到EditActivity
        binding.fabEditShare.throttleClick {
//            startActivity(Intent(this, TestActivity::class.java))

            Log.d("theRouter","dsfs")
            var a = matchRouteMap("/app/edit")
            Log.d("theRouter",a.toString())
            TheRouter.build("/app/edit").navigation(this)
        }
    }

    // 当 Tab 选中 / 未选中时的样式设置
    private fun setTabsStyle(tab: Tab, selected: Boolean) {
        tab.apply {
            if (selected) {
                customView?.apply {
                    // 图标选中颜色
                    findViewById<ImageView>(R.id.iv_icon).apply {
                        drawable.tint(this@MainActivity.color(R.color.main_blue_cornflower))
                    }
                    // 标题选中颜色
                    findViewById<TextView>(R.id.tv_title).apply {
                        setTextColor(this@MainActivity.color(R.color.main_blue_cornflower))
                    }
                    // 左右抖动动画
                    if (this.animation == null) {
                        this.animation = RotateAnimation(-1f, 1f, (this.width / 2).toFloat(), this.height.toFloat()).apply {
                            duration = 295
                            interpolator = CycleInterpolator(3F)
                        }
                    }
                    this.startAnimation(this.animation)
                }
            } else {
                customView?.apply {
                    findViewById<ImageView>(R.id.iv_icon).apply {
                        drawable.tint(this@MainActivity.color(R.color.main_gray_dim))
                        this.animation?.cancel()
                    }
                    findViewById<TextView>(R.id.tv_title).apply {
                        setTextColor(this@MainActivity.color(R.color.main_gray_dim))
                    }
                }
            }
        }
    }

    // 检查当前 AppLayout 和 Fragment  是否已经到顶
    private fun checkReachTop(position: Int): Boolean {
        val top =
            (fragments[position]
                .takeIf { it is FragmentScrollable }
                ?.As<FragmentScrollable>()
                ?.checkReachTop()
                ?: false) // 不可滑动默认为到顶
                .and(binding.appbarLayout.isExpanded)
        return top
    }

    // 设置抽屉布局
    private fun initDrawerView() {
        // 让图片变暗，同时慢慢放大缩小的动画
        headerBinding.mainIvBackground.apply {
            drawable.filter("#D0D0D0")
            post {
                val height = this.height.toFloat()
                val width = this.width.toFloat()
                this.startAnimation(
                    ScaleAnimation(
                        1.0f, 1.5f,
                        1.0f, 1.5f,
                        height / 2, width / 2
                    ).apply {
                        duration = 15000
                        repeatCount = -1
                        repeatMode = Animation.REVERSE
                        interpolator = FastOutSlowInInterpolator()
                    }
                )

            }
        }

        // 头像处理，处理为圆形
        headerBinding.mainIvAvatar.apply {
            post {
                setClipViewCornerRadius(this.width / 2)
            }
        }
        // 顶部圆角裁剪
        headerBinding.mainConstraintLayoutContent.apply {
            post {
                setClipViewCornerTopRadius(abs(this.y - headerBinding.mainIvBackground.height).toInt())
            }
        }

        headerBinding.mainIvMore.apply {
            throttleClick {
                headerPopupMenu.show()
            }
        }

        // 长按点击出菜单
        headerBinding.mainIvBackground.longClick {
            headerPopupMenu.show()
            return@longClick true
        }

        // 设置图标
        headerBinding.mainTvUserCentral.apply {
            post {
                val color = color(R.color.main_gray_dim)
                val drawable = drawable(R.drawable.main_ic_right_arrow)?.let {
                    it.tint(color)
                    it.setBounds(0, 0, this.height, this.height)
                    it
                }
                this.compoundDrawablePadding = 2.dp
                this.setCompoundDrawables(null, null, drawable, null)
            }
        }

        headerBinding.mainConstraintLayoutUserInfo.click {
            if (ServiceManager.loginService.isLogin()) {
                // 登录了跳转到个人信息
                navigateToUserInfo()
            } else {
                // 没有登录，跳转到登陆界面
                navigateToLogin()
            }
        }

        // 如果是从网络加载
        if (ServiceManager.settingService.getIsDrawerBackgroundFromRemote()) {
            // TODO 网络加载
            headerBinding.mainIvBackground.setRemoteBackground(ServiceManager.settingService.getDrawerBackgroundPath())
        } else {
            headerBinding.mainIvBackground.setLocalBackground(ServiceManager.settingService.getDrawerBackgroundPath())
        }

        // ----------------------- footer --------------------
        footerBinding.mainTvSettings.apply {
            click { ServiceManager.settingService.navigate() }
        }

        // TODO： 切换日渐夜间模式
        footerBinding.mainTvToggleMode.apply {

        }

        footerBinding.mainTvSkin.apply {

        }

    }

    private fun initDrawerMenu() {
        // 弹窗菜单
        headerPopupMenu = PopupMenu(this, headerBinding.mainIvMore).apply {
            menuInflater.inflate(R.menu.main_menu_drawer, menu)
            setOnMenuItemClickListener {
                val consumed: Boolean =
                    when (it.itemId) {
                        R.id.main_item_download_background -> {
                            // TODO 下载背景图
                            true
                        }

                        R.id.main_item_switch_background -> {
                            // TODO 切换背景图
                            true
                        }

                        R.id.main_item_custom_background -> {
                            selectDrawerCustomBackground()
                            true
                        }
                        // 切换配文
                        R.id.main_item_switch_caption -> {
                            viewModel.getCaption()
                            true
                        }
                        // 复制配文
                        R.id.main_item_copy_caption -> {
                            copyCaption()
                            true
                        }

                        else -> false
                    }
                return@setOnMenuItemClickListener consumed
            }
        }
        // 如果是本地设置的图片，那么就需要禁用下载，默认是远程加载
        headerPopupMenu.menu.findItem(R.id.main_item_download_background)?.isEnabled = ServiceManager.settingService.getIsDrawerBackgroundFromRemote()
    }


    // 初始化抽屉数据
    private fun initDrawerData() {
        // 日期初始化
        updateCurrentDateInfo()
        // 获取配文
        viewModel.getCaption()
        // 点击切换配文
        headerBinding.mainTvCaption.throttleClick(2000) {
            viewModel.getCaption()
        }


        // 将 viewModel更新到界面上
        lifecycleScope.launch {
            launchIO {
                val loginState = ServiceManager.loginService.checkLogin()
                if (!loginState) {
                    // 登录失效，重新登录
                    if (ServiceManager.loginService.isExpired()) {
                        ToastUtils.warn("登录失效")
                        navigateToLogin()
                    }
                }
            }

            launchOnCreated {
                viewModel.caption.collect {
                    headerBinding.mainTvCaption.text = it
                }
            }

            // 登录信息
            launchOnCreated {
                viewModel.userInfo.collect { user ->
                    // 更新登录信息，为空则清除数据
                    user?.let { updateUserInfoUI(it) } ?: clearUserInfoUI()
                }
            }
        }

    }

    /**
     * 更新当前的日期，星期，月份
     *
     * */
    private fun updateCurrentDateInfo() {
        val calendar = Calendar.getInstance()
        // 日期
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val day = dateFormat.format(calendar.time)
        // 月份
        val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val month = monthFormat.format(calendar.time)
        // 星期
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val week = dayOfWeekFormat.format(calendar.time)

        headerBinding.mainTvDateDay.text = day
        headerBinding.mainTvDateWeek.text = week
        headerBinding.mainTvDateMonth.text = month
    }

    /**
     * 将配文复制到剪贴板
     * */
    private fun copyCaption() {
        val caption = headerBinding.mainTvCaption.text.As<String>()
        if (!caption.isNullOrEmpty() && caption == MainViewModel.TIPS_NETWORK_ERROR_CLICK) return
        val clipboardManager = this.getSystemService(ClipboardManager::class.java)
        val clip: ClipData = ClipData.newPlainText("caption", caption)
        clipboardManager.setPrimaryClip(clip)
        ToastUtils.success("复制成功")
    }

    /**
     * 自定义抽屉背景
     * */
    private fun selectDrawerCustomBackground() {
        val width = headerBinding.mainIvBackground.width
        val height = headerBinding.mainIvBackground.height
        FileUtils.PhotoRequest
            .with(this).pick()
            .crop(FileUtils.PhotoRequest.CropOptions().aspect(width, height).scale().ouput(width, height))
            .build { uri, state ->
                if (state == FileUtils.PhotoRequest.State.SUCCESS && uri != null) {
                    headerBinding.mainIvBackground.setLocalBackground(uri)
                    ServiceManager.settingService.apply {
                        setDrawerBackgroundPath(uri.toFile().path)
                        setDrawerBackgroundLastUpdateTime(System.currentTimeMillis())
                        setIsDrawerBackgroundFromRemote(false)
                    }
                } else if (state == FileUtils.PhotoRequest.State.CANCEL_SELECT) {
                    ToastUtils.error("选择取消")
                }
            }
    }

    /**
     * 自定义主页背景
     * */
    private fun selectMainCustomBackground() {
        val width = binding.ivToolbarBackground.width
        val height = binding.ivToolbarBackground.height
        FileUtils.PhotoRequest
            .with(this).pick()
            .crop(FileUtils.PhotoRequest.CropOptions().aspect(width, height).scale().ouput(width, height))
            .build { uri, state ->
                if (state == FileUtils.PhotoRequest.State.SUCCESS && uri != null) {
                    binding.ivToolbarBackground.setLocalBackground(uri)
                    ServiceManager.settingService.apply {
                        setMainBackgroundPath(uri.toFile().path)
                        setMainBackgroundLastUpdateTime(System.currentTimeMillis())
                        setIsMainBackgroundFromRemote(false)
                    }
                } else {
                    ToastUtils.error("选择出错")
                }
            }
    }


    /**
     * 清除登录用户的信息
     * */
    private fun clearUserInfoUI() {
        // TODO 清除其他用户信息
        headerBinding.mainTvUsername.gone()
        headerBinding.mainTvIntroduce.gone()
        headerBinding.mainTvCreateTime.gone()
        headerBinding.mainTvLoginTips.apply {
            visible()
            text = context.getString(R.string.main_login_register_first)
        }
    }

    /**
     * 更新登录界面的信息
     * */
    private fun updateUserInfoUI(user: User) {
        // TODO 更新其他信息
        headerBinding.mainTvUsername.apply {
            // TODO 判断男女，在后面加一个图标
            text = user.username
            val icon = if (user.sex == null) {
//                drawable(R.drawable.ma)
                1
            } else {
                0
            }
            visible()
        }

        headerBinding.mainIvAvatar.apply {
            setAvatar(user.avatar)
        }

        headerBinding.mainTvCreateTime.apply {
            val dis = abs(user.createTime - System.currentTimeMillis())
            val days = dis / (24 * 60 * 60 * 1000)
            text = context.getString(R.string.main_welcome_days, days.toInt().toString())
        }

        headerBinding.mainTvIntroduce.apply {
            text = user.introduce.takeIf { !it.isNullOrBlank() } ?: context.getString(R.string.main_user_no_introduce)
            visible()
        }
        // 更新头像
        headerBinding.mainIvAvatar
        headerBinding.mainTvLoginTips.gone()
    }


    /**
     * 跳转到 登录界面 [LoginActivity][com.sll.mod_login.ui.LoginActivity]
     * */
    private fun navigateToLogin() {
        val intent = ServiceManager.loginService.navigate(this)
        window.exitTransition = Slide(Gravity.BOTTOM)
        window.enterTransition = Fade()
        startActivity(intent)

        // TODO: 动画跳转
    }

    private fun navigateToUserInfo() {
        val intent = ServiceManager.userService.navigate(this)

        window.exitTransition = Slide(Gravity.START)
        window.enterTransition = Explode()

        ViewCompat.setTransitionName(headerBinding.mainIvAvatar, "avatar")
        ViewCompat.setTransitionName(headerBinding.mainTvUsername, "username")

        val avatarPair = androidx.core.util.Pair.create((headerBinding.mainIvAvatar as View), ViewCompat.getTransitionName(headerBinding.mainIvAvatar) ?: "avatar")
        val usernamePair = androidx.core.util.Pair.create((headerBinding.mainTvUsername as View), ViewCompat.getTransitionName(headerBinding.mainTvUsername) ?: "username")

        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, avatarPair, usernamePair)
        startActivity(intent, activityOptionsCompat.toBundle())
    }


}