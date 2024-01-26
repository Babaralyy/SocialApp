package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddJourneyResponse(
    @SerializedName("success"  ) var success  : Boolean?  = null,
    @SerializedName("message"  ) var message  : String?   = null,
    @SerializedName("response" ) var addJourneyData : AddJourneyData? = AddJourneyData()
)
