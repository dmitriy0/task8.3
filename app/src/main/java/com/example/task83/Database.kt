package com.example.task83

import androidx.room.RoomDatabase
import androidx.room.Database

@Database(entities = [EntityImages::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun dao(): Dao?
}