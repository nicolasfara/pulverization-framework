package it.nicolasfarabegoli.pulverization.runtime.context

import it.nicolasfarabegoli.pulverization.component.Context
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

private const val FILE_SIZE = 255

internal actual suspend fun createContext(configFilePath: String): Context {
    val filePtr = fopen(configFilePath, "r") ?: error("Unable to open the file $configFilePath")
    val contentRaw = ByteArray(FILE_SIZE)
    contentRaw.usePinned {
        fgets(it.addressOf(0), FILE_SIZE, filePtr)
    }
    fclose(filePtr)
    val content = contentRaw.decodeToString()
    getKey("DEVICE_ID", content)?.let {
        return object : Context {
            override val deviceID: String = it
        }
    } ?: error("Unable to get the `DEVICE_ID` from config file")
}

internal fun getKey(key: String, content: String): String? {
    val regex = "(.+)=(.+)".toRegex()
    val matched = content.lines().mapNotNull { regex.find(it) }.map { it.destructured }
        .firstOrNull { (k, _) -> k == key }
    return matched?.let { (_, c) -> c }
}
