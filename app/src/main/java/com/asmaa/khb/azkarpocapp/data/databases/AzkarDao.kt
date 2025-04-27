package com.asmaa.khb.azkarpocapp.data.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.asmaa.khb.azkarpocapp.data.util.Constants.AZKAR_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(azkar: AzkarEntity)

    @Query("SELECT * FROM $AZKAR_TABLE_NAME")
    fun getAllAzkar(): Flow<List<AzkarEntity?>>

    @Query("SELECT * FROM $AZKAR_TABLE_NAME ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomAzkar(): AzkarEntity?
}