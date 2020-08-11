package com.github.agcom.bson.streaming

import com.github.agcom.bson.BsonEncodingException
import org.bson.*
import org.bson.BsonType.*
import org.bson.io.BsonOutput

internal fun BsonOutput.writeBson(bson: BsonValue) {
    when (bson.bsonType) {
        DOUBLE, STRING, BINARY, OBJECT_ID, BOOLEAN, DATE_TIME, NULL, REGULAR_EXPRESSION, JAVASCRIPT, INT32, INT64, DECIMAL128 ->
            writePrimitive(bson)
        DOCUMENT -> TODO()
        ARRAY -> TODO()
        else -> throw BsonEncodingException("Unexpected bson type '${bson.bsonType}'")
    }
}

private fun BsonOutput.writePrimitive(bson: BsonValue) {
    writeByte(bson.bsonType.value)
    when (bson.bsonType) {
        DOUBLE -> writeDouble(bson.asDouble().value)
        STRING -> writeString(bson.asString().value)
        BINARY -> {
            bson.asBinary(); bson as BsonBinary
            var totalLen: Int = bson.data.size
            if (bson.type == BsonBinarySubType.OLD_BINARY.value) totalLen += 4
            writeInt32(totalLen)
            writeByte(bson.type.toInt())
            if (bson.type == BsonBinarySubType.OLD_BINARY.value) writeInt32(totalLen - 4)
            writeBytes(bson.data)
        }
        OBJECT_ID -> writeObjectId(bson.asObjectId().value)
        BOOLEAN -> writeByte(if (bson.asBoolean().value) 1 else 0)
        DATE_TIME -> writeInt64(bson.asDateTime().value)
        NULL -> { /* Nothing */ }
        REGULAR_EXPRESSION -> {
            bson.asRegularExpression(); bson as BsonRegularExpression
            writeCString(bson.pattern)
            writeCString(bson.options)
        }
        JAVASCRIPT -> writeString(bson.asJavaScript().code)
        INT32 -> writeInt32(bson.asInt32().value)
        INT64 -> writeInt64(bson.asInt64().value)
        DECIMAL128 -> {
            bson.asDecimal128(); bson as BsonDecimal128
            writeInt64(bson.value.low)
            writeInt64(bson.value.high)
        }
        else -> throw BsonEncodingException("Unexpected bson type '${bson.bsonType}'")
    }
}