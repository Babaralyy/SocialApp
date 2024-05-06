package com.codecoy.mvpflycollab.datamodels

import com.google.gson.annotations.SerializedName

data class OneTwoOneChatData(@SerializedName("id"          ) var id         : Int?    = null,
                             @SerializedName("sender_id"   ) var senderId   : Int?    = null,
                             @SerializedName("receiver_id" ) var receiverId : Int?    = null,
                             @SerializedName("message"     ) var message    : String? = null,
                             @SerializedName("created_at"  ) var createdAt  : String? = null,
                             @SerializedName("updated_at"  ) var updatedAt  : String? = null)
