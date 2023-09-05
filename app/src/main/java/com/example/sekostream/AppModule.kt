package com.example.sekostream

import com.example.sekostream.media.RtcTokenBuilder2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenBuilder(): RtcTokenBuilder2 {
        return RtcTokenBuilder2()
    }

    @Provides
    @Singleton
    fun provideRepository(tokenBuilder: RtcTokenBuilder2): TokenRepository {
        return TokenRepository(tokenBuilder)
    }
}