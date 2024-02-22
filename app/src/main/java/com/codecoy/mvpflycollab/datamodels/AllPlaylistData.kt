package com.codecoy.mvpflycollab.datamodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AllPlaylistData(
    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : Int?    = null,
    @SerializedName("title"        ) var title       : String? = null,
    @SerializedName("description"  ) var description : String? = null,
    @SerializedName("playlist_img" ) var playlistImg : String? = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null
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
        parcel.writeString(playlistImg)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllPlaylistData> {
        override fun createFromParcel(parcel: Parcel): AllPlaylistData {
            return AllPlaylistData(parcel)
        }

        override fun newArray(size: Int): Array<AllPlaylistData?> {
            return arrayOfNulls(size)
        }
    }
}
