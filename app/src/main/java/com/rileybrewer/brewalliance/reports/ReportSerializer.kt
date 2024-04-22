package com.rileybrewer.brewalliance.reports

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.rileybrewer.brewalliance.proto.Reports
import java.io.InputStream
import java.io.OutputStream

/** Serializer for writing Report info to storage. */
object ReportSerializer: Serializer<Reports> {
    override val defaultValue: Reports = Reports.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Reports {
        try {
            return Reports.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Reports, output: OutputStream) = t.writeTo(output)
}
