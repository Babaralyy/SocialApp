package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllActivitiesDateData(
    @SerializedName("activity_date" ) var activityDate : String? = null
)
