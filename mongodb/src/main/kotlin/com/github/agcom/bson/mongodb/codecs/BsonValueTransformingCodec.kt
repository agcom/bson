package com.github.agcom.bson.mongodb.codecs

import org.bson.BsonReader
import org.bson.BsonValue
import org.bson.BsonWriter
import org.bson.codecs.BsonValueCodec
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

abstract class BsonValueTransformingCodec<T> : Codec<T> {

    companion object {
        val bsonCodec: Codec<BsonValue> = BsonValueCodec()
    }

    final override fun encode(writer: BsonWriter, value: T, encoderContext: EncoderContext) {
        bsonCodec.encode(writer, toBson(value), encoderContext)
    }

    final override fun decode(reader: BsonReader, decoderContext: DecoderContext): T {
        return fromBson(bsonCodec.decode(reader, decoderContext))
    }

    abstract fun toBson(value: T): BsonValue
    abstract fun fromBson(value: BsonValue): T

}