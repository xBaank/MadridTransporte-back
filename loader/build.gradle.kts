plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("api.MainKt")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

val arrow_version: String by project
val okhttp_version: String by project
val mongo_version: String by project

dependencies {
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3") // for JVM platform
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation(project(":common"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}