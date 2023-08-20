package com.sll.lib_common.service

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
interface ISettingService {

    fun navigate()


    // ================================ 图片相关 ========================
    /**
     * 获取用户头像最后的更新时间
     * */
    fun getUserAvatarLastUpdateTime(): Long

    /**
     * 设置用户头像最后的更新时间
     * */
    fun setUserAvatarLastUpdateTime(value: Long)

    /**
     * 获取抽屉背景最后的更新时间
     * */
    fun getDrawerBackgroundLastUpdateTime(): Long

    /**
     * 设置抽屉背景最后的更新时间
     * */
    fun setDrawerBackgroundLastUpdateTime(value: Long)

    /**
     * 获取抽屉背景路径
     * */
    fun getDrawerBackgroundPath(): String?

    /**
     * 设置抽屉背景路径
     * */
    fun setDrawerBackgroundPath(value: String)

    /**
     * 获取抽屉背景是否从网络加载
     * */
    fun getIsDrawerBackgroundFromRemote(): Boolean

    /**
     * 设置抽屉背景是否从网络加载
     * */
    fun setIsDrawerBackgroundFromRemote(value: Boolean)

    /**
     * 获取主背景最后的更新时间
     * */
    fun getMainBackgroundLastUpdateTime(): Long

    /**
     * 设置主背景最后的更新时间
     * */
    fun setMainBackgroundLastUpdateTime(value: Long)

    /**
     * 获取抽屉背景路径
     * */
    fun getMainBackgroundPath(): String?

    /**
     * 设置抽屉背景路径
     * */
    fun setMainBackgroundPath(value: String)

    /**
     * 获取抽屉背景是否从网络加载
     * */
    fun getIsMainBackgroundFromRemote(): Boolean

    /**
     * 设置抽屉背景是否从网络加载
     * */
    fun setIsMainBackgroundFromRemote(value: Boolean)

    /**
     * 获取全局保存路径
     * */
    fun getPicturesSavePath(): String

     /**
     * 设置全局保存路径
     * */
    fun setPicturesSavePath(value: String)



    // =========================== 夜间模式 =================================
    /**
     * 获取 自动切换夜间模式
     * */
    fun getAutoSwitchNightMode(): Boolean

    /**
     * 设置 自动切换夜间模式
     * */
    fun setAutoSwitchNightMode(value: Boolean)

    /**
     * 设置 自动切换夜间模式的时间段
     * */
    fun getAutoSwitchNightModeTimeInterval(): Pair<Long, Long>?

    /**
     * 设置 自动切换夜间模式的时间段
     * */
    fun setAutoSwitchNightModeTimeInterval(value: Pair<Long, Long>)


}