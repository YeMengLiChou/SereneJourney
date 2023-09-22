package com.sll.mod_user.ui.modify

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.provider.Settings
import android.transition.ChangeBounds
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sll.lib_common.entity.Sex
import com.sll.lib_common.setAvatar
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.res.color
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.textFlow
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.FileUtils
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.util.debug
import com.sll.mod_user.R
import com.sll.mod_user.databinding.UserFragmentModifyBinding
import com.sll.mod_user.databinding.UserLayoutModifyAvatarBinding
import com.sll.mod_user.databinding.UserLayoutModifyIntroduceBinding
import com.sll.mod_user.databinding.UserLayoutModifySexBinding
import com.sll.mod_user.databinding.UserLayoutModifyUsernameBinding
import com.sll.mod_user.ui.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/14
 */
class ModifyFragment :
    BaseMvvmFragment<UserFragmentModifyBinding,
            UserViewModel>() {
    companion object {
        private val TAG = ModifyFragment::class.simpleName

        private const val MAX_LENGTH_USERNAME = 15

        private const val MAX_LENGTH_INTRODUCE = 50

        private const val TYPE_MODIFY_AVATAR = 0

        private const val TYPE_MODIFY_USERNAME = 1

        private const val TYPE_MODIFY_SEX = 2

        private const val TYPE_MODIFY_INTRODUCE = 3

        // 未知性别
        private const val SEX_KEY_UNKNOWN = 0

        // 男
        private const val SEX_KEY_MALE = 1

        // 女
        private const val SEX_KEY_FEMALE = 2
    }

    private lateinit var avatarBinding: UserLayoutModifyAvatarBinding

    private lateinit var usernameBinding: UserLayoutModifyUsernameBinding

    private lateinit var introduceBinding: UserLayoutModifyIntroduceBinding

    private lateinit var sexBinding: UserLayoutModifySexBinding

    // 相机
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            getPhotoFromCamera()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                debug(TAG, "${Manifest.permission.CAMERA} not granted and should not show rationale")
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("权限说明")
                    .setMessage("不授予摄像头权限，则无法进行拍照喔~")
                    .setPositiveButton("去授权") { d, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", "com.sll.serenejourney", null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        this.startActivity(intent)
                        d.dismiss()
                    }.setNegativeButton("取消") { d, _ ->
                        d.dismiss()
                    }.show()
            }
        }
    }

    override fun onDefCreateView() {
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            onBackPressed()
        }
        initTransition()
        initViewData()
        initListeners()
    }

    override fun initViewBinding(container: ViewGroup?) = UserFragmentModifyBinding.inflate(requireActivity().layoutInflater)

    override fun getViewModelClass() = UserViewModel::class

    private fun onBackPressed() {

        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun initTransition() {
        ViewCompat.setTransitionName(binding.userIvAvatar, "avatar")
        ViewCompat.setTransitionName(binding.userTvUsername, "username")


    }


    private fun initViewData() {
        avatarBinding = UserLayoutModifyAvatarBinding.inflate(layoutInflater)
        usernameBinding = UserLayoutModifyUsernameBinding.inflate(layoutInflater)
        introduceBinding = UserLayoutModifyIntroduceBinding.inflate(layoutInflater)
        sexBinding = UserLayoutModifySexBinding.inflate(layoutInflater).apply {
            userTvMale.post {
                userTvMale.setClipViewCornerRadius(userTvMale.width / 2)
                userTvMale.foreground.As<RippleDrawable>()?.setColor(ColorStateList.valueOf(color(R.color.user_blue_light_a80)))
            }
            userTvFemale.post {
                userTvFemale.setClipViewCornerRadius(userTvFemale.width / 2)
                userTvFemale.foreground.As<RippleDrawable>()?.setColor(ColorStateList.valueOf(color(R.color.user_pink_100_a80)))
            }
            userTvUnknown.post {
                userTvUnknown.setClipViewCornerRadius(userTvUnknown.width / 2)
                userTvFemale.foreground.As<RippleDrawable>()?.setColor(ColorStateList.valueOf(color(R.color.user_gray_100_a80)))
            }
        }

        launchOnCreated {
            viewModel.userInfo.collectLatest {
                it?.let {
                    binding.apply {
                        userTvUsername.text = it.username
                        userTvSex.text = when (it.sex) {
                            Sex.MALE.key -> getString(R.string.user_sex_male)
                            Sex.FEMALE.key -> getString(R.string.user_sex_female)
                            else -> getString(R.string.user_sex_unknown)
                        }
                        userTvIntroduce.text = it.introduce
                        binding.userIvAvatar.setAvatar(it.avatar)
                    }
                }
            }
        }
        // 用户信息修改
        launchOnCreated {
            viewModel.updateState.collect { res ->
                res?.onSuccess { ToastUtils.success("修改成功") }
                    ?.onError { ToastUtils.error("${it.message}") }
            }
        }
        // 头像上传
        launchOnCreated {
            viewModel.uploadState.collect { res ->
                res?.onSuccess { ToastUtils.success("上传成功") }
                    ?.onError { ToastUtils.error("上传失败") }
            }
        }
    }

    private fun initListeners() {
        binding.userIvAvatar.throttleClick {
            showBottomFragment(TYPE_MODIFY_AVATAR)
        }
        binding.userLayoutUsername.throttleClick {
            showBottomFragment(TYPE_MODIFY_USERNAME)
        }
        binding.userLayoutSex.throttleClick {
            showBottomFragment(TYPE_MODIFY_SEX)
        }
        binding.userLayoutIntroduce.throttleClick {
            showBottomFragment(TYPE_MODIFY_INTRODUCE)
        }
        binding.userIvBack.click {
           onBackPressed()
        }
    }


    /**
     * 显示底部的修改 dialog fragment
     * */
    private fun showBottomFragment(type: Int) {
        val fragment = BottomModifyFragment()
        when (type) {
            //修改头像
            TYPE_MODIFY_AVATAR -> initAvatarFragment(fragment)
            // 修改用户名
            TYPE_MODIFY_USERNAME -> initUsernameFragment(fragment)
            // 修改性别
            TYPE_MODIFY_SEX -> initSexFragment(fragment)
            // 修改个性签名
            TYPE_MODIFY_INTRODUCE -> initIntroduceFragment(fragment)
        }

        fragment.show(
            requireActivity().supportFragmentManager,
            "modify_fragment"
        )
    }

    private fun initAvatarFragment(fragment: BottomModifyFragment) {
        avatarBinding.userTvCamera.click {
            // 申请权限后再进行拍照
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        avatarBinding.userTvAlbum.click {
            getPhotoFromAlbum()
        }
        fragment
            .setTopBarVisibility(false)
            .setContentView(avatarBinding.root)
    }

    private fun initUsernameFragment(fragment: BottomModifyFragment) {
        // 设置输入内容
        usernameBinding.userEtInput.setText(viewModel.userInfo.value?.username)

        val job = launchOnCreated {
            usernameBinding.userEtInput.textFlow()
                .debounce(200)
                .collectLatest {
                    fragment.setConfirmEnable(it.isNotEmpty() && it.length <= MAX_LENGTH_USERNAME && it != viewModel.userInfo.value?.username)
                    viewModel.setUpdateUsername(it)
                }
        }
        fragment
            .setContentView(usernameBinding.root)
            .setTitle("修改用户名")
            .setAction(object : BottomModifyFragment.OnDialogAction {
                override fun onStart() {
                    // 自动弹出输入法，输入框获取焦点
                    SystemBarUtils.showIme(requireActivity(), usernameBinding.root)
                }

                override fun onCancel() {
                    SystemBarUtils.hideIme(requireActivity(), usernameBinding.root)
                    job.cancel() // 取消流
                }

                override fun onConfirm() {
                    viewModel.updateUsername()
                }
            })
    }

    private fun initSexFragment(fragment: BottomModifyFragment) {
        // 初始化选中
        when (viewModel.userInfo.value?.sex) {
            SEX_KEY_MALE -> {
                setSexStyle(sexBinding.userTvMale, color(R.color.user_white), color(R.color.user_blue_cornflower))
            }

            SEX_KEY_FEMALE -> {
                setSexStyle(sexBinding.userTvFemale, color(R.color.user_white), color(R.color.user_pink_a200))
            }

            else -> {
                setSexStyle(sexBinding.userTvUnknown, color(R.color.user_white), color(R.color.user_gray_dim))
            }
        }

        sexBinding.apply {
            userTvMale.throttleClick {
                setSexStyle(sexBinding.userTvMale, color(R.color.user_white), color(R.color.user_blue_cornflower))
                setSexStyle(sexBinding.userTvFemale, color(R.color.user_pink_a200), color(R.color.user_white))
                setSexStyle(sexBinding.userTvUnknown, color(R.color.user_gray_dim), color(R.color.user_white))
                viewModel.setUpdateSex(SEX_KEY_MALE)
                fragment.setConfirmEnable(viewModel.userInfo.value?.sex != SEX_KEY_MALE)
            }
            userTvFemale.throttleClick {
                setSexStyle(sexBinding.userTvMale, color(R.color.user_blue_cornflower), color(R.color.user_white))
                setSexStyle(sexBinding.userTvFemale, color(R.color.user_white), color(R.color.user_pink_a200))
                setSexStyle(sexBinding.userTvUnknown, color(R.color.user_gray_dim), color(R.color.user_white))
                viewModel.setUpdateSex(SEX_KEY_FEMALE)
                fragment.setConfirmEnable(viewModel.userInfo.value?.sex != SEX_KEY_FEMALE)
            }
            userTvUnknown.throttleClick {
                setSexStyle(sexBinding.userTvMale, color(R.color.user_blue_cornflower), color(R.color.user_white))
                setSexStyle(sexBinding.userTvFemale, color(R.color.user_pink_a200), color(R.color.user_white))
                setSexStyle(sexBinding.userTvUnknown, color(R.color.user_white), color(R.color.user_gray_dim))
                viewModel.setUpdateSex(SEX_KEY_UNKNOWN)
                fragment.setConfirmEnable(viewModel.userInfo.value?.sex != SEX_KEY_UNKNOWN)
            }
        }

        fragment.setContentView(sexBinding.root)
            .setTitle("性别选择")
            .setAction(object : BottomModifyFragment.OnDialogAction {
                override fun onStart() {}

                override fun onCancel() {}

                override fun onConfirm() {
                    viewModel.updateSex()
                }
            })
    }

    // 初始化修改个人签名的fragment
    private fun initIntroduceFragment(fragment: BottomModifyFragment) {
        // 设置输入内容
        introduceBinding.userEtInput.setText(viewModel.userInfo.value?.introduce ?: "")
        val job = launchOnCreated {
            introduceBinding.userEtInput.textFlow()
                .debounce(200)
                .collectLatest {
                    // 签名不为空， 不超过指定长度， 与之前的签名不同
                    fragment.setConfirmEnable(it.isNotBlank() && it.length <= MAX_LENGTH_INTRODUCE && it != viewModel.userInfo.value?.introduce)
                    viewModel.setUpdateIntroduce(it)
                }
        }
        fragment.setContentView(introduceBinding.root)
            .setTitle("修改个性签名")
            .setAction(object : BottomModifyFragment.OnDialogAction {
                override fun onStart() {
                    // 自动弹出输入法，输入框获取焦点
                    SystemBarUtils.showIme(requireActivity(), introduceBinding.root)
                }

                override fun onCancel() {
                    SystemBarUtils.hideIme(requireActivity(), introduceBinding.root)
                    job.cancel()
                }

                override fun onConfirm() {
                    viewModel.updateIntroduce()
                }
            })
    }

    // 修改性别 fragment 的样式
    private fun setSexStyle(view: TextView, contentColor: Int, bgColor: Int) {
        view.apply {
            setTextColor(contentColor)
            setBackgroundColor(bgColor)
        }
    }

    // 从相机获取
    private fun getPhotoFromCamera() {
        FileUtils.PhotoRequest.with(this)
            .camera()
            .crop(FileUtils.PhotoRequest.CropOptions().aspect(1, 1).scale().ouput(500, 500))
            .build { uri, state ->
                if (state == FileUtils.PhotoRequest.State.SUCCESS) {
                    debug("photo", uri)
                    uri?.let { viewModel.uploadAvatar(it) } // 上传图片
                }
            }
    }

    // 从相册获取
    private fun getPhotoFromAlbum() {
        FileUtils.PhotoRequest.with(this)
            .pick()
            .crop(FileUtils.PhotoRequest.CropOptions().aspect(1, 1).scale().ouput(500, 500))
            .build { uri, state ->
                debug("photo", uri)
                if (state == FileUtils.PhotoRequest.State.SUCCESS) {
                    uri?.let { viewModel.uploadAvatar(it) } // 上传图片
                }
            }
    }
}