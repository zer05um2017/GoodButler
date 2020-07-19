package com.j2d2.DatabaseMigrationsExample

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity
data class Song(
    @PrimaryKey val id: Long,
    val title: String
)

/*
@Entity
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val tag: String // Added in version 2.
)
*/

// Migration from 1 to 2, Room 2.1.0
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Song ADD COLUMN tag TEXT NOT NULL DEFAULT ''")
    }
}
