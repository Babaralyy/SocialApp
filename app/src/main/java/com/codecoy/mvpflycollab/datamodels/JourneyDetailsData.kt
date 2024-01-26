package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class JourneyDetailsData(@SerializedName("id"          ) var id          : Int?              = null,
                              @SerializedName("journey_id"  ) var journeyId   : Int?              = null,
                              @SerializedName("title"       ) var title       : String?           = null,
                              @SerializedName("description" ) var description : String?           = null,
                              @SerializedName("date"        ) var date        : String?           = null,
                              @SerializedName("created_at"  ) var createdAt   : String?           = null,
                              @SerializedName("updated_at"  ) var updatedAt   : String?           = null,
                              @SerializedName("images"      ) var journeyDetailImages      : ArrayList<JourneyDetailImages> = arrayListOf())
