package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AddJourneyDetailBody(@SerializedName("journey_id"  ) var journeyId   : Int?             = null,
                                @SerializedName("title"       ) var title       : String?          = null,
                                @SerializedName("description" ) var description : String?          = null,
                                @SerializedName("date"        ) var date        : String?          = null,
                                @SerializedName("image"       ) var image       : ArrayList<AddJourneyDetailImage> = arrayListOf())
