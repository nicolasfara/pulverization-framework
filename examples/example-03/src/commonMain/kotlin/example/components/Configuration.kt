package example.components

import it.nicolasfarabegoli.pulverization.core.BehaviourComponent
import it.nicolasfarabegoli.pulverization.core.CommunicationComponent
import it.nicolasfarabegoli.pulverization.core.SensorsComponent
import it.nicolasfarabegoli.pulverization.core.StateComponent
import it.nicolasfarabegoli.pulverization.dsl.Cloud
import it.nicolasfarabegoli.pulverization.dsl.Device
import it.nicolasfarabegoli.pulverization.dsl.Edge
import it.nicolasfarabegoli.pulverization.dsl.pulverizationConfig

val config = pulverizationConfig {
    logicalDevice("gps") {
        // BehaviourComponent and StateComponent deployableOn Cloud
        BehaviourComponent deployableOn Cloud
        StateComponent deployableOn Cloud
        CommunicationComponent deployableOn Edge
        SensorsComponent deployableOn Device
    }
}
