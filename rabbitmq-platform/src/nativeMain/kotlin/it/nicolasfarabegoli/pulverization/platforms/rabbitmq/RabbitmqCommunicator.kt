package it.nicolasfarabegoli.pulverization.platforms.rabbitmq

import com.autodesk.coroutineworker.CoroutineWorker
import it.nicolasfarabegoli.pulverization.runtime.communication.Binding
import it.nicolasfarabegoli.pulverization.runtime.communication.Communicator
import it.nicolasfarabegoli.pulverization.runtime.communication.RemotePlace
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import librabbitmq.AMQP_REPLY_SUCCESS
import librabbitmq.AMQP_RESPONSE_NORMAL
import librabbitmq.AMQP_SASL_METHOD_PLAIN
import librabbitmq.amqp_basic_publish
import librabbitmq.amqp_channel_close
import librabbitmq.amqp_channel_open
import librabbitmq.amqp_connection_close
import librabbitmq.amqp_connection_state_t
import librabbitmq.amqp_consume_message
import librabbitmq.amqp_destroy_connection
import librabbitmq.amqp_envelope_t
import librabbitmq.amqp_get_rpc_reply
import librabbitmq.amqp_login
import librabbitmq.amqp_maybe_release_buffers
import librabbitmq.amqp_new_connection
import librabbitmq.amqp_rpc_reply_t
import librabbitmq.amqp_socket_open
import librabbitmq.amqp_tcp_socket_new
import kotlin.coroutines.coroutineContext

/**
 * Implement the [Communicator] interface relying on RabbitMQ as a platform for communications.
 */
actual class RabbitmqCommunicator actual constructor(
    private val hostname: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val virtualHost: String,
) : Communicator {

    private var connection: amqp_connection_state_t? = null
    private val flow = MutableSharedFlow<ByteArray>(1)
    private lateinit var receivingJobRef: CoroutineWorker

    companion object {
        private const val MAX_FRAME_SIZE = 131072
    }

    override suspend fun setup(binding: Binding, remotePlace: RemotePlace?) = coroutineScope {
        connection = amqp_new_connection() ?: error("Error creating the connection")
        val socket = amqp_tcp_socket_new(connection) ?: error("creating TCP socket failed")

        val status = amqp_socket_open(socket, hostname, port)
        if (status != 0) error("Error on opening socket TCP")

        amqp_login(connection, virtualHost, 0, MAX_FRAME_SIZE, 0, AMQP_SASL_METHOD_PLAIN, username, password)
            .dieOnAmqpError("Login")
        amqp_channel_open(connection, 1)
        amqp_get_rpc_reply(connection).dieOnAmqpError("Opening channel")

        receivingJobRef = CoroutineWorker.execute {
            var ret: CValue<amqp_rpc_reply_t>?
            val envelope: CValue<amqp_envelope_t> = cValue()
            while (true) {
                amqp_maybe_release_buffers(connection)
                ret = amqp_consume_message(connection, envelope, null, 0)
                ret.useContents {
                    if (reply_type != AMQP_RESPONSE_NORMAL) error("Error receiving messages")
                }
                envelope.useContents {
                    flow.emit(this.message.body.toKotlin().encodeToByteArray())
                }
            }
        }
    }

    override suspend fun finalize() {
        amqp_channel_close(connection, 1, AMQP_REPLY_SUCCESS).dieOnAmqpError("Closing channel")
        amqp_connection_close(connection, AMQP_REPLY_SUCCESS).dieOnAmqpError("Closing connection")
        amqp_destroy_connection(connection).dieOnError("Ending connection")
        receivingJobRef.cancelAndJoin()
    }

    override suspend fun fireMessage(message: ByteArray) {
        CoroutineWorker.withContext(coroutineContext) {
            amqp_basic_publish(
                connection,
                1,
                "amq.direct".toAMQP(), // Exchange
                "test".toAMQP(), // routing key
                0,
                0,
                null,
                message.decodeToString().toAMQP(),
            ).dieOnError("Send message")
        }
    }

    override fun receiveMessage(): Flow<ByteArray> = flow
}
