package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.AllJourneyData

interface JourneyCallback {
    fun onJourneyClick(journeyData: AllJourneyData)
    fun onJourneyDeleteClick(journeyData: AllJourneyData)
}