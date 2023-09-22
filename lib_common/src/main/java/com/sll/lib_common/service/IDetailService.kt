package com.sll.lib_common.service

import android.content.Context
import com.sll.lib_common.entity.dto.ImageShare

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/17
 */
interface IDetailService {

    /**
     * 跳转到 [com.sll.mod_detail.ui.activity.DetailActivity]
     * */
    fun navigate(context: Context, imageShare: ImageShare)
}