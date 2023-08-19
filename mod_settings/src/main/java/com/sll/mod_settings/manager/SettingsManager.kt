package com.sll.mod_settings.manager

import com.sll.lib_framework.manager.KvManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
object SettingsManager {

    fun saveSetting(key: String, value: String) {
        if (key.isEmpty()) return
        saveSettingLocal(key, value)
    }

    fun saveSetting(key: String, value: Int) {
        if (key.isEmpty()) return
        saveSettingLocal(key, value)
    }

    fun saveSetting(key: String, value: Long) {
        if (key.isEmpty()) return
        saveSettingLocal(key, value)
    }

    fun saveSetting(key: String, value: Boolean) {
        if (key.isEmpty()) return
        saveSettingLocal(key, value)
    }

    fun saveSetting(key: String, value: Float) {
        if (key.isEmpty()) return
        saveSettingLocal(key, value)
    }

    private fun saveSettingLocal(key: String, value: String) {
        KvManager.kv.putString(key, value)
    }

    private fun saveSettingLocal(key: String, value: Int) {
        KvManager.kv.putInt(key, value)
    }

    private fun saveSettingLocal(key: String, value: Long) {
        KvManager.kv.putLong(key, value)
    }

    private fun saveSettingLocal(key: String, value: Boolean) {
        KvManager.kv.putBoolean(key, value)

    }

    private fun saveSettingLocal(key: String, value: Float) {
        KvManager.kv.putFloat(key, value)
    }

}