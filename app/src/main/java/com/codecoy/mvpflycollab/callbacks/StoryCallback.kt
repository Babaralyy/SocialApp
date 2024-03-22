package com.codecoy.mvpflycollab.callbacks

import com.codecoy.mvpflycollab.datamodels.CalendarStoryData

interface StoryCallback {
    fun onStoryClick(storyData: CalendarStoryData)
}