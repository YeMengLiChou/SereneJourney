package com.sll.lib_framework.ext.view

import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn


/**
 * EditText 的 扩展
 *
 *
 * @author Gleamrise
 * Created: 2023/07/16
 * Tested: 2023/07/27
 */

/**
 * 输入框文字变化流
 *
 * 栗子:
 * ```
 * editText.textFlow()
 * .filter { it.isNotEmpty() } // 过滤空内容，避免无效的搜索，注意清空时发送的文字还是最后清除的字符
 * .debounce(300) // 300ms 间隔防抖
 * .flatMapLatest {
 *     flow{ searchFlow(it) } // 新搜索覆盖旧搜索
 *  }
 * .flowOn(Dispatchers.IO) // 搜索异步执行
 * .onEach { updateUI(it) } // 搜索结果更新界面
 * .launchIn(mainScope) // 主线程
 * ```
 * */
fun EditText.textFlow(): Flow<String> = callbackFlow {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        override fun afterTextChanged(s: Editable?) {
            // 将变化后的文本发送到 flow 中
            s?.let { trySend(it.toString()) }
        }
    }
    addTextChangedListener(watcher)
    // 堵塞保证 flow 存在
    awaitClose { removeTextChangedListener(watcher) }
}.flowOn(Dispatchers.IO)






fun TextInputEditText.setErrorAnimated(errorMsg: String, animation: Animation = editTextShakeAnimation(3, 300)) {
    this.error = errorMsg
}

fun TextInputLayout.setErrorAnimated(errorMsg: String, animation: Animation = editTextShakeAnimation(3, 300)) {
    this.error = errorMsg
//    this.startAnimation(animation)

}

private fun editTextShakeAnimation(
    counts: Int,
    duration: Long = 300
): Animation {
    val trAnimation: Animation = TranslateAnimation(0f, 10f, 0f, 0f)
    trAnimation.interpolator = CycleInterpolator(counts.toFloat())
    trAnimation.duration = duration
    return trAnimation
}