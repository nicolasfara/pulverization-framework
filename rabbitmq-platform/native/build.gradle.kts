import org.gradle.internal.os.OperatingSystem

val currentOs: OperatingSystem = OperatingSystem.current()
val bash = if (currentOs.isWindows) "bash.exe" else "bash"

val buildLibrabbitmq: Task by tasks.creating { group = "build" }

// Build librabbitmq for the host platform's architecture (linux/macOS/windows).
val buildLibrabbitmqHost by tasks.creating(Exec::class) {
    group = "build"
    buildLibrabbitmq.dependsOn(this)

    val target = when {
        currentOs.isLinux -> "linux"
        currentOs.isMacOsX -> "darwin"
        currentOs.isWindows -> "mingw"
        else -> error("Unsupported OS $currentOs")
    }

    inputs.files(projectDir.resolve("build.sh"))
    outputs.dir(projectDir.resolve("build/$target"))

    workingDir = projectDir
    environment("TARGET", target)
    commandLine(bash, "build.sh")
}

val cleanBuild by tasks.creating {
    group = "build"
    dependsOn(tasks.clean)
    doLast {
        delete(projectDir.resolve("rabbitmq-c/build"))
    }
}
