package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class AllActivitiesData(
    @SerializedName("id"                   ) var id                  : Int?                       = null,
    @SerializedName("user_id"              ) var userId              : Int?                       = null,
    @SerializedName("activity_name"        ) var activityName        : String?                    = null,
    @SerializedName("activity_description" ) var activityDescription : String?                    = null,
    @SerializedName("activity_date"        ) var activityDate        : String?                    = null,
    @SerializedName("start_time"           ) var startTime           : String?                    = null,
    @SerializedName("end_time"             ) var endTime             : String?                    = null,
    @SerializedName("created_at"           ) var createdAt           : String?                    = null,
    @SerializedName("updated_at"           ) var updatedAt           : String?                    = null,
    @SerializedName("activity_details"     ) var activityDetails     : ArrayList<ActivityDetails> = arrayListOf()
)
