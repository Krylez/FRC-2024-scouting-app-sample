package com.rileybrewer.brewalliance.events

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.rileybrewer.brewalliance.proto.Event
import java.io.InputStream
import java.io.OutputStream

/** Serializer for writing Event info to storage. */
object EventSerializer: Serializer<Event> {
    override val defaultValue: Event = Event.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Event {
        try {
            return Event.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Event,
        output: OutputStream
    ) = t.writeTo(output)
}
