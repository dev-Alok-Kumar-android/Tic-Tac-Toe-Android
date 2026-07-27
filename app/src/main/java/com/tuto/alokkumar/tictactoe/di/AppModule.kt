package com.tuto.alokkumar.tictactoe.di

import android.content.Context
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.core.service.AndroidHardwareService
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.data.repository.GameRepositoryImpl
import com.tuto.alokkumar.tictactoe.domain.HardwareService
import com.tuto.alokkumar.tictactoe.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindHardwareService(impl: AndroidHardwareService): HardwareService

    companion object {
        @Provides
        @Singleton
        fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
            return PreferencesManager(context)
        }

        @Provides
        @Singleton
        fun provideSoundManager(): SoundManager {
            return SoundManager()
        }
    }
}
