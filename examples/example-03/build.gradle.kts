import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

fun ShadowJar.genericJarConfig(jarName: String, mainClass: String) {
    archiveClassifier.set("all")
    archiveBaseName.set(jarName)
    archiveVersion.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
    manifest {
        attributes("Main-Class" to mainClass)
    }
    val main by kotlin.jvm().compilations
    from(main.output)
    configurations += main.compileDependencyFiles as Configuration
    configurations += main.runtimeDependencyFiles as Configuration
}

kotlin {
    jvm {
        apply(plugin = "com.github.johnrengelman.shadow")

        tasks {
            register("generateJars") {
                dependsOn("sensorsJar", "behaviourJar", "communicationJar", "stateJar")
            }
            register<ShadowJar>("stateJar") {
                genericJarConfig("state", "example.units.StateUnitKt")
            }
            register<ShadowJar>("behaviourJar") {
                genericJarConfig("behaviour", "example.units.BehaviourUnitKt")
            }
            register<ShadowJar>("communicationJar") {
                genericJarConfig("communication", "example.units.CommunicationUnitKt")
            }
            register<ShadowJar>("sensorsJar") {
                genericJarConfig("sensors", "example.units.SensorsUnitKt")
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":platform"))
                implementation(project(":rabbitmq-platform"))
            }
        }
        jvmMain {
            dependencies {
                implementation("io.projectreactor.rabbitmq:reactor-rabbitmq:1.5.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
            }
        }
    }

    val hostOs = System.getProperty("os.name").trim().toLowerCaseAsciiOnly()
    val hostArch = System.getProperty("os.arch").trim().toLowerCaseAsciiOnly()
    val nativeTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinTarget =
        when (hostOs to hostArch) {
            "linux" to "aarch64" -> ::linuxArm64
            "linux" to "amd64" -> ::linuxX64
            "linux" to "arm", "linux" to "arm32" -> ::linuxArm32Hfp
            "linux" to "mips", "linux" to "mips32" -> ::linuxMips32
            "linux" to "mipsel", "linux" to "mips32el" -> ::linuxMipsel32
            "mac os x" to "aarch64" -> ::macosArm64
            "mac os x" to "amd64", "mac os x" to "x86_64" -> ::macosX64
            "windows" to "amd64", "windows server 2022" to "amd64" -> ::mingwX64
            "windows" to "x86" -> ::mingwX86
            else -> throw GradleException(
                "Host OS '$hostOs' with arch '$hostArch' is not supported in Kotlin/Native.",
            )
        }

    nativeTarget("native") {
        binaries {
            "example.units.main".let {
                executable { entryPoint = it }
            }
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        enabled = false
    }
}
