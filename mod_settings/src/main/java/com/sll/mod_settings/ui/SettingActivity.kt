package com.sll.mod_settings.ui

import android.os.Bundle
import android.view.ViewGroup
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.fragment.app.commit
import com.sll.lib_common.constant.PATH_SETTINGS_ACTIVITY_SETTING
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.util.SystemBarUtils
import com.sll.mod_settings.databinding.SettingsActivitySettingBinding
import com.sll.mod_settings.ui.preference.PreferenceMainFragment
import com.therouter.router.Route
import kotlin.reflect.KClass

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
@Route(path = PATH_SETTINGS_ACTIVITY_SETTING)
class SettingActivity : BaseMvvmActivity<SettingsActivitySettingBinding, SettingViewModel>() {
    companion object {
        private val TAG = SettingActivity::class.simpleName
    }


    override fun onDefCreate(savedInstanceState: Bundle?) {
        SystemBarUtils.immersiveStatusBar(this)
        setSupportActionBar(binding.settingsToolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun initViewBinding(container: ViewGroup?): SettingsActivitySettingBinding
        = SettingsActivitySettingBinding.inflate(layoutInflater)

    override fun getViewModelClass(): KClass<SettingViewModel>
        = SettingViewModel::class


}