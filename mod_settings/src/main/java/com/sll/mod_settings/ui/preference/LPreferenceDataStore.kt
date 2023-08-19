package com.sll.mod_settings.ui.preference

import androidx.preference.PreferenceDataStore
import com.sll.lib_framework.manager.KvManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
object LPreferenceDataStore: PreferenceDataStore() {

    override fun putString(key: String?, value: String?) {
        KvManager.kv.putString(key, value)
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        KvManager.kv.putObject(key, values, KvManager.SetStringEncoder)
    }

    override fun putInt(key: String?, value: Int) {
        KvManager.kv.putInt(key, value)
    }

    override fun putLong(key: String?, value: Long) {
        KvManager.kv.putLong(key, value)
    }

    override fun putFloat(key: String?, value: Float) {
        KvManager.kv.putFloat(key, value)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        KvManager.kv.putBoolean(key, value)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return KvManager.kv.getString(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {

        return KvManager.kv.getObject(key)?: defValues
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return KvManager.kv.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return KvManager.kv.getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return KvManager.kv.getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return KvManager.kv.getBoolean(key, defValue)
    }
}