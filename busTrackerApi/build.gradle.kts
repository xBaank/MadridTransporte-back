import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

val ktor_version: String by project
val test_containers_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    kotlin("plugin.serialization") version "1.8.21"
    application
}

application {
    mainClass.set("busTrackerApi.MainKt")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1") // for JVM platform
    //arrow kt
    implementation("io.arrow-kt:arrow-core:1.1.5")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.github.xbaank:simpleJson-core:2.1.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.10.0")
    // https://mvnrepository.com/artifact/io.insert-koin/koin-ktor
    implementation("io.insert-koin:koin-ktor:3.4.1")
    // https://mvnrepository.com/artifact/org.amshove.kluent/kluent
    testImplementation("org.amshove.kluent:kluent:1.72")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    implementation("io.github.pdvrieze.xmlutil:core:0.86.0")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.86.0")
    implementation("io.ktor:ktor-server-caching-headers:$ktor_version")
    implementation(project(":crtm"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}




ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}
