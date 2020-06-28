package com.j2d2.graph

import com.j2d2.main.DataType
import com.j2d2.main.Terry

interface OnSelectedDataCallBack {
    fun setDataType(type:DataType, parcel: Terry )
}