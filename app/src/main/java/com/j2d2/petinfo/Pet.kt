package com.j2d2.petinfo
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet")
data class Pet (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,                  // ID
    @ColumnInfo(name = "name") val name: String,            // 이름
    @ColumnInfo(name = "birth") val birth: Long,            // 생년월일
    @ColumnInfo(name = "occur_date") val occurDate: Long,   // 발병일자
    @ColumnInfo(name = "breed") val breedType: Int,         // 품종코드
    @ColumnInfo(name = "breed_name") val breedName: String, // 품종명
    @ColumnInfo(name = "weight") val weight: Float,         // 몸무게
    @ColumnInfo(name = "sex") val sex: Int?,                // 성별   0:female 1:male
    @ColumnInfo(name = "remark") val remark: String?        // 기타
)