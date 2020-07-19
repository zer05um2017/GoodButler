package com.j2d2.petinfo
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "breed")
data class Breed (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,      // ID
    @ColumnInfo(name = "name") val name:String  // name
)



//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("CREATE TABLE 'Fruit' ('id' INTEGER, 'name' TEXT, " +
//                "PRIMARY KEY('id'))")
//    }
//}
//
//val MIGRATION_2_3 = object : Migration(2, 3) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE Book ADD COLUMN pub_year INTEGER")
//    }
//}