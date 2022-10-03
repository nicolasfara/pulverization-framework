package it.nicolasfarabegoli.pulverization.core

/**
 * This interface model the _Communication component_ in a pulverized context.
 * This component is responsible for providing the operations needed to communicate with other devices.
 * @param S the type of the message to send.
 * @param R the type of the message to receive.
 */
interface Communication<in S, out R> {
    /**
     * Abstraction of the _sending action_ to other devices.
     */
    suspend fun send(payload: S)

    /**
     * Abstraction of the _receiving action_ from other devices.
     */
    suspend fun receive(): R
}
