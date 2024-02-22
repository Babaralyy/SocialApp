package com.codecoy.mvpflycollab.datamodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AllPlaylistResponse(
    @SerializedName("success"  ) var success  : Boolean?            = null,
    @SerializedName("message"  ) var message  : String?             = null,
    @SerializedName("response" ) var allPlaylistData : ArrayList<AllPlaylistData> = arrayListOf()
):Parcelable {
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

    companion object CREATOR : Parcelable.Creator<AllPlaylistResponse> {
        override fun createFromParcel(parcel: Parcel): AllPlaylistResponse {
            return AllPlaylistResponse(parcel)
        }

        override fun newArray(size: Int): Array<AllPlaylistResponse?> {
            return arrayOfNulls(size)
        }
    }
}
