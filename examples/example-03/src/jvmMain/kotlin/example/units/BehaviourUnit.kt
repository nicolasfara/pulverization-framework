package example.units

import example.components.BehaviourComp
import example.components.behaviourLogics
import example.components.config
import it.nicolasfarabegoli.pulverization.dsl.getDeviceConfiguration
import it.nicolasfarabegoli.pulverization.platforms.rabbitmq.RabbitmqCommunicator
import it.nicolasfarabegoli.pulverization.platforms.rabbitmq.defaultRabbitMQRemotePlace
import it.nicolasfarabegoli.pulverization.runtime.dsl.PulverizationPlatformScope.Companion.behaviourLogic
import it.nicolasfarabegoli.pulverization.runtime.dsl.pulverizationPlatform
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val platform = pulverizationPlatform(config.getDeviceConfiguration("gps")!!) {
        behaviourLogic(BehaviourComp(), ::behaviourLogics)
        // stateLogic(StateComp(), ::stateLogic)
        withPlatform { RabbitmqCommunicator(hostname = "rabbitmq") }
        withRemotePlace { defaultRabbitMQRemotePlace() }
    }
    val jobs = platform.start()
    jobs.forEach { it.join() }
    platform.stop()
}
