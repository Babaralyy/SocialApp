package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllActivitiesDateResponse(@SerializedName("success"  ) var success  : Boolean?            = null,
                                     @SerializedName("message"  ) var message  : String?             = null,
                                     @SerializedName("response" ) var response : ArrayList<AllActivitiesDateData> = arrayListOf())
