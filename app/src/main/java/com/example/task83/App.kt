package com.example.task83

import android.app.Application
import androidx.room.Room


class App : Application() {
    private var database: Database? = null
    var needUpdateData = false
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, Database::class.java, "database")
            .build()
    }

    fun getDatabase(): Database? {
        return database
    }

    companion object {
        var instance: App? = null
    }
}