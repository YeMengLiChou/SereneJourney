package com.sll.mod_detail.ui.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sll.lib_common.constant.PATH_DETAIL_ACTIVITY_DETAIL
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.launchOnStarted
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.divider
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.height
import com.sll.lib_framework.ext.view.locationOnScreen
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_detail.R
import com.sll.mod_detail.adapter.DetailFirstCommentAdapter
import com.sll.mod_detail.adapter.DetailImageAdapter
import com.sll.mod_detail.adapter.FooterAdapter
import com.sll.mod_detail.databinding.DetailActivityDetailBinding
import com.sll.mod_detail.databinding.DetailLayoutBottomBarBinding
import com.sll.mod_detail.ui.fragment.CommentBottomFragment
import com.sll.mod_detail.ui.vm.DetailViewModel
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/12
 */
@Route(path = PATH_DETAIL_ACTIVITY_DETAIL)
class DetailActivity : BaseMvvmActivity<DetailActivityDetailBinding, DetailViewModel>() {
    companion object {
        private const val TAG = "DetailActivity"

        private const val KEY_IMAGE_SHARE = "image_share"

        private const val MESSAGE_FOCUS = 1

        private const val MESSAGE_CANCEL_FOCUS = 2

        private const val MESSAGE_LIKE = 3

        private const val MESSAGE_CANCEL_LIKE = 4

        private const val MESSAGE_COLLECT = 5

        private const val MESSAGE_CANCEL_COLLECT = 6

        private const val DELAY_SEND_MESSAGE = 500L
    }

    /** 底部操作栏 */
    private lateinit var mBottomBarBinding: DetailLayoutBottomBarBinding

    private lateinit var mImageAdapter: DetailImageAdapter

    private lateinit var mImageGridLayoutManager: GridLayoutManager

    private lateinit var mCommentAdapter: DetailFirstCommentAdapter

    private var mCommentFragment: CommentBottomFragment? = null

    private val mApiHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
        when (it.what) {
            // 点赞
            MESSAGE_LIKE -> {
                viewModel.likeOrCancelImageShare()
            }
            // 收藏
            MESSAGE_COLLECT -> {
                viewModel.collectImageShare()
            }
            // 关注
            MESSAGE_FOCUS -> {
                viewModel.focusOrCancelUser()
            }
            else -> {
                return@Handler false
            }
        }
        return@Handler true
    }

    override fun onDefCreate(savedInstanceState: Bundle?) {
        TheRouter.inject(this)
        viewModel.setImageShare(intent?.extras?.getParcelable("ImageShare")!!)
        SystemBarUtils.immersiveStatusBar(this)
        binding.includeTopBar.root.post {
            val statusHeight = SystemBarUtils.getStatusBarHeight(this)
            binding.includeTopBar.spaceMargin.height(statusHeight)
        }

        initView()
        initData()
    }

    override fun initViewBinding(container: ViewGroup?) = DetailActivityDetailBinding.inflate(layoutInflater)

    override fun getViewModelClass(): KClass<DetailViewModel> = DetailViewModel::class

    private fun initView() {
        initTopBar()
        initContent()
        initBottomBar()
    }

    private fun initData() {
        launchOnCreated {
            viewModel.imageShare.collect {
                loadUserInfo(it)
                loadImageShareDetail(it)
            }
        }
        loadFirstLevelComments()
    }

    // 初始化顶部栏
    private fun initTopBar() {
        // 点击返回
        binding.includeTopBar.ivBack.click { finish() }
        // 顶部栏的用户信息显示控制
        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            // 当前偏移量超过标题+用户信息控件的高度，开始显示
            val visibleHeight = binding.includeUser.root.locationOnScreen.second
            val offset = abs(verticalOffset) - visibleHeight
            binding.includeTopBar.constraintLayoutPublisherInfo.apply {
                if (offset > 0) {
                    val distAlpha = offset / 20f
                    if (alpha == 1f  && distAlpha < 1f || alpha < 1f) alpha = distAlpha
                }
                if (offset <= 0 && alpha != 0f) {
                    alpha = 0f
                }
            }
        }

        // 关注点击
        binding.includeTopBar.btFocus.click {
            viewModel.userFocus = !viewModel.userFocus
            // 先移除，再发送处理的消息
            mApiHandler.removeMessages(MESSAGE_FOCUS)
            mApiHandler.sendEmptyMessageDelayed(MESSAGE_FOCUS, DELAY_SEND_MESSAGE)
        }
        launchOnCreated {
            viewModel.userFocusState.collect {
                Log.i(TAG, "initTopBar: focusState Collect: $it")
                setUserFocus(viewModel.userFocus)
            }
        }
    }


    // 初始化中间部分的
    private fun initContent() {
        binding.btFocus.click {
            viewModel.userFocus = !viewModel.userFocus
            // 先移除，再发送处理的消息
            mApiHandler.removeMessages(MESSAGE_FOCUS)
            mApiHandler.sendEmptyMessageDelayed(MESSAGE_FOCUS, DELAY_SEND_MESSAGE)
        }
    }


    // 初始化下底部栏，用于评论、点赞、收藏等操作
    private fun initBottomBar() {
        mBottomBarBinding = DetailLayoutBottomBarBinding.bind(binding.includeBottomBar.root)
        mBottomBarBinding.tvComment.click {
            // 弹出评论 fragment
            CommentBottomFragment().apply {
                mCommentFragment = this
                // 保存在 viewModel 中
                launchOnStarted {
                    // 读取文字信息
                    this@apply.commentContentFlow.collectLatest { viewModel.mAddFirstLevelCommentContent = it }
                }
            }
                .setTitle("评论分享") // 标题
                .setOnActionListener(object : CommentBottomFragment.OnCommentActionListener {
                    override fun onStart(root: View) {
                        SystemBarUtils.showIme(this@DetailActivity, root)
                    }

                    override fun onDismiss(root: View) {
                        SystemBarUtils.hideIme(this@DetailActivity, root)
                    }

                    override fun onConfirm() {
                        viewModel.addFirstLevelComment() // 确认发送评论
                    }
                })
                .setEditContent(viewModel.mAddFirstLevelCommentContent) //
                .show(supportFragmentManager, "comment")
        }
        launchOnCreated {
            viewModel.addFirstLevelCommentState.collect { res ->
                res?.onSuccess {
                    // 发送成功后退出fragment，并刷新
                    ToastUtils.success("发送成功~")
                    mCommentFragment?.dismiss()
                    mCommentFragment = null
                    mCommentAdapter.refresh()
                    viewModel.mAddFirstLevelCommentContent = ""
                }
            }
        }

    }


    /**
     * 加载用户信息
     * */
    private fun loadUserInfo(imageShare: ImageShare?) {
        imageShare?.let {
            viewModel.getUserInfoByUsername(it.username!!)
            binding.includeUser.apply {
                // 用户名
                tvUsername.text = it.username
                binding.includeTopBar.tvPublisher.text = it.username
                // 时间
                tvTime.text = it.createTime
                // 头像
                launchOnStarted {
                    viewModel.userInfoState.collect { userInfo ->
                        userInfo?.onSuccess { user ->
                            ivUserIcon.setAvatar(user?.avatar)
                            binding.includeTopBar.ivPublisherAvatar.setAvatar(user?.avatar)
                        }
                    }
                }
                setUserFocus(it.hasFocus)
            }
        } ?: run {
            // TODO 当为空时做一个默认处理
        }
    }

    /**
     *
     * 加载分享的内容
     * */
    private fun loadImageShareDetail(imageShare: ImageShare?) {
        imageShare?.let {
            binding.tvTitle.text = it.title
            binding.tvContent.text = it.content
            if (it.imageUrlList.isNotEmpty()) {
                binding.recyclerViewImages.apply {
                    visible()
                    // 计算列数
                    val urlSize = it.imageUrlList.size
                    val spanSize = when {
                        urlSize < 4 -> urlSize
                        urlSize == 4 -> 2
                        else -> 3
                    }
                    layoutManager = object : GridLayoutManager(context, spanSize, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically() = false
                    }.also { l -> mImageGridLayoutManager = l }
                    mImageAdapter = DetailImageAdapter(context, it.imageUrlList.toMutableList())
                        .also { a ->
                            post {
                                a.width = this.width
                                a.marginSpan = 8.dp
                                adapter = a
                            }
                        }
                }
            } else {
                binding.recyclerViewImages.gone()
            }

        }
    }


    /**
     * 加载该图文分享的一级评论
     *
     * */
    private fun loadFirstLevelComments() {
        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.VERTICAL }
            // 设置分割线
            divider()
            adapter = DetailFirstCommentAdapter(this@DetailActivity, supportFragmentManager) { comment, count ->
                viewModel.detailParentComment = comment
                viewModel.detailCommentCount = 0
            }.apply {
                mCommentAdapter = this
                // 底部加载栏
                withLoadStateFooter(FooterAdapter(context) {
                    this.retry() // 失败重试
                })
                // 状态栏加载
                addLoadStateListener {
                    when (it.refresh) {
                        is LoadState.Loading -> {
                            binding.refreshComments.isRefreshing = true
                        }

                        is LoadState.NotLoading -> {
                            binding.refreshComments.isRefreshing = false
                            // 当没有评论的时候，就提示无评论
                            if (mCommentAdapter.itemCount == 0) {
                                binding.recyclerViewComments.gone()
                                binding.tvErrorTips.visible()
                            } else {
                                binding.recyclerViewComments.visible()
                                binding.tvErrorTips.gone()
                            }
                        }

                        is LoadState.Error -> {
                            binding.refreshComments.isRefreshing = false
                            ToastUtils.error("加载失败:${(it.refresh as LoadState.Error).error.message}")
                        }
                    }
                }
                // 一级评论加载
                launchOnStarted {
                    viewModel.getFirstLevelComments().collect {
                        submitData(it)
                    }
                }
                launchOnStarted {
                    viewModel.detailNeedRefresh.collect {
                        if (it) {
                            mCommentAdapter.refresh()
                            mCommentAdapter.notifyItemRangeChanged(0, mCommentAdapter.itemCount)
                            viewModel.setNeedRefresh(false)
                        }
                    }
                }
            }
        }
        // 刷新
        binding.refreshComments.setOnRefreshListener {
            mCommentAdapter.refresh()
        }
    }

    /**
     * 设置用户是否已关注
     * */
    private fun setUserFocus(focus: Boolean) {
        setButtonFocusStyle(binding.btFocus, focus)
        setButtonFocusStyle(binding.includeTopBar.btFocus, focus)
    }

    /**
     * 设置Button不同关注状态的样式
     * */
    private fun setButtonFocusStyle(button: MaterialButton, focus: Boolean) {
        val preFocus: Boolean = button.tag?.As<Boolean>() ?: false
        // 关注且先前没有关注
        if (focus && !preFocus) {
            button.apply {
                tag = true
                setIconResource(R.drawable.detail_ic_checked)
                val color = color(R.color.detail_gray_dim)
                iconTint = ColorStateList.valueOf(color)
                setTextColor(color)
                text = "已关注"
                setTextColor(color(R.color.detail_white))
                backgroundTintList = ColorStateList.valueOf(color("#E0E0E0"))
            }
        }
        // 取消关注且先前已经关注
        if (!focus && preFocus){
            button.apply {
                tag = false
                setIconResource(R.drawable.detail_ic_plus)
                val color = color(R.color.detail_white)
                iconTint = ColorStateList.valueOf(color)
                text = "关注"
                setTextColor(color)
                backgroundTintList = ColorStateList.valueOf(color(R.color.detail_blue_cornflower))
            }
        }
    }


}