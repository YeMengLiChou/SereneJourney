package com.sll.mod_imageshare.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.sll.lib_framework.ext.res.layoutInflater
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.visible
import com.sll.mod_image_share.databinding.IsLayoutItemFooterLoadBinding
import com.sll.mod_imageshare.adapter.vh.FooterViewHolder

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/20
 */
class FooterAdapter(
    private val context: Context,
    private val retry: () -> Unit
): LoadStateAdapter<FooterViewHolder>() {
    companion object {
        private const val TAG = "FooterAdapter"
    }

    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        // 根据不同的加载状态展现不同的视图
        holder.apply {
            when (loadState) {
                is LoadState.Error -> {
                    binding.isProgressBar.gone()
                    binding.isBtRetry.visible()
                    binding.isBtRetry.setOnClickListener {
                        retry()
                    }
                }
                is LoadState.Loading -> {
                    binding.isProgressBar.visible()
                    binding.isBtRetry.gone()
                }
                is LoadState.NotLoading -> {
                    binding.isProgressBar.gone()
                    binding.isBtRetry.gone()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterViewHolder {
        return FooterViewHolder(
            IsLayoutItemFooterLoadBinding.inflate(context.layoutInflater, parent, false)
        )
    }
}