package com.sll.mod_settings.service

import com.sll.lib_common.constant.PATH_SETTINGS_ACTIVITY_SETTING
import com.sll.lib_common.service.ISettingService
import com.sll.lib_framework.manager.KvManager
import com.sll.lib_framework.util.FileUtils
import com.sll.mod_settings.constant.KEY_AUTO_NIGHT_MODE
import com.sll.mod_settings.constant.KEY_AUTO_NIGHT_MODE_TIME_INTERVAL
import com.sll.mod_settings.constant.KEY_DRAWER_BACKGROUND_FROM_REMOTE
import com.sll.mod_settings.constant.KEY_DRAWER_BACKGROUND_PATH
import com.sll.mod_settings.constant.KEY_DRAWER_BACKGROUND_UPDATE_TIME
import com.sll.mod_settings.constant.KEY_MAIN_BACKGROUND_FROM_REMOTE
import com.sll.mod_settings.constant.KEY_MAIN_BACKGROUND_PATH
import com.sll.mod_settings.constant.KEY_MAIN_BACKGROUND_UPDATE_TIME
import com.sll.mod_settings.constant.KEY_USER_AVATAR_LAST_UPDATE_TIME
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider
import kotlin.random.Random

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
object SettingService : ISettingService {

    @JvmStatic
    @ServiceProvider(returnType = ISettingService::class)
    fun getService() = this


    override fun navigate() {
        TheRouter
            .build(PATH_SETTINGS_ACTIVITY_SETTING)
            .navigation()
    }
    const val UNINITIALIZED = -1L

    override fun getUserAvatarLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_USER_AVATAR_LAST_UPDATE_TIME, UNINITIALIZED)
    }

    override fun setUserAvatarLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_USER_AVATAR_LAST_UPDATE_TIME, value)
    }

    override fun getDrawerBackgroundLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_DRAWER_BACKGROUND_UPDATE_TIME, UNINITIALIZED)
    }

    override fun setDrawerBackgroundLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_DRAWER_BACKGROUND_UPDATE_TIME, value)
    }

    override fun getDrawerBackgroundPath(): String? {
        return KvManager.kv.getString(KEY_DRAWER_BACKGROUND_PATH, null)
    }

    override fun setDrawerBackgroundPath(value: String) {
        KvManager.kv.putString(KEY_DRAWER_BACKGROUND_PATH, value)
    }

    override fun getIsDrawerBackgroundFromRemote(): Boolean {
        return KvManager.kv.getBoolean(KEY_DRAWER_BACKGROUND_FROM_REMOTE, true)
    }

    override fun setIsDrawerBackgroundFromRemote(value: Boolean) {
        KvManager.kv.putBoolean(KEY_DRAWER_BACKGROUND_FROM_REMOTE, value)
    }

    override fun getMainBackgroundLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_MAIN_BACKGROUND_UPDATE_TIME, UNINITIALIZED)
    }

    override fun setMainBackgroundLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_MAIN_BACKGROUND_UPDATE_TIME, value)
    }

    override fun getMainBackgroundPath(): String? {
        return KvManager.kv.getString(KEY_MAIN_BACKGROUND_PATH, null)
    }

    override fun setMainBackgroundPath(value: String) {
        KvManager.kv.putString(KEY_MAIN_BACKGROUND_PATH, value)
    }

    override fun getIsMainBackgroundFromRemote(): Boolean {
        return KvManager.kv.getBoolean(KEY_MAIN_BACKGROUND_FROM_REMOTE, true)
    }

    override fun setIsMainBackgroundFromRemote(value: Boolean) {
        KvManager.kv.putBoolean(KEY_MAIN_BACKGROUND_FROM_REMOTE, value)
    }

    override fun getPicturesSavePath(): String {
        return KvManager.kv.getString(KEY_MAIN_BACKGROUND_PATH, FileUtils.DEFAULT_SAVE_PATH)
    }

    override fun setPicturesSavePath(value: String) {
        KvManager.kv.putString(KEY_MAIN_BACKGROUND_PATH, value)
    }

    override fun getAutoSwitchNightMode(): Boolean {
        return KvManager.kv.getBoolean(KEY_AUTO_NIGHT_MODE, false)
    }

    override fun setAutoSwitchNightMode(value: Boolean) {
        return KvManager.kv.putBoolean(KEY_AUTO_NIGHT_MODE, value)
    }

    override fun getAutoSwitchNightModeTimeInterval(): Pair<Long, Long> {
        return Pair(
            KvManager.kv.getLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_START", UNINITIALIZED),
            KvManager.kv.getLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_END", UNINITIALIZED),
        )
    }

    override fun setAutoSwitchNightModeTimeInterval(value: Pair<Long, Long>) {
        KvManager.kv.putLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_START", value.first)
        KvManager.kv.putLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_END", value.second)
    }
}