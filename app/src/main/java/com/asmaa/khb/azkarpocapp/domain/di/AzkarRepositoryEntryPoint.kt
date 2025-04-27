package com.asmaa.khb.azkarpocapp.domain.di

import com.asmaa.khb.azkarpocapp.domain.repos.AzkarRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AzkarRepositoryEntryPoint {
    fun azkarRepository(): AzkarRepository
}
