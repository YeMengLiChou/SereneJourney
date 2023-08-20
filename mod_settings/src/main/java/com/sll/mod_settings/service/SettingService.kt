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
import com.sll.mod_settings.constant.KEY_PICTURE_SAVE_PATH
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

    /**
     * 用户头像最后更新时间
     * */
    override fun getUserAvatarLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_USER_AVATAR_LAST_UPDATE_TIME, UNINITIALIZED)
    }

    /**
     * 设置用户头像最后更新时间
     * */
    override fun setUserAvatarLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_USER_AVATAR_LAST_UPDATE_TIME, value)
    }

    /**
     * 获取抽屉背景的最后更新时间
     * */
    override fun getDrawerBackgroundLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_DRAWER_BACKGROUND_UPDATE_TIME, UNINITIALIZED)
    }

    /**
     * 设置抽屉背景的最后更新时间
     * */
    override fun setDrawerBackgroundLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_DRAWER_BACKGROUND_UPDATE_TIME, value)
    }

    /**
     * 获取抽屉背景的本地缓存路径
     * */
    override fun getDrawerBackgroundPath(): String? {
        return KvManager.kv.getString(KEY_DRAWER_BACKGROUND_PATH, null)
    }

    /**
     * 设置抽屉背景本地缓存
     * */
    override fun setDrawerBackgroundPath(value: String) {
        KvManager.kv.putString(KEY_DRAWER_BACKGROUND_PATH, value)
    }

    /**
     * 判断抽屉背景是否为远程获取
     * */
    override fun getIsDrawerBackgroundFromRemote(): Boolean {
        return KvManager.kv.getBoolean(KEY_DRAWER_BACKGROUND_FROM_REMOTE, true)
    }


    /**
     * 设置抽屉背景是否为远程获取
     * */
    override fun setIsDrawerBackgroundFromRemote(value: Boolean) {
        KvManager.kv.putBoolean(KEY_DRAWER_BACKGROUND_FROM_REMOTE, value)
    }

    /**
     * 获取主页背景的最后更新时间
     * */
    override fun getMainBackgroundLastUpdateTime(): Long {
        return KvManager.kv.getLong(KEY_MAIN_BACKGROUND_UPDATE_TIME, UNINITIALIZED)
    }

    /**
     * 设置主页背景的最后更新时间
     * */
    override fun setMainBackgroundLastUpdateTime(value: Long) {
        KvManager.kv.putLong(KEY_MAIN_BACKGROUND_UPDATE_TIME, value)
    }

    /**
     * 获取主页背景的本地缓存路径
     * */
    override fun getMainBackgroundPath(): String? {
        return KvManager.kv.getString(KEY_MAIN_BACKGROUND_PATH, null)
    }

    /**
     * 设置主页背景的本地缓存路径
     * */
    override fun setMainBackgroundPath(value: String) {
        KvManager.kv.putString(KEY_MAIN_BACKGROUND_PATH, value)
    }

    /**
     * 判断主页背景是否为远程获取
     * */
    override fun getIsMainBackgroundFromRemote(): Boolean {
        return KvManager.kv.getBoolean(KEY_MAIN_BACKGROUND_FROM_REMOTE, true)
    }

    /**
     * 设置主页背景是否为远程获取
     * */
    override fun setIsMainBackgroundFromRemote(value: Boolean) {
        KvManager.kv.putBoolean(KEY_MAIN_BACKGROUND_FROM_REMOTE, value)
    }

    /**
     * 获取图片下载保存的位置，默认为 /storage/emulated/0/Pictures/Save
     * */
    override fun getPicturesSavePath(): String {
        val path = KvManager.kv.getString(KEY_PICTURE_SAVE_PATH)
        if (path == null) {
            KvManager.kv.putString(KEY_PICTURE_SAVE_PATH, FileUtils.DEFAULT_SAVE_PATH)
        }
        return path
    }

    /**
     * 设置图片下载保存的位置
     * */
    override fun setPicturesSavePath(value: String) {
        KvManager.kv.putString(KEY_PICTURE_SAVE_PATH, value)
    }

    /**
     * 判断是否为自动切换为夜间模式
     * */
    override fun getAutoSwitchNightMode(): Boolean {
        return KvManager.kv.getBoolean(KEY_AUTO_NIGHT_MODE, false)
    }

    /**
     * 设置是否自动切换为夜间模式
     * */
    override fun setAutoSwitchNightMode(value: Boolean) {
        return KvManager.kv.putBoolean(KEY_AUTO_NIGHT_MODE, value)
    }

    /**
     * 获取自动切换夜间模式的时间区间
     * */
    override fun getAutoSwitchNightModeTimeInterval(): Pair<Long, Long> {
        return Pair(
            KvManager.kv.getLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_START", UNINITIALIZED),
            KvManager.kv.getLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_END", UNINITIALIZED),
        )
    }

    /**
     * 设置自动切换夜间模式的时间区间
     * */
    override fun setAutoSwitchNightModeTimeInterval(value: Pair<Long, Long>) {
        KvManager.kv.putLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_START", value.first)
        KvManager.kv.putLong("${KEY_AUTO_NIGHT_MODE_TIME_INTERVAL}_END", value.second)
    }
}