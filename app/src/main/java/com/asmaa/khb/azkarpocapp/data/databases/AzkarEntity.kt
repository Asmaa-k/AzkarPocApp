package com.asmaa.khb.azkarpocapp.data.databases

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asmaa.khb.azkarpocapp.data.util.Constants.AZKAR_TABLE_NAME

@Entity(tableName = AZKAR_TABLE_NAME)
data class AzkarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String
)