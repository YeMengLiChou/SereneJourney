package com.sll.mod_settings.ui.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.ext.res.string
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_settings.R

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
class PreferenceAccountFragment: PreferenceFragmentCompat() {
    companion object {
        private val TAG = PreferenceAccountFragment::class.simpleName
    }

    private lateinit var keyAutoLogin: String
    private lateinit var keyLogout: String
    private lateinit var keyInfoModify: String


    private lateinit var mPreAutoLogin: SwitchPreference
    private lateinit var mPreModify: Preference
    private lateinit var mPreLogout: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences_accout, rootKey)
        initKeys()
        initPreferences()
    }

    private fun initKeys() {
        keyAutoLogin = string(R.string.settings_account_auto_login)
        keyLogout = string(R.string.settings_account_logout)
        keyInfoModify = string(R.string.settings_account_modify)
    }

    private fun initPreferences() {
        mPreAutoLogin = findPreference<SwitchPreference>(keyAutoLogin)!!.apply {
//            icon = ServiceManager.
        }

        mPreLogout = findPreference<Preference>(keyLogout)!!.apply {
            setOnPreferenceClickListener {
                ServiceManager.loginService.logout()
                ServiceManager.userService.clearUserInfo()
                ToastUtils.success("已退出登录")
                return@setOnPreferenceClickListener true
            }
        }

        mPreModify = findPreference<Preference>(keyInfoModify)!!.apply {
            // TODO: 跳转到个人主页
        }


    }
}