package com.sll.lib_framework.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Px
import kotlin.math.max
import kotlin.math.min

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
object AppManager {

    private val TAG = AppManager::class.simpleName

    private lateinit var mContext: Application

    @Px
    var screenWidthPx = 0
        private set

    @Px
    var screenHeightPx = 0
        private set

    var screenWidthDp = 0
        private set

    var screenHeightDp = 0
        private set

    /**
     * density dpi
     */
    var densityDpi = 0
        private set

    /**
     * density scale
     */
    var density = 0f
        private set

    /**
     * status bar height
     */
    var statusBarHeight = 0
        private set

    /**
     * product type
     */
    var productType: String? = null
        private set

    var isBiggerScreen = false
        private set

    /**
     * 需要初始化
     * */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun init(application: Application) {
        mContext = application
        val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        screenHeightPx = max(metrics.heightPixels, metrics.widthPixels)
        screenWidthPx = min(metrics.heightPixels, metrics.widthPixels)
        isBiggerScreen = screenHeightPx * 1.0 / screenWidthPx > 16.0 / 9
        densityDpi = metrics.densityDpi
        density = metrics.density
        screenHeightDp = (screenHeightPx / density).toInt()
        screenWidthDp = (screenWidthPx / density).toInt()

        val resourceId =
            application.resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = application.resources.getDimensionPixelSize(resourceId)
        productType = genProductType()
    }

    private fun genProductType(): String {
        val model = DeviceInfoUtils.phoneModel
        return model.replace("[:{} \\[\\]\"']*".toRegex(), "")
    }

    /**
     * 获取魅族smartBar高度
     *
     * @return
     */
    fun getSmartBarHeight(): Int {
        if (isMeizu && hasSmartBar()) {
            val autoHideSmartBar = Settings.System.getInt(
                mContext.contentResolver,
                "mz_smartbar_auto_hide", 0
            ) == 1
            return if (autoHideSmartBar) {
                0
            } else {
                getNormalNavigationBarHeight()
            }
        }
        return 0
    }

    private fun getNormalNavigationBarHeight(): Int {
        try {
            val res: Resources =
                mContext.resources
            val rid = res.getIdentifier("config_showNavigationBar", "bool", "android")
            if (rid > 0) {
                val flag = res.getBoolean(rid)
                if (flag) {
                    val resourceId =
                        res.getIdentifier("navigation_bar_height", "dimen", "android")
                    if (resourceId > 0) {
                        return res.getDimensionPixelSize(resourceId)
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 判断是否 meizu 手机
     */
    val isMeizu: Boolean = Build.MANUFACTURER.equals("Meizu", ignoreCase = true)

    fun hasSmartBar(): Boolean {
        try { // 新型号可用反射调用Build.hasSmartBar()
            val method =
                Class.forName("android.os.Build").getMethod("hasSmartBar")
            return method.invoke(null) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "hasSmartBar", e)
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE == "mx2") {
            return true
        } else if (Build.DEVICE == "mx" || Build.DEVICE == "m9") {
            return false
        }
        return false
    }


    /**
     * 获取手机品牌商
     */
    fun getDeviceBuildBrand(): String {
        return Build.BRAND ?: ""
    }

    /**
     * 获取手机型号
     */
    fun getDeviceBuildModel(): String {
        return DeviceInfoUtils.phoneModel
    }

    /**
     * 获取手机系统版本号
     */
    fun getDeviceBuildRelease(): String {
        return Build.VERSION.RELEASE ?: ""
    }

    fun dip2px(dipValue: Float): Int {
        return (dipValue * density + 0.5f).toInt()
    }

    /**
     * 获取版本名称
     */
    fun getAppVersionName(context: Context): String {
        var versionName = ""
        try {
            val pm = context.packageManager
            val packageName = context.packageName ?: "com.sum.tea"
            pm.getPackageInfo(packageName, 0).versionName
            val pi = pm.getPackageInfo(packageName, 0)
            versionName = pi.versionName
            if (versionName.isNullOrEmpty()) {
                return ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAppVersionName: $e", )
        }
        return versionName
    }

    /**
     * 获取版本号
     */
    fun getAppVersionCode(context: Context): Long {
        var appVersionCode: Long = 0
        try {
            val packageName = context.packageName ?: "com.sum.tea"
            val packageInfo = context.applicationContext
                .packageManager
                .getPackageInfo(packageName, 0)
            appVersionCode = packageInfo.versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "getAppVersionCode: $e", )
        }
        return appVersionCode
    }

    override fun toString(): String {
        return ("PhoneInfoManager {"
                + "screenWidthPx="
                + screenWidthPx
                + ", screenHeightPx="
                + screenHeightPx
                + ", screenWidthDp="
                + screenWidthDp
                + ", screenHeightDp="
                + screenHeightDp
                + ", mDensityDpi="
                + densityDpi
                + ", mDensity="
                + density
                + ", mStatusBarHeight="
                + statusBarHeight
                + '}')
    }
}