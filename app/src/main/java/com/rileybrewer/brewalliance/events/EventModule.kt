package com.rileybrewer.brewalliance.events

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.rileybrewer.brewalliance.proto.DistrictEvents
import com.rileybrewer.brewalliance.proto.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventModule {
    @Singleton
    @Provides
    internal fun providesDistrictEventsDataStore(
        @ApplicationContext context: Context
    ): DataStore<DistrictEvents> {
        return DataStoreFactory.create(
            serializer = DistrictEventsSerializer
        ) {
            context.dataStoreFile("district_events.pb")
        }
    }

    @Singleton
    @Provides
    internal fun providesEventDataStore(
        @ApplicationContext context: Context
    ): DataStore<Event> {
        return DataStoreFactory.create(
            serializer = EventSerializer
        ) {
            context.dataStoreFile("current_event.pb")
        }
    }
}