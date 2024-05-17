import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

val ktor_version: String by project
val test_containers_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlinx.kover") version "0.8.0"
    kotlin("plugin.serialization") version "1.9.24"
    application
}

application {
    mainClass.set("api.MainKt")
}

dependencies {
    implementation("com.sun.xml.ws:jaxws-tools:4.0.2")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3") // for JVM platform
    //arrow kt
    implementation("io.arrow-kt:arrow-core:1.2.4")
    // https://mvnrepository.com/artifact/io.arrow-kt/arrow-resilience
    implementation("io.arrow-kt:arrow-resilience:1.2.4")

    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.github.xbaank:simpleJson-core:3.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.13.0")
    // https://mvnrepository.com/artifact/org.amshove.kluent/kluent
    testImplementation("org.amshove.kluent:kluent:1.73")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.testcontainers:mongodb:1.19.8")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    implementation("io.ktor:ktor-server-compression:$ktor_version")
    testImplementation("io.mockk:mockk:1.13.11")
    implementation("io.github.pdvrieze.xmlutil:core:0.86.3")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.86.3")
    implementation("io.ktor:ktor-server-caching-headers:$ktor_version")// https://mvnrepository.com/artifact/com.google.firebase/firebase-messaging
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-guava
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.1")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.0")
    implementation("dev.inmo:krontab:2.3.0")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.17.2")
    implementation(project(":crtm"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}




ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}
