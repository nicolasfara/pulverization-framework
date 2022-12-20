package example.components

import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Sensor
import it.nicolasfarabegoli.pulverization.core.SensorsContainer
import it.nicolasfarabegoli.pulverization.runtime.componentsref.BehaviourRef
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.core.component.inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Gps(val long: Double, val lat: Double)

@Serializable
data class DeviceSensors(val gps: Gps)

class GpsSensor : Sensor<Gps> {
    override fun sense(): Gps = Gps(Random.nextDouble(-180.0, 180.0), Random.nextDouble(-90.0, 90.0))
}

class LocalizationSensor : SensorsContainer() {
    override val context: Context by inject()

    override suspend fun initialize() {
        this += GpsSensor()
    }
}

suspend fun mySensorsLogic(sensors: SensorsContainer, behaviour: BehaviourRef<DeviceSensors>): Unit = coroutineScope {
    sensors.get<GpsSensor> {
        repeat(10) {
            val payload = DeviceSensors(sense())
            behaviour.sendToComponent(payload)
            println("send complete")
            delay(2.seconds)
            println("GoOn")
        }
    }
}
