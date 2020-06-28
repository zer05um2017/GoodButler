package com.j2d2.bloodglucose

import android.os.Parcel
import android.os.Parcelable
import com.j2d2.main.Terry
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BloodGlucoseParcel(var millis: Long,
                         var dataType: Int,
                         var bloodSugar: Int) : Terry {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt()){}

    companion object : Parceler<BloodGlucoseParcel> {

        override fun BloodGlucoseParcel.write(parcel: Parcel, flags: Int) {
            parcel.writeLong(millis)
            parcel.writeInt(dataType)
            parcel.writeInt(bloodSugar)
        }

        override fun create(parcel: Parcel): BloodGlucoseParcel {
            return BloodGlucoseParcel(parcel)
        }
    }
}