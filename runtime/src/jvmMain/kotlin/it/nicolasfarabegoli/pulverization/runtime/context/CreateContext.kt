package it.nicolasfarabegoli.pulverization.runtime.context

import it.nicolasfarabegoli.pulverization.component.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File

internal actual suspend fun createContext(): Context {
    val deviceID = getIDFromFile() ?: getIDFromEnv() ?: error("Unable to find the device ID")
    return object : Context {
        override val deviceID: String = deviceID
    }
}

internal fun getIDFromEnv(): String? = System.getProperty("DEVICE_ID")
internal suspend fun getIDFromFile(): String? = coroutineScope {
    withContext(Dispatchers.IO) {
        val configFile = File(".pulverization.env")
        if (!configFile.exists()) {
            null
        } else {
            getKey("DEVICE_ID", configFile.readText())
        }
    }
}

internal fun getKey(key: String, content: String): String? {
    val regex = "(.+)=(.+)".toRegex()
    val matched = content.lines().mapNotNull { regex.find(it) }.map { it.destructured }
        .firstOrNull { (k, _) -> k == key }
    return matched?.let { (_, c) -> c }
}
