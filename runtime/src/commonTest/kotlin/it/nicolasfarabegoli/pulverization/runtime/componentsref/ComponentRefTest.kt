package it.nicolasfarabegoli.pulverization.runtime.componentsref

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import it.nicolasfarabegoli.pulverization.core.BehaviourComponent
import it.nicolasfarabegoli.pulverization.core.StateComponent
import it.nicolasfarabegoli.pulverization.runtime.communication.CommManager
import it.nicolasfarabegoli.pulverization.runtime.communication.LocalCommunicator
import it.nicolasfarabegoli.pulverization.runtime.dsl.PulverizationKoinModule
import it.nicolasfarabegoli.pulverization.runtime.dsl.StatePayload
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest

class ComponentRefTest : FreeSpec(), KoinTest {
    private val module = module { single { CommManager() } }

    init {
        "When creating a ComponentRef" - {
            "with a concrete implementation" - {
                "should rely on the communicator for communicating with another component" {
                    PulverizationKoinModule.koinApp = koinApplication { modules(module) }
                    val stateJob = launch {
                        val behaviourRef =
                            ComponentRefImpl<StatePayload>(StateComponent to BehaviourComponent, LocalCommunicator())
                        behaviourRef.setup()
                        behaviourRef.receiveFromComponent().first() shouldBe StatePayload(1)
                    }
                    val behaviourJob = launch {
                        val stateRef =
                            ComponentRefImpl<StatePayload>(BehaviourComponent to StateComponent, LocalCommunicator())
                        stateRef.setup()
                        stateRef.sendToComponent(StatePayload(1))
                    }
                    behaviourJob.join()
                    stateJob.join()
                }
            }
        }
    }
}
