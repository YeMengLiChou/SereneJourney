package com.sll.mod_imageshare.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sll.lib_common.entity.dto.ImageShare
import com.sll.mod_imageshare.repository.ImageRepository

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/19
 */
class ImageShareViewModel: ViewModel() {
    companion object {
        private const val TAG = "ImageShareViewModel"
    }
    fun fetchDiscoverImageShares() = ImageRepository.fetchDiscoverImageShares().cachedIn(viewModelScope)

    fun fetchFocusImageShares() = ImageRepository.fetchFocusImageShare().cachedIn(viewModelScope)

    fun fetchLikeImageShares() = ImageRepository.fetchLikeImageShare().cachedIn(viewModelScope)

    fun fetchCollectImageShares() = ImageRepository.fetchCollectImageShare().cachedIn(viewModelScope)

    fun fetchSavedImageShares() = ImageRepository.fetchSavedImageShare().cachedIn(viewModelScope)

    fun fetchPublishImageShares() = ImageRepository.fetchPublishImageShare().cachedIn(viewModelScope)
}