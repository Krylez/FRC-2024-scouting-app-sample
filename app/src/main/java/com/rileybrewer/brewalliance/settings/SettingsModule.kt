package com.rileybrewer.brewalliance.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.rileybrewer.brewalliance.proto.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {
    @Singleton
    @Provides
    internal fun providesSettingsStore(
        @ApplicationContext context: Context
    ): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer
        ) {
            context.dataStoreFile("settings.pb")
        }
    }
}