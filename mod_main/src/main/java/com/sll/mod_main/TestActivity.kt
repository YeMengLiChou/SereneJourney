package com.sll.mod_main

import android.net.LinkAddress
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.base.activity.BaseBindingActivity
import com.sll.lib_framework.ext.dp
import com.sll.lib_framework.ext.view.get
import com.sll.mod_main.databinding.MainActivityTestBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/13
 */
class TestActivity: BaseBindingActivity<MainActivityTestBinding>() {
    companion object {
        private val TAG = TestActivity::class.simpleName
    }

    override fun onDefCreate(savedInstanceState: Bundle?) {

        binding.mainContent.apply {
            addView(getTextView("LoginService:\n${ServiceManager.loginService.getLoginInfoFlow().value.toString()}"))

            addView(getTextView("UserInfo:\n${ServiceManager.userService.getUserInfo().toString()}"))

            addView(getTextView("内部存储路径:\n" +
                    " getFilesDir: ${context.filesDir.absolutePath}\n" +
                    " getCacheDir: ${context.cacheDir.absolutePath}"))

                addView(getTextView("外部存储路径:\n" +
                        "- getExternalFilesDir: ${context.getExternalFilesDir(null)?.absolutePath}\n" +
                        "- getExternalFilesDir: ${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}\n" +
                        "- getExternalCacheDir: ${context.externalCacheDir?.absolutePath}\n\n" +
                        "sd卡: ${Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED}" +
                        "- ${Environment.getExternalStorageDirectory()}\n" +
                        "- ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}\n" +
                        "- ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}\n"
                ))
        }


    }

    override fun initViewBinding(container: ViewGroup?): MainActivityTestBinding {
        return MainActivityTestBinding.inflate(layoutInflater)
    }


    private fun getTextView(content: String): TextView {
            return TextView(this@TestActivity).apply {
                this.text = content
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(8.dp, 12.dp, 8.dp, 12.dp)
                }
            }
    }
}