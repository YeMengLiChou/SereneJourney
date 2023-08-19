package com.sll.mod_login.ui

import android.view.ViewGroup
import com.sll.lib_framework.base.fragment.BaseMvvmFragment
import com.sll.lib_framework.ext.As
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.res.string
import com.sll.lib_framework.ext.view.textFlow
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.ToastUtils
import com.sll.lib_network.error.ApiException
import com.sll.mod_login.CheckParamsUtils
import com.sll.mod_login.R
import com.sll.mod_login.databinding.LoginFragmentRegisterBinding
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/10
 */
class RegisterFragment : BaseMvvmFragment<LoginFragmentRegisterBinding, LoginViewModel>() {
    companion object {
        private val TAG = RegisterFragment::class.simpleName

        private const val FLAG_USERNAME_ENABLED = 1 shl 0
        private const val FLAG_PASSWORD_ENABLED = 1 shl 1
        private const val FLAG_PASSWORD_REPEAT_ENABLED = 1 shl 2
        private const val FLAG_ALL_ENABLE = FLAG_PASSWORD_ENABLED and FLAG_USERNAME_ENABLED and FLAG_PASSWORD_REPEAT_ENABLED
    }

    // 用于判断是否可以点击注册
    private var flagRegisterEnabled = 0

    override fun onDefCreateView() {
        initEditText()
        initClick()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: 清除ViewModel对应的数据，防止下次打开进行弹窗
    }

    override fun initViewBinding(container: ViewGroup?): LoginFragmentRegisterBinding {
        return LoginFragmentRegisterBinding.inflate(layoutInflater, container, false)
    }

    override fun getViewModelClass(): KClass<LoginViewModel> {
        return LoginViewModel::class
    }


    private fun initClick() {
        binding.loginBtRegister.throttleClick {
            val username = binding.loginEtUsername.text?.toString()
            val password = binding.loginEtPassword.text?.toString()
            if (CheckParamsUtils.checkUsernamePwd(username, password)) {
                viewModel.register(username!!, password!!)
            }
        }
    }

    private fun updateUI() {
        launchOnCreated {
            viewModel.registerState.collect {
                it
                    ?.onLoading {
                        /* TODO: 弹窗显示等待 */
                        ToastUtils.warn("请稍等....")
                    }
                    ?.onError { e ->
                        /**TODO: 结束弹窗*/
                        ToastUtils.error("注册失败:${e.As<ApiException>()?.msg}")
                    }
                    ?.onRetry {  }
                    ?.onSuccess {
                        ToastUtils.success("注册成功")
                        viewModel.switchToLoginFragment()
                    }
            }
        }
    }

    // 初始化输入框
    private fun initEditText() {
        /**
         *
         *
         * */
        launchOnCreated {
            binding.loginEtUsername.setText(viewModel.registerUsername)
            binding.loginEtUsername.textFlow()
                .debounce(100)
                .filter { it.isNotEmpty() }
                .collect {
                    viewModel.registerUsername = it
                    val errorMsg: String? =
                        when {
                            //
                            it.length > LoginViewModel.MAX_USERNAME_LENGTH -> string(R.string.login_error_exceed_max_length)
                            else -> null
                        }
                    errorMsg
                        ?.let {
                            clearFlag(FLAG_USERNAME_ENABLED)
                            binding.loginInputLayoutUsername.error = it
                        }
                        ?: run {
                            setFlag(FLAG_USERNAME_ENABLED)
                            binding.loginInputLayoutUsername.isErrorEnabled = false
                        }
                }
        }

        launchOnCreated {
            binding.loginEtPassword.setText(viewModel.registerPassword)
            binding.loginEtPassword.textFlow()
                .debounce(100)
                .filter { it.isNotEmpty() }
                .collect {
                    viewModel.registerPassword = it
                    val errorMsg: String? =
                        when {
                            //
                            it.length > LoginViewModel.MAX_PASSWORD_LENGTH -> string(R.string.login_error_exceed_max_length)
                            else -> null
                        }
                    errorMsg
                        ?.let {
                            clearFlag(FLAG_USERNAME_ENABLED)
                            binding.loginInputLayoutPassword.error = it
                        }
                        ?: run {
                            setFlag(FLAG_USERNAME_ENABLED)
                            binding.loginInputLayoutPassword.isErrorEnabled = false
                        }
                }
        }

        launchOnCreated {
            binding.loginEtPasswordRepeat.setText(viewModel.registerPasswordRepeat)
            binding.loginEtPasswordRepeat.textFlow()
                .debounce(100)
                .filter { it.isNotEmpty() }
                .collect {
                    viewModel.registerPasswordRepeat = it
                    val errorMsg: String? =
                        when {
                            //
                            it.length > LoginViewModel.MAX_PASSWORD_LENGTH -> string(R.string.login_error_exceed_max_length)
                            !it.contentEquals(viewModel.registerPassword) -> string(R.string.login_error_password_not_equals)
                            else -> null
                        }
                    errorMsg
                        ?.let {
                            clearFlag(FLAG_PASSWORD_REPEAT_ENABLED)
                            binding.loginInputLayoutPasswordRepeat.error = it
                        }
                        ?: run {
                            setFlag(FLAG_PASSWORD_REPEAT_ENABLED)
                            binding.loginInputLayoutPasswordRepeat.isErrorEnabled = false
                        }
                }
        }
    }

    private fun setFlag(flags: Int) {
        flagRegisterEnabled = flagRegisterEnabled.and(flags)
        binding.loginBtRegister.isEnabled = checkRegisterEnable()
    }

    private fun clearFlag(flags: Int) {
        flagRegisterEnabled = flagRegisterEnabled.and(flags.inv())
        binding.loginBtRegister.isEnabled = checkRegisterEnable()

    }

    private fun checkRegisterEnable(): Boolean {
        return flagRegisterEnabled == FLAG_ALL_ENABLE
    }

}