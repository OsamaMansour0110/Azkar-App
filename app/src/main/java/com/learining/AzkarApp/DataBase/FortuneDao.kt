package com.learining.AzkarApp.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learining.AzkarApp.Data.model.FortuneItem

@Dao
interface FortuneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fortune: FortuneItem)

    @Query("SELECT * FROM fortune")
    suspend fun getAllFortunes(): List<FortuneItem>

    @Query("UPDATE fortune SET score = score + :newScore WHERE id = :fortuneId")
    suspend fun updateScore(
        fortuneId: Int,
        newScore: Int
    )

    @Query("SELECT COUNT(*) FROM fortune")
    suspend fun getCount(): Int
}
