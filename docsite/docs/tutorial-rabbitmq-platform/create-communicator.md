---
sidebar_position: 2
---

# Create Communicators

In the previous section, we figure out how to write the "pure" pulverized components. In this section, we provide a
guide on how to bind the previously defined components with the corresponding platform-specific communicator, in this
case, **RabbitMQ**.  
Importing the `it.nicolasfarabegoli.pulverization-framework:rabbitmq-platform:<version>` module, the following class are
available to enable inter-component communication:

- `SimpleRabbitmqSenderCommunicator`
- `SimpleRabbitmqReceiverCommunicator`
- `SimpleRabbitmqBidirectionalCommunicator`

The first one enables the communication between two components $A \rightarrow B$ where $A$ sends messages to $B$.  
The second component enables the communication between two components $A \leftarrow B$ where $B$ sends messages to
$A$.  
The last components combine the functionality of the first two communicators to achieve a bi-directional communication
between $A \leftrightarrow B$.

All three communicators manage internally all the **RabbitMQ**-specific stuff to provide a simple and clean API
for the user. The only thing that the user should specify is the type of communication which specify from which
component the communication occurs.

```kotlin
SimpleRabbitmqReceiverCommunicator<AllSensorsPayload>(SensorsComponent to BehaviourComponent)
```

The code above shows how to create a receiver communicator. The type parameter determines the type of message the
two components share. In the example, we create a receiver that receives messages from the `SensorsComponent` to
the `BehaviourComponent`. The pair passed to the constructor follows the semantic _upstream/downstream_.

The sender communicator is identical and also in this case the semantics of the pair passed to the constructor follow
the semantic of _upstream/downstream_.

In the bidirectional communicator, the semantic of the pair is slightly different: the semantic assumed is again _
upstream/downstream_ but in this case from the sender's perspective.

For example, if we create the following communicator:

```kotlin
SimpleRabbitmqBidirectionalCommunicator<String>(SensorsComponent to BehaviourComponent)
```

we say that we want a bidirectional communication between the `Sensors` and `Behaviour` components where who will use
the class take the part of the sensor.
So we can express the communication like  
$Sensors \xrightarrow{\text{send}} Behaviour$ and  
$Sensor \xleftarrow{\text{receive}} Behaviour$.

The semantic change if we create the same communicator bu with the pair inverted

```kotlin
SimpleRabbitmqBidirectionalCommunicator<String, String>(BehaviourComponent to SensorsComponent)
```

in this case the communication is  
$Behaviour \xrightarrow{\text{send}} Sensors$ and  
$Behaviour \xleftarrow{\text{receive}} Sensor$.

:::caution
Please, pay attention when specify the communication type.
:::