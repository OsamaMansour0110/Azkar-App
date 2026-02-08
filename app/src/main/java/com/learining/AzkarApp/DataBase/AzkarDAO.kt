package com.learining.AzkarApp.DataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learining.AzkarApp.Data.model.AzkarItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDAO {

    @Insert
    suspend fun addZekr(azkarItem: AzkarItem)

    @Update
    suspend fun updateZekr(azkarItem: AzkarItem)

    @Delete
    suspend fun deleteZekr(azkarItem: AzkarItem)

    @Query("DELETE FROM azkar WHERE text = :text")
    suspend fun deleteZekrByText(text: String)

    @Query("DELETE FROM azkar")
    suspend fun clearAzkar()

    @Query("SELECT * FROM azkar ORDER BY id DESC")
    fun getAllZekr(): Flow<List<AzkarItem>>

    // Pagination Query: LIMIT (how many to fetch) and OFFSET (how many to ignore)
    @Query("SELECT * FROM azkar ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedZekr(limit: Int, offset: Int): List<AzkarItem>

    @Query("SELECT EXISTS(SELECT 1 FROM azkar WHERE text = :value)")
    suspend fun isZekrExists(value: String): Boolean
}