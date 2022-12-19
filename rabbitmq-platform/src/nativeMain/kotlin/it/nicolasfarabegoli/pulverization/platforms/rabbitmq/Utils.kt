package it.nicolasfarabegoli.pulverization.platforms.rabbitmq

import kotlinx.cinterop.ArenaBase
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import kotlinx.cinterop.utf8
import librabbitmq.AMQP_CHANNEL_CLOSE_METHOD
import librabbitmq.AMQP_CONNECTION_CLOSE_METHOD
import librabbitmq.AMQP_RESPONSE_LIBRARY_EXCEPTION
import librabbitmq.AMQP_RESPONSE_NONE
import librabbitmq.AMQP_RESPONSE_NORMAL
import librabbitmq.AMQP_RESPONSE_SERVER_EXCEPTION
import librabbitmq.amqp_bytes_t
import librabbitmq.amqp_channel_close_t
import librabbitmq.amqp_connection_close_t
import librabbitmq.amqp_rpc_reply_t

@Suppress("UNCHECKED_CAST")
internal fun CValue<amqp_rpc_reply_t>.dieOnAmqpError(reason: String): Unit = useContents {
    when (reply_type) {
        AMQP_RESPONSE_NORMAL -> Unit
        AMQP_RESPONSE_NONE -> error("$reason: missing RPC reply type!")
        AMQP_RESPONSE_LIBRARY_EXCEPTION -> error("$reason, library error: $library_error")
        AMQP_RESPONSE_SERVER_EXCEPTION -> {
            when (reply.id) {
                AMQP_CONNECTION_CLOSE_METHOD -> {
                    (reply.decoded as CPointer<amqp_connection_close_t>).pointed.let {
                        error("$reason: server channel error ${it.reply_code}")
                    }
                }

                AMQP_CHANNEL_CLOSE_METHOD -> {
                    (reply.decoded as CPointer<amqp_channel_close_t>).pointed.let {
                        error("$reason: server channel error ${it.reply_code}")
                    }
                }

                else -> error("$reason: Unknown server error")
            }
        }
    }
}

internal fun String.toAMQP(): CValue<amqp_bytes_t> {
    return cValue {
        len = this@toAMQP.length.toULong()
        bytes = this@toAMQP.utf8.getPointer(ArenaBase())
    }
}

internal fun amqp_bytes_t.toKotlin(): String {
    return bytes?.reinterpret<ByteVar>()?.toKString() ?: error("Try to decode a string from null pointer")
}

internal fun Int.dieOnError(reason: String) {
    if (this < 0) error("$reason: error $this")
}
