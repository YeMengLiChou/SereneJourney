package com.sll.mod_detail.ui.activity

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
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
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_network.ext.requestResponse
import com.sll.mod_detail.R
import com.sll.mod_detail.adapter.DetailFirstCommentAdapter
import com.sll.mod_detail.adapter.DetailImageAdapter
import com.sll.mod_detail.adapter.FooterAdapter
import com.sll.mod_detail.databinding.DetailActivityDetailBinding
import com.sll.mod_detail.databinding.DetailLayoutBottomBarBinding
import com.sll.mod_detail.repository.DetailRepository
import com.sll.mod_detail.ui.fragment.CommentBottomFragment
import com.sll.mod_detail.ui.vm.DetailViewModel
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/12
 */
@Route(path = PATH_DETAIL_ACTIVITY_DETAIL)
class DetailActivity : BaseMvvmActivity<DetailActivityDetailBinding, DetailViewModel>() {
    companion object {
        private const val TAG = "DetailActivity"

        const val KEY_IMAGE_SHARE = "image_share"

        const val FLAG_SCROLL_TO_COMMENT = "comment"

        private const val MESSAGE_FOCUS = 1

        private const val MESSAGE_LIKE = 2

        private const val MESSAGE_COLLECT = 3

        private const val DELAY_SEND_MESSAGE = 300L
    }

    /** 底部操作栏 */
    private lateinit var mBottomBarBinding: DetailLayoutBottomBarBinding

    private lateinit var mImageAdapter: DetailImageAdapter

    private lateinit var mImageGridLayoutManager: GridLayoutManager

    private lateinit var mCommentAdapter: DetailFirstCommentAdapter

    private var mCommentFragment: CommentBottomFragment? = null

    private var mScrollToComment = false

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

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
        // 状态栏
        SystemBarUtils.immersiveStatusBar(this)
        binding.includeTopBar.root.post {
            val statusHeight = SystemBarUtils.getStatusBarHeight(this)
            binding.includeTopBar.spaceMargin.height(statusHeight)
        }

        viewModel.setImageShare(intent?.extras?.getParcelable(KEY_IMAGE_SHARE)!!)
        intent?.extras?.getBoolean(FLAG_SCROLL_TO_COMMENT, false)?.let {

        }

        initView()
        initData()
    }

    override fun onResume() {
        super.onResume()
        if (mScrollToComment) {
            mScrollToComment = false
            binding.appBarLayout.apply {
                post {
                    val behavior = this.layoutParams.As<CoordinatorLayout.LayoutParams>()?.behavior as AppBarLayout.Behavior
                    ValueAnimator.ofInt(0, -10000).apply {
                        addUpdateListener {
                            behavior.topAndBottomOffset = it.animatedValue as Int
                            requestLayout()
                        }
                        duration = 200
                        start()
                    }
                }
            }
        }
    }


    override fun initViewBinding(container: ViewGroup?) = DetailActivityDetailBinding.inflate(layoutInflater)

    override fun getViewModelClass(): KClass<DetailViewModel> = DetailViewModel::class


    /**
     * 解析时间戳并格式化为年月日时分的字符串
     *
     * @param timestamp 时间戳
     * @return 格式化后的时间字符串，例如：2023-07-19 14:30
     */
    private fun parseTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return dateFormat.format(date)
    }

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
                    if (alpha == 1f && distAlpha < 1f || alpha < 1f) alpha = distAlpha
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


        launchOnCreated {
            viewModel.imageShare.collect {
                it?.let { imageShare ->
                    mBottomBarBinding.includeLike.apply {
                        tvText.text = (imageShare.likeNum ?: "点赞").toString()
                        ivIcon.setImageResource(if (imageShare.hasLike) R.drawable.detail_ic_like_fill else R.drawable.detail_ic_like)
                    }

                    mBottomBarBinding.includeCollect.apply {
                        tvText.minWidth = 40.dp
                        if (imageShare.hasCollect) {
                            ivIcon.setImageResource(R.drawable.detail_ic_collect_fill)
                            tvText.text = ""
                        } else {
                            ivIcon.setImageResource(R.drawable.detail_ic_collect)
                            tvText.text = "收藏"
                        }
                    }
                }

            }
        }
        // 点赞
        val item = viewModel.imageShare.value
        if (item != null) {
            // 点赞
            mBottomBarBinding.includeLike.apply {
                setLikeStyle(item.hasLike, item.likeNum ?: 0, false)
                root.throttleClick(300L) {
                    val preLike = root.getTag(R.id.detail_tag_like)?.As<Boolean>() ?: false
                    val preLikeNum = item.likeNum ?: 0
                    var curLikeNum: Int

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (preLike) {
                            curLikeNum = preLikeNum - 1
                            item.likeNum = curLikeNum
                            setLikeStyle(false, likeCount = curLikeNum, false)
                            if (item.likeId == null) {
                                setLikeStyle(false, likeCount = curLikeNum, false)
                            } else {
                                viewModel.requestResponse {
                                    DetailRepository.cancelLikeImageShare(item.likeId!!)
                                }.collect { res ->
                                    res.onSuccess {
                                        // 同步状态
                                        setLikeStyle(false, likeCount = curLikeNum, false)
                                    }.onError {
                                        ToastUtils.error("取消点赞失败，请检查网络~")
                                        // 恢复原状
                                        setLikeStyle(preLike, preLikeNum, false)
                                        item.likeNum = preLikeNum
                                    }
                                }
                            }
                        } else { // 点赞
                            curLikeNum = preLikeNum + 1
                            item.likeNum = curLikeNum
                            setLikeStyle(true, curLikeNum, true)
                            viewModel.requestResponse {
                                DetailRepository.likeImageShare(item.id)
                            }.collect { res ->
                                res.onSuccess {
                                    setLikeStyle(true, curLikeNum, false)
                                    // 更新 likeId
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        viewModel.updateImageShare(item.id)
                                    }
                                }.onError {
                                    ToastUtils.error("点赞失败，请检查网络~")
                                    setLikeStyle(preLike, preLikeNum, false)
                                    item.likeNum = preLikeNum
                                }
                            }
                        }
                    }
                }
            }

            // 收藏
            mBottomBarBinding.includeCollect.apply {
                root.throttleClick(300L) {
                    val preCollect = root.getTag(R.id.detail_tag_collect)?.As<Boolean>() ?: false
                    val preCollectNum = item.collectNum ?: 0
                    var curCollectNum: Int

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (preCollect) {
                            curCollectNum = preCollectNum - 1
                            item.collectNum = curCollectNum
                            setCollectStyle(false, collectNum = curCollectNum, false)
                            if (item.collectId == null) {
                                setCollectStyle(false, collectNum = curCollectNum, false)
                            } else {
                                viewModel.requestResponse {
                                    DetailRepository.cancelCollectImageShare(item.collectId!!)
                                }.collect { res ->
                                    res.onSuccess {
                                        // 同步状态
                                        setCollectStyle(false, collectNum = curCollectNum, false)
                                    }.onError {
                                        ToastUtils.error("取消点赞失败，请检查网络~")
                                        // 恢复原状
                                        setCollectStyle(preCollect, preCollectNum, false)
                                        item.collectNum = preCollectNum
                                    }
                                }
                            }
                        } else { // 点赞
                            curCollectNum = preCollectNum + 1
                            item.collectNum= curCollectNum
                            setCollectStyle(true, curCollectNum, true)
                            viewModel.requestResponse {
                                DetailRepository.collectImageShare(item.id)
                            }.collect { res ->
                                res.onSuccess {
                                    setCollectStyle(true, curCollectNum, false)
                                    // 更新 collectId
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        viewModel.updateImageShare(item.id)
                                    }
                                }.onError {
                                    ToastUtils.error("点赞失败，请检查网络~")
                                    setCollectStyle(preCollect, preCollectNum, false)
                                    item.collectNum = preCollectNum
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    /**
     * 加载用户信息
     * */
    private fun loadUserInfo(imageShare: ImageShare?) {
        imageShare?.let {
            setUserFocus(it.hasFocus)
            viewModel.getUserInfoByUsername(it.username!!)
            binding.includeUser.apply {
                // 用户名
                tvUsername.text = it.username
                binding.includeTopBar.tvPublisher.text = it.username
                // 时间
                tvTime.text = parseTimestamp(it.createTime.toLong())
                // 头像
                launchOnStarted {
                    viewModel.userInfoState.collect { userInfo ->
                        userInfo?.onSuccess { user ->
                            ivUserIcon.setAvatar(user?.avatar)
                            binding.includeTopBar.ivPublisherAvatar.setAvatar(user?.avatar)
                        }
                    }
                }
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
    private fun setButtonFocusStyle(button: MaterialButton, focused: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            button.apply {
                val preFocus = getTag(R.id.detail_tag_focus)?.As<Boolean>()
                // 没有点赞且需要点赞
                if ((preFocus == null || !preFocus) && focused) {
                    setTag(R.id.detail_tag_focus, true)
                    setIconResource(0)
                    text = "已关注"
                    setTextColor(context.color(R.color.detail_blue_cornflower))
                    backgroundTintList = ColorStateList.valueOf(context.color(R.color.detail_blue_alice))
                }
                if ((preFocus == null || preFocus) && !focused) {
                    setIconResource(R.drawable.detail_ic_plus)
                    setTag(R.id.detail_tag_focus, false)
                    text = "关注"
                    setTextColor(context.color(R.color.detail_white))
                    backgroundTintList = ColorStateList.valueOf(context.color(R.color.detail_blue_cornflower))
                }
            }
        }
    }

    private fun setLikeStyle(liked: Boolean, likeCount: Int, animated: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            mBottomBarBinding.includeLike.apply {
                val preLiked = root.getTag(R.id.detail_tag_like)?.As<Boolean>()
                // 初始化 TAG
                if (preLiked == null) {
                    if (liked) {
                        ivIcon.setImageResource(R.drawable.detail_ic_like_fill)
                        root.setTag(R.id.detail_tag_like, true)
                    } else {
                        ivIcon.setImageResource(R.drawable.detail_ic_like)
                        root.setTag(R.id.detail_tag_like, false)
                    }
                    tvText.text = if (likeCount == 0) "赞" else likeCount.toString()
                } else {
                    // 没有点赞且需要点赞
                    if (!preLiked && liked) {
                        if (animated) {
                            startActionAnimator(mBottomBarBinding.includeLike.ivIcon) {
                                mBottomBarBinding.includeLike.ivIcon.setImageResource(R.drawable.detail_ic_like_fill)
                            }
                        } else {
                            ivIcon.setImageResource(R.drawable.detail_ic_like_fill)
                        }
                        root.setTag(R.id.detail_tag_like, liked)
                        tvText.text = likeCount.toString()
                    }
                    if (preLiked && !liked) {
                        root.setTag(R.id.detail_tag_like, liked)
                        ivIcon.setImageResource(R.drawable.detail_ic_like)
                        tvText.text = if (likeCount == 0) "赞" else likeCount.toString()
                    }
                }
            }
        }
    }

    private fun setCollectStyle(collected: Boolean, collectNum: Int, animated: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            mBottomBarBinding.includeCollect.apply {
                val preCollect = root.getTag(R.id.detail_tag_collect)?.As<Boolean>()
                // 初始化 TAG
                if (preCollect == null) {
                    if (collected) {
                        ivIcon.setImageResource(R.drawable.detail_ic_collect_fill)
                        root.setTag(R.id.detail_tag_collect, true)
                    } else {
                        ivIcon.setImageResource(R.drawable.detail_ic_collect)
                        root.setTag(R.id.detail_tag_collect, false)
                    }
                    tvText.text = if (collectNum == 0) "收藏" else collectNum.toString()
                } else {
                    if (!preCollect && collected) {
                        if (animated) {
                            startActionAnimator(mBottomBarBinding.includeCollect.ivIcon) {
                                mBottomBarBinding.includeCollect.ivIcon.setImageResource(R.drawable.detail_ic_collect_fill)
                            }
                        } else {
                            ivIcon.setImageResource(R.drawable.detail_ic_collect_fill)
                        }
                        root.setTag(R.id.detail_tag_collect, true)
                        tvText.text = collectNum.toString()
                    }
                    if (preCollect && !collected) {
                        root.setTag(R.id.detail_tag_like, false)
                        ivIcon.setImageResource(R.drawable.detail_ic_collect)
                        tvText.text = if (collectNum == 0) "收藏" else collectNum.toString()
                    }
                }
            }
        }
    }


    private fun startActionAnimator(view: View, onEnd: () -> Unit) {
        val mLikeShrinkAnimator = ValueAnimator.ofFloat(1.4f, 1f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
            }
            duration = 100
        }
        ValueAnimator.ofFloat(1f, 1.4f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
            }
            duration = 200
            interpolator = OvershootInterpolator(1.4f)
            doOnEnd {
                onEnd()
                mLikeShrinkAnimator.start()
            }
            start()
        }


    }
}