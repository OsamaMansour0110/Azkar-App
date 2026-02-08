package com.learining.AzkarApp.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learining.AzkarApp.Data.model.AzkarItem
import com.learining.AzkarApp.Data.model.FastingDayItem
import com.learining.AzkarApp.Data.model.FortuneItem
import com.learining.AzkarApp.Data.model.MissedPrayers

@Database(
    entities = [AzkarItem::class, MissedPrayers::class, FastingDayItem::class, FortuneItem::class],
    version = 2
)
abstract class MyDataBase : RoomDatabase() {
    abstract fun zekrDAO(): AzkarDAO
    abstract fun PrayerDAO(): PrayerDAO
    abstract fun FastingDAO(): FastingDAO
    abstract fun fortuneDao(): FortuneDao
}