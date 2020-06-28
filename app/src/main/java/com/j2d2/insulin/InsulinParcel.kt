package com.j2d2.insulin

import android.os.Parcel
import android.os.Parcelable
import com.j2d2.main.Terry
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsulinParcel(var millis:Long,
                    var dataType:Int,
                    var type:Int,
                    var undiluted: Float,
                    var totalCapacity:Int,
                    var dilution:Int,
                    var remark: String?) : Terry {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    ) {}

    companion object : Parceler<InsulinParcel> {

        override fun InsulinParcel.write(parcel: Parcel, flags: Int) {
            parcel.writeLong(millis)
            parcel.writeInt(dataType)
            parcel.writeInt(type)
            parcel.writeFloat(undiluted)
            parcel.writeInt(totalCapacity)
            parcel.writeInt(dilution)
            parcel.writeString(remark)
        }

        override fun create(parcel: Parcel): InsulinParcel {
            return InsulinParcel(parcel)
        }
    }
}