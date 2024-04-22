package com.rileybrewer.brewalliance.events

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.rileybrewer.brewalliance.proto.DistrictEvents
import java.io.InputStream
import java.io.OutputStream

object DistrictEventsSerializer: Serializer<DistrictEvents> {
    override val defaultValue: DistrictEvents = DistrictEvents.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DistrictEvents {
        try {
            return DistrictEvents.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: DistrictEvents, output: OutputStream) = t.writeTo(output)
}
