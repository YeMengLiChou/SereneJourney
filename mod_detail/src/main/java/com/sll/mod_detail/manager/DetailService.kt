package com.sll.mod_detail.manager

import android.content.Context
import com.sll.lib_common.constant.PATH_DETAIL_ACTIVITY_DETAIL
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.lib_common.service.IDetailService
import com.sll.lib_framework.ext.toJson
import com.sll.mod_detail.ui.activity.DetailActivity
import com.therouter.TheRouter
import com.therouter.inject.ServiceProvider

/**
 *
 *
 *
 * @author Preke-Li
 * <br/>Created: 2023/09/14
 */
object DetailService: IDetailService {

    @JvmStatic
    @ServiceProvider(returnType = IDetailService::class)
    fun getService() = this


    /**
     * 跳转到 [com.sll.mod_detail.ui.activity.DetailActivity]
     * */
    override fun navigate(context: Context, imageShare: ImageShare, needScrollToComment: Boolean) {
        val intent = TheRouter.build(PATH_DETAIL_ACTIVITY_DETAIL)
            .createIntent(context).apply {
                putExtra(DetailActivity.KEY_IMAGE_SHARE,  imageShare)
                putExtra(DetailActivity.FLAG_SCROLL_TO_COMMENT, needScrollToComment)
            }
        context.startActivity(intent)
    }
}