package com.FowlFind.googlemaps

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Observation(
    val species: String,
    val observationDate: String,
    val notes: String,
    val image: Bitmap?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Bitmap::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(species)
        parcel.writeString(observationDate)
        parcel.writeString(notes)
        parcel.writeParcelable(image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Observation> {
        override fun createFromParcel(parcel: Parcel): Observation {
            return Observation(parcel)
        }

        override fun newArray(size: Int): Array<Observation?> {
            return arrayOfNulls(size)
        }
    }
}
