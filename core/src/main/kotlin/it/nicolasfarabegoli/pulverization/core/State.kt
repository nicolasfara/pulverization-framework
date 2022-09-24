package it.nicolasfarabegoli.pulverization.core

/**
 * Model the concept of _State_ in the pulverization context.
 * @param S the type of the state.
 */
interface State<S> {
    /**
     * The device state.
     */
    var state: S

    /**
     * Update the [state] with a [newState].
     */
    fun updateState(newState: S)
}
