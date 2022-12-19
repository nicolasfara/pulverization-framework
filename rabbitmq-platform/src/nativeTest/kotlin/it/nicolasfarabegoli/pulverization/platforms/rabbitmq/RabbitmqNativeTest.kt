package it.nicolasfarabegoli.pulverization.platforms.rabbitmq

import io.kotest.core.spec.style.FreeSpec
import it.nicolasfarabegoli.pulverization.core.BehaviourComponent
import it.nicolasfarabegoli.pulverization.core.StateComponent

class RabbitmqNativeTest : FreeSpec() {
    init {
        "Native implementation" - {
            "fer" {
                val comm = RabbitmqCommunicator()
                comm.setup(StateComponent to BehaviourComponent, null)
                comm.fireMessage("my test 1".encodeToByteArray())
                comm.finalize()
            }
        }
    }
}
