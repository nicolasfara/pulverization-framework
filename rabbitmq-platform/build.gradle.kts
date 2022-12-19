import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":platform"))
            }
        }
        jvmMain {
            dependencies {
                implementation(rootProject.libs.kotlinx.coroutines.reactive)
                implementation(rootProject.libs.kotlinx.coroutines.reactor)
                api("io.projectreactor.rabbitmq:reactor-rabbitmq:1.5.5")
            }
        }
        jvmTest {
            dependencies {
                implementation(rootProject.libs.kotlinx.coroutines.reactive)
                implementation(rootProject.libs.kotlinx.coroutines.reactor)
                implementation("com.github.fridujo:rabbitmq-mock:1.2.0")
            }
        }
    }

    fun KotlinNativeTarget.librabbitmqCInterop(target: String) {
        compilations["main"].cinterops {
            val librabbitmq by creating {
                tasks[interopProcessingTaskName].dependsOn(
                    ":rabbitmq-platform:native:buildLibrabbitmq${target.capitalized()}",
                )
                includeDirs.headerFilterOnly(
                    project.file("native/rabbitmq-c/build/include/"),
                    project.file("native/rabbitmq-c/include"),
                    project.file("/usr/include/"),
                )
            }
        }
    }

    val setupNative: KotlinNativeTargetWithHostTests.() -> Unit = {
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["nativeMain"])
        librabbitmqCInterop("host")
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-include-binary",
            "$projectDir/native/build/linux/librabbitmq.a",
        )
    }

    linuxX64(setupNative)
    macosX64(setupNative)
    mingwX64(setupNative)
}
