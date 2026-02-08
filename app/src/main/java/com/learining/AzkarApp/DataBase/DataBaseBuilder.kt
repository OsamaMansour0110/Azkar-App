package com.learining.AzkarApp.DataBase

import android.content.Context
import androidx.room.Room

object DataBaseBuilder {
    @Volatile
    private var INSTANCE: MyDataBase? = null

    fun getInstance(context: Context): MyDataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                MyDataBase::class.java,
                "APP_DB"
            ).fallbackToDestructiveMigration().build()
            INSTANCE = instance
            instance
        }
    }
}