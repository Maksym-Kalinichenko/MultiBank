package com.realtime.data.di

import com.realtime.data.source.WebSocketDataSource
import com.realtime.network.source.DefaultWebSocketDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun provideWebsocketDataSource(
        webSocketDataSource: DefaultWebSocketDataSource
    ): WebSocketDataSource
}