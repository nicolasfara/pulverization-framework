package it.nicolasfarabegoli.pulverization.runtime.componentsref

import it.nicolasfarabegoli.pulverization.core.BehaviourComponent
import it.nicolasfarabegoli.pulverization.core.StateComponent
import it.nicolasfarabegoli.pulverization.runtime.communication.Communicator
import kotlinx.serialization.KSerializer

interface StateRef<S : Any> : ComponentRef<S> {
    companion object {
        fun <S : Any> create(
            serializer: KSerializer<S>,
            communicator: Communicator,
            exists: Boolean = true,
        ): StateRef<S> {
            return if (!exists) NoOpStateRef()
            else StateRefImpl(serializer, communicator)
        }
    }
}

internal class StateRefImpl<S : Any>(
    private val serializer: KSerializer<S>,
    private val communicator: Communicator,
) : ComponentRef<S> by ComponentRefImpl(serializer, BehaviourComponent to StateComponent, communicator),
    StateRef<S>

internal class NoOpStateRef<S : Any> : ComponentRef<S> by NoOpComponentRef(), StateRef<S>
