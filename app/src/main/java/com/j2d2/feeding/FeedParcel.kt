package com.j2d2.feeding

import android.os.Parcel
import android.os.Parcelable
import com.j2d2.main.Terry

class FeedParcel(var millis:Long,
                var dataType:Int,
                var type:Int,
                var brandName: String?,
                var totalCapacity:Int,
                var remark: String?
) : Terry {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(millis)
        parcel.writeInt(dataType)
        parcel.writeInt(type)
        parcel.writeString(brandName)
        parcel.writeInt(totalCapacity)
        parcel.writeString(remark)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedParcel> {
        override fun createFromParcel(parcel: Parcel): FeedParcel {
            return FeedParcel(parcel)
        }

        override fun newArray(size: Int): Array<FeedParcel?> {
            return arrayOfNulls(size)
        }
    }
}