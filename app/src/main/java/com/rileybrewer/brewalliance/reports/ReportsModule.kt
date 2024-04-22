package com.rileybrewer.brewalliance.reports

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.rileybrewer.brewalliance.proto.Reports
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ReportsModule {
    @Singleton
    @Provides
    internal fun providesReportsStore(
        @ApplicationContext context: Context
    ): DataStore<Reports> {
        return DataStoreFactory.create(
            serializer = ReportSerializer
        ) {
            context.dataStoreFile("reports.pb")
        }
    }
}
