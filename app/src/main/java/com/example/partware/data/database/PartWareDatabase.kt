package com.example.partware.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CatalogPartEntity::class], version = 1, exportSchema = false)
abstract class PartWareDatabase : RoomDatabase() {
    abstract fun catalogPartDao(): CatalogPartDao

    companion object {
        @Volatile private var INSTANCE: PartWareDatabase? = null
        fun get(context: Context): PartWareDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, PartWareDatabase::class.java, "partware.db")
                .createFromAsset("database/partware.db")
                .build().also { INSTANCE = it }
        }
    }
}
