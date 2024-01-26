package com.codecoy.mvpflycollab.datamodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AllJourneyData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("journey_img") var journeyImg: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(journeyImg)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllJourneyData> {
        override fun createFromParcel(parcel: Parcel): AllJourneyData {
            return AllJourneyData(parcel)
        }

        override fun newArray(size: Int): Array<AllJourneyData?> {
            return arrayOfNulls(size)
        }
    }
}
