package it.nicolasfarabegoli.pulverization.communication

import it.nicolasfarabegoli.pulverization.core.DeviceID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Models the ability to (only) send messages to other components.
 */
interface SenderCommunicator<in Send, I : DeviceID> {
    /**
     * The device ID.
     */
    val id: I

    /**
     * Send the [payload] [to] another component.
     */
    suspend fun sendToComponent(payload: Send, to: I? = null)
}

/**
 * Models the ability to (only) receive messages from other components.
 */
interface ReceiverCommunicator<out Receive, I : DeviceID> {
    /**
     * The device ID.
     */
    val id: I

    /**
     * Receive a message [from] a component.
     */
    suspend fun receiveFromComponent(from: I? = null): Receive?

    /**
     * Creates an async flow with all received messages.
     */
    fun receiveFromComponent(): Flow<Receive> = emptyFlow()
}

/**
 * Models the ability to send and receive messages to and from other components.
 */
interface BidirectionalCommunicator<in Send, out Receive, I : DeviceID> :
    SenderCommunicator<Send, I>, ReceiverCommunicator<Receive, I>
