package com.learining.AzkarApp.DataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learining.AzkarApp.Data.model.FastingDayItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingDAO {
    @Insert
    suspend fun addFastingDay(fastingDayItem: FastingDayItem)

    @Update
    suspend fun updateFastingDay(fastingDayItem: FastingDayItem)

    @Delete
    suspend fun deleteFastingDay(fastingDayItem: FastingDayItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFastingDays(list: List<FastingDayItem>)

    @Query("DELETE FROM fastingDay")
    suspend fun deleteAllFastingDays()

    @Query("SELECT * FROM fastingDay ORDER BY dateDay ASC")
    suspend fun getFastingDaysSortedByDate(): List<FastingDayItem>

    @Query("SELECT * FROM fastingDay ORDER BY dateDay ASC")
    fun getFastingDays(): Flow<List<FastingDayItem>>

}