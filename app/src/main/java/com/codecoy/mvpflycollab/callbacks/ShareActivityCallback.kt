package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.AllActivitiesData

interface ShareActivityCallback {
    fun onShareActivityClick(event: AllActivitiesData)
}