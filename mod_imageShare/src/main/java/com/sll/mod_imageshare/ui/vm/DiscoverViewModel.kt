package com.sll.mod_imageshare.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sll.mod_imageshare.repository.ImageRepository

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class DiscoverViewModel: ViewModel() {
    companion object {
        private const val TAG = "DiscoverViewModel"
    }


    fun fetchDiscoverImageShares() = ImageRepository.fetchDiscoverImageShares().cachedIn(viewModelScope)



}