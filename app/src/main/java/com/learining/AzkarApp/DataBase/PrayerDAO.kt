package com.learining.AzkarApp.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learining.AzkarApp.Data.model.MissedPrayers
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDAO {

    @Query("SELECT * FROM missed_prayers WHERE id = 1")
    fun getMissedPrayers(): Flow<MissedPrayers?>

    @Query(
        """
    UPDATE missed_prayers 
    SET 
        fajr = fajr + 1,
        dhuhr = dhuhr + 1,
        asr = asr + 1,
        maghrib = maghrib + 1,
        isha = isha + 1
    WHERE id = 1
"""
    )
    suspend fun incrementAllPrayers()


    @Query(
        """
    UPDATE missed_prayers
    SET 
        fajr = CASE WHEN fajr > 0 THEN fajr - 1 ELSE 0 END,
        dhuhr = CASE WHEN dhuhr > 0 THEN dhuhr - 1 ELSE 0 END,
        asr = CASE WHEN asr > 0 THEN asr - 1 ELSE 0 END,
        maghrib = CASE WHEN maghrib > 0 THEN maghrib - 1 ELSE 0 END,
        isha = CASE WHEN isha > 0 THEN isha - 1 ELSE 0 END
    WHERE id = 1
    """
    )
    suspend fun decrementAllPrayersSafely()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(prayer: MissedPrayers)
}