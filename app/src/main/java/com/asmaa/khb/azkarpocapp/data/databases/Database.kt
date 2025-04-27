package com.asmaa.khb.azkarpocapp.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AzkarEntity::class], version = 1)
abstract class AzkarDatabase : RoomDatabase() {
    abstract fun azkarDao(): AzkarDao
}
