@file:Suppress("UndocumentedPublicFunction", "UnusedPrivateMember")

import io.gitlab.arturbosch.detekt.Detekt
import org.danilopianini.gradle.mavencentral.JavadocJar
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.conventionalCommits)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.gitSemVer)
}

val Provider<PluginDependency>.id get() = get().pluginId

tasks {
    create("uploadAll") {
        description = "Upload all artifacts"
        group = "publishing"
        dependsOn(
            "core:uploadAllPublicationsToMavenCentralNexus",
            "platform:uploadAllPublicationsToMavenCentralNexus",
            "rabbitmq-platform:uploadAllPublicationsToMavenCentralNexus",
        )
    }
    create("uploadAllGithub") {
        description = "Upload all artifacts to github"
        group = "publishing"
        dependsOn(
            "core:publishKotlinMultiplatformPublicationToGithubRepository",
            "platform:publishKotlinMultiplatformPublicationToGithubRepository",
            "rabbitmq-platform:publishKotlinMultiplatformPublicationToGithubRepository",
        )
    }
}

allprojects {
    with(rootProject.libs.plugins) {
        apply(plugin = kotlin.multiplatform.id)
        apply(plugin = detekt.id)
        apply(plugin = ktlint.id)
        apply(plugin = dokka.id)
        apply(plugin = kover.id)
        apply(plugin = publishOnCentral.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = kotlinx.serialization.id)
    }

    repositories {
        google()
        mavenCentral()
    }

    kotlin {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
                filter {
                    isFailOnNoMatchingTests = false
                }
                testLogging {
                    showExceptions = true
                    events = setOf(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                    )
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                }
            }
        }

        js(IR) {
            browser()
            nodejs()
            binaries.library()
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
                sharedLib()
                staticLib()
            }
        }

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(rootProject.libs.kotlin.stdlib)
                    implementation(rootProject.libs.koin.core)
                    implementation(rootProject.libs.kotlinx.coroutines.core)
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(rootProject.libs.bundles.kotlin.testing.common)
                    implementation(rootProject.libs.koin.test)
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(rootProject.libs.bundles.kotlin.testing.jvm)
                }
            }
            val nativeMain by getting {
                dependsOn(commonMain)
            }
            val nativeTest by getting {
                dependsOn(commonTest)
            }
        }
        targets.all {
            compilations.all {
                kotlinOptions {
                    allWarningsAsErrors = true
                }
            }
        }
    }

    tasks.dokkaJavadoc {
        enabled = false
    }
    tasks.withType<Detekt>().configureEach {
        exclude("**/*Test.kt", "**/*Fixtures.kt")
    }
    tasks.withType<JavadocJar>().configureEach {
        val dokka = tasks.dokkaHtml.get()
        dependsOn(dokka)
        from(dokka.outputDirectory)
    }

    detekt {
        parallel = true
        buildUponDefaultConfig = true
        config = files("${rootDir.path}/detekt.yml")
        source = files(kotlin.sourceSets.map { it.kotlin.sourceDirectories })
    }
    group = "it.nicolasfarabegoli.${rootProject.name}"
}

subprojects {
    with(rootProject.libs.plugins) {
        apply(plugin = publishOnCentral.id)
    }
    signing {
        if (System.getenv("CI") == "true") {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }
    publishOnCentral {
        projectUrl.set("https://github.com/nicolasfara/rabbitmq-platform")
        projectLongName.set("Framework enabling pulverization")
        projectDescription.set("A framework to create a pulverized system")
        repository("https://maven.pkg.github.com/nicolasfara/${rootProject.name}".toLowerCase()) {
            user.set("nicolasfara")
            password.set(System.getenv("GITHUB_TOKEN"))
        }
    }
    publishing.publications.withType<MavenPublication>().configureEach {
        pom {
            scm {
                connection.set("git:git@github.com:nicolasfara/rabbitmq-platform")
                developerConnection.set("git:git@github.com:nicolasfara/rabbitmq-platform")
                url.set("https://github.com/nicolasfara/rabbitmq-platform")
            }
            developers {
                developer {
                    name.set("Nicolas Farabegoli")
                    email.set("nicolas.farabegoli@gmail.com")
                    url.set("https://www.nicolasfarabegoli.it/")
                }
            }
        }
    }
    publishing {
        publications {
            publications.withType<MavenPublication>().configureEach {
                if ("OSSRH" !in name) {
                    artifact(tasks.javadocJar)
                }
            }
        }
    }
}

koverMerged {
    enable()
    htmlReport {
        onCheck.set(true)
    }
    xmlReport {
        onCheck.set(true)
    }
    filters {
        projects {
            excludes += listOf(":", ":examples:example-03")
        }
    }
}
