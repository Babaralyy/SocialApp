package com.codecoy.mvpflycollab.datamodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AllJourneyResponse(@SerializedName("success"  ) var success  : Boolean?            = null,
                              @SerializedName("message"  ) var message  : String?             = null,
                              @SerializedName("response" ) var allJourneyData : ArrayList<AllJourneyData> = arrayListOf()) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),

    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(success)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllJourneyResponse> {
        override fun createFromParcel(parcel: Parcel): AllJourneyResponse {
            return AllJourneyResponse(parcel)
        }

        override fun newArray(size: Int): Array<AllJourneyResponse?> {
            return arrayOfNulls(size)
        }
    }
}
