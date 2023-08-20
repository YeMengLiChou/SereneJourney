package com.sll.lib_framework.base.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.sll.lib_framework.ext.lazyNone
import com.sll.lib_framework.util.debug
import com.therouter.TheRouter

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
    }

}