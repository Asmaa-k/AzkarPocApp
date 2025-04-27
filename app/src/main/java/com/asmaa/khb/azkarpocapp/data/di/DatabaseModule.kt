package com.asmaa.khb.azkarpocapp.data.di

import android.content.Context
import androidx.room.Room
import com.asmaa.khb.azkarpocapp.data.databases.AzkarDao
import com.asmaa.khb.azkarpocapp.data.databases.AzkarDatabase
import com.asmaa.khb.azkarpocapp.data.util.Constants.DATA_BASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AzkarDatabase =
        Room.databaseBuilder(context, AzkarDatabase::class.java, DATA_BASE_NAME).build()

    @Provides
    fun provideDao(db: AzkarDatabase): AzkarDao = db.azkarDao()
}