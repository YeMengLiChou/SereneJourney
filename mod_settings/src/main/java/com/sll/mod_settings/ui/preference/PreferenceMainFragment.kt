package com.sll.mod_settings.ui.preference

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.ext.res.string
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_framework.util.FileUtils
import com.sll.lib_framework.util.ToastUtils
import com.sll.mod_settings.R

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
class PreferenceMainFragment: PreferenceFragmentCompat() {
    companion object {
        private val TAG = PreferenceMainFragment::class.simpleName
    }
    private lateinit var keyPictureSavePath: String

    private lateinit var keyAccout: String

    private lateinit var keyClearCache: String

    private lateinit var mPrePictureSavePath: EditTextPreference

    private lateinit var mPreAccount: Preference

    private lateinit var mPreClearCache: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences_main, rootKey)
        preferenceManager.preferenceDataStore = LPreferenceDataStore

        initKeys()
        initPreferences()
    }

    private fun initKeys() {
        keyPictureSavePath = string(R.string.settings_picture_save_path)
        keyAccout = string(R.string.settings_account)
        keyClearCache = string(R.string.settings_picture_clear_cache)
    }

    private fun initPreferences() {
        mPreAccount = findPreference<Preference>(keyAccout)!!.apply {
            isEnabled = ServiceManager.loginService.isLogin()
        }

        mPrePictureSavePath = findPreference<EditTextPreference>(keyPictureSavePath)!!.apply {
            dialogTitle = "图片保存路径"
            dialogMessage = "默认保存目录: "
            this.negativeButtonText = "恢复默认"

            summary = "当前: ${KvManager.kv.getString(keyPictureSavePath)}"
            setOnBindEditTextListener {
                ToastUtils.success(it.text.toString())

            }
        }

        mPreClearCache = findPreference<Preference>(keyClearCache)!!.apply {
            this.summary = "全部缓存: "

        }




    }
}