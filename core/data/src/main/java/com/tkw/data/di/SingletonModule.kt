package com.tkw.data.di

import com.tkw.data.local.AlarmRepositoryImpl
import com.tkw.data.local.CupRepositoryImpl
import com.tkw.data.local.PrefDataRepositoryImpl
import com.tkw.data.local.SettingRepositoryImpl
import com.tkw.data.local.WaterRepositoryImpl
import com.tkw.database.AlarmDao
import com.tkw.database.CupDao
import com.tkw.database.FileMerger
import com.tkw.database.SettingDao
import com.tkw.database.WaterDao
import com.tkw.database.local.AlarmDaoImpl
import com.tkw.database.local.CupDaoImpl
import com.tkw.database.local.RealmMerger
import com.tkw.database.local.SettingDaoImpl
import com.tkw.database.local.WaterDaoImpl
import com.tkw.datastore.PrefDataSource
import com.tkw.datastore.local.PrefLocalDataSourceImpl
import com.tkw.domain.AlarmRepository
import com.tkw.domain.CupRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import com.tkw.domain.WaterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * broadcast receiver에서 받을 수 있도록 싱글톤 컴포넌트
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonModule {
    @Binds
    @Singleton
    abstract fun providePrefRepo(repo: PrefDataRepositoryImpl): PrefDataRepository

    @Binds
    @Singleton
    abstract fun providePrefDataSource(dataSource: PrefLocalDataSourceImpl): PrefDataSource

    @Binds
    @Singleton
    abstract fun provideAlarmRepo(repo: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun provideSettingRepo(repo: SettingRepositoryImpl): SettingRepository

    @Binds
    @Singleton
    abstract fun provideAlarmDao(dao: AlarmDaoImpl): AlarmDao

    @Binds
    @Singleton
    abstract fun provideMerger(merger: RealmMerger): FileMerger

    @Binds
    @Singleton
    abstract fun provideSettingDao(dao: SettingDaoImpl): SettingDao

    @Binds
    @Singleton
    abstract fun provideCupRepo(repo: CupRepositoryImpl): CupRepository

    @Binds
    @Singleton
    abstract fun provideWaterRepo(repo: WaterRepositoryImpl): WaterRepository

    @Binds
    @Singleton
    abstract fun provideCupDao(dao: CupDaoImpl): CupDao

    @Binds
    @Singleton
    abstract fun provideWaterDao(dao: WaterDaoImpl): WaterDao
}