package it.nicolasfarabegoli.pulverization.runtime.componentsref

import it.nicolasfarabegoli.pulverization.core.BehaviourComponent
import it.nicolasfarabegoli.pulverization.core.StateComponent
import it.nicolasfarabegoli.pulverization.core.StateRepresentation
import it.nicolasfarabegoli.pulverization.runtime.communication.Communicator
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Represent a reference to the _state_ component in a pulverized system.
 */
interface StateRef<S : StateRepresentation> : ComponentRef<S> {
    companion object {
        /**
         * Create a [StateRef] specifying the [serializer] and the [communicator] to be used.
         */
        fun <S : StateRepresentation> create(serializer: KSerializer<S>, communicator: Communicator): StateRef<S> =
            StateRefImpl(serializer, communicator)

        /**
         * Create a [StateRef] specifying the [communicator] to be used.
         */
        inline fun <reified S : StateRepresentation> create(communicator: Communicator): StateRef<S> =
            create(serializer(), communicator)

        /**
         * Create a fake component reference.
         * All the methods implementation with this instance do nothing.
         */
        fun <S : StateRepresentation> createDummy(): StateRef<S> = NoOpStateRef()
    }
}

internal data class StateRefImpl<S : StateRepresentation>(
    private val serializer: KSerializer<S>,
    private val communicator: Communicator,
) : ComponentRef<S> by ComponentRefImpl(serializer, BehaviourComponent to StateComponent, communicator),
    StateRef<S>

internal class NoOpStateRef<S : StateRepresentation> : ComponentRef<S> by NoOpComponentRef(), StateRef<S>
