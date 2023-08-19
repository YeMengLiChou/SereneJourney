package com.sll.mod_login.ui

import android.view.ViewGroup
import androidx.fragment.app.commit
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.launchOnStarted
import com.sll.lib_framework.ext.res.string
import com.sll.lib_framework.ext.view.setErrorAnimated
import com.sll.lib_framework.ext.view.textFlow
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_framework.util.info
import com.sll.mod_login.CheckParamsUtils
import com.sll.mod_login.R
import com.sll.mod_login.databinding.LoginFragmentLoginBinding
import com.sll.mod_login.manager.LoginInfoManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.math.log
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/10
 */
class LoginFragment : BaseMvvmFragment<LoginFragmentLoginBinding, LoginViewModel>() {
    companion object {
        private val TAG = LoginFragment::class.simpleName
    }

    override fun onDefCreateView() {
        initLocalData()
        initEvent()
        initEditText()
    }


    override fun initViewBinding(container: ViewGroup?): LoginFragmentLoginBinding {
        return LoginFragmentLoginBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModelClass(): KClass<LoginViewModel> {
        return LoginViewModel::class
    }


    // 初始化本地数据，保存的用户名，密码
    private fun initLocalData() {
        val loginInfo = ServiceManager.loginService.getLoginInfoFlow().value ?: return
        val (username, password, remember, _) = loginInfo
        info("LoginFragment", "LoginLocalData: $loginInfo")
        viewModel.setRememberChecked(remember)
        viewModel.setLoginUsername(username)
        // 只有记住密码将其展示
        if (viewModel.loginRemember.value) viewModel.setLoginPassword(password)
    }

    // 相关事件
    private fun initEvent() {
        // 点击登录
        binding.loginBtLogin.throttleClick(
            500,
            onStart = {
                // 全局弹窗
                it.isEnabled = false
            },
            onEnd = {
                it.isEnabled = true
            },
            action = {
                val username = binding.loginEtUsername.text?.toString()
                val password = binding.loginEtPassword.text?.toString()
                if (CheckParamsUtils.checkUsernamePwd(username, password)) {
                    viewModel.login(username!!, password!!)
                }
            }
        )

        // 切换到注册
        binding.loginBtRegister.throttleClick {
            viewModel.switchToRegisterFragment()
        }
        binding.loginCbRemember.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRememberChecked(isChecked)
        }
    }

    // 初始化输入框
    @OptIn(FlowPreview::class)
    private fun initEditText() {
        // 当重新进入时加载数据
        launchOnStarted {
            binding.loginEtUsername.setText(viewModel.loginUsername.value)
            binding.loginEtPassword.setText(viewModel.loginPassword.value)
            binding.loginCbRemember.isChecked = viewModel.loginRemember.value
        }

        launchOnCreated {
            binding.loginEtUsername
                .textFlow()
                .debounce(200)
                .collect { text ->
                    // 保存当前输入
                    viewModel.setLoginUsername(text)
                    // 检查输入是否有问题 TODO：补充限制
                    val msg = when {
                        text.length >= LoginViewModel.MAX_USERNAME_LENGTH -> string(R.string.login_error_exceed_max_length)
                        else -> null
                    }
                    // 如果msg为空，意味着没有错误
                    msg
                        ?.let { binding.loginInputLayoutUsername.setErrorAnimated(it) }
                        ?: run { binding.loginInputLayoutUsername.isErrorEnabled = false }
                }
        }
        launchOnCreated {
            binding.loginEtPassword
                .textFlow()
                .debounce(200)
                .collect { text ->
                    viewModel.setLoginPassword(text)
                    val msg = when {
                        text.length >= LoginViewModel.MAX_PASSWORD_LENGTH -> string(R.string.login_error_exceed_max_length)
                        else -> null
                    }
                    msg
                        ?.let { binding.loginInputLayoutPassword.error = it }
                        ?: run { binding.loginInputLayoutPassword.isErrorEnabled = false }
                }
        }

        launchOnCreated {
            viewModel.loginState.collect { res ->
                res  // 登录成功，不需要退出，交给Activity去做
                    ?.onSuccess {
                        ToastUtils.success("${it!!.username} 欢迎回来~")
                    }
                    ?.onLoading {
                        // TODO 改成dialog形式的加载
                        ToastUtils.warn("请稍等")
                    }
                    ?.onError {
                        ToastUtils.error("登陆失败:${it.message}")
                    }
            }
        }

    }






}