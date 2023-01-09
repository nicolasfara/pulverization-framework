package example.units

import example.components.DeviceSensors
import example.components.LocalizationSensor
import example.components.mySensorsLogic
import it.nicolasfarabegoli.pulverization.dsl.getDeviceConfiguration
import it.nicolasfarabegoli.pulverization.platforms.rabbitmq.RabbitmqCommunicator
import it.nicolasfarabegoli.pulverization.platforms.rabbitmq.defaultRabbitMQRemotePlace
import it.nicolasfarabegoli.pulverization.runtime.dsl.PulverizationPlatformScope.Companion.sensorsLogic
import it.nicolasfarabegoli.pulverization.runtime.dsl.pulverizationPlatform
import kotlinx.coroutines.runBlocking

/**
 * State-Behaviour entrypoint.
 */
fun main() = runBlocking {
    val platform = pulverizationPlatform<Any, Any, DeviceSensors, Any, Unit>(config.getDeviceConfiguration("gps")!!) {
        sensorsLogic(LocalizationSensor(), ::mySensorsLogic)
        withPlatform { RabbitmqCommunicator(hostname = "rabbitmq") }
        withRemotePlace { defaultRabbitMQRemotePlace() }
    }
    val jobs = platform.start()
    jobs.forEach { it.join() }
    platform.stop()
}
