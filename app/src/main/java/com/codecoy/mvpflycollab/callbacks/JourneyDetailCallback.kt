package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.JourneyDetailImages

interface JourneyDetailCallback {
    fun onImgClick(imageData: String)
}