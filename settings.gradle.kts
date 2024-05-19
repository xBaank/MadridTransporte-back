rootProject.name = "madrid-transporte-back"
include("api")
include("crtm")
include("loader")
include("common")
pluginManagement {
    val kotlin_version: String by settings
    val ktor_version: String by settings
    val kover_version: String by settings
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version kotlin_version
        id("io.ktor.plugin") version ktor_version
        kotlin("plugin.serialization") version kotlin_version
        id("org.jetbrains.kotlinx.kover") version kover_version
    }
}
