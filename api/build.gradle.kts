import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

val ktor_version: String by project
val arrow_version: String by project
val test_containers_version: String by project
val simplejson_version: String by project
val okhttp_version: String by project
val okhttpcoroutines_version: String by project
val logback_classic_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlinx.kover")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("api.MainKt")
}

dependencies {
    implementation("com.sun.xml.ws:jaxws-tools:4.0.3")
    implementation("ch.qos.logback:logback-classic:$logback_classic_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:$okhttpcoroutines_version")
    //arrow kt
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    // https://mvnrepository.com/artifact/io.arrow-kt/arrow-resilience
    implementation("io.arrow-kt:arrow-resilience:$arrow_version")

    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.github.xbaank:simpleJson-core:$simplejson_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.14.0")
    implementation("io.ktor:ktor-server-compression:$ktor_version")
    implementation("io.github.pdvrieze.xmlutil:core:0.91.0")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.91.1")
    implementation("io.ktor:ktor-server-caching-headers:$ktor_version")// https://mvnrepository.com/artifact/com.google.firebase/firebase-messaging
    implementation("com.google.firebase:firebase-admin:9.4.3")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.33.1")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-guava
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.10.2")
    implementation("dev.inmo:krontab:2.7.2")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.20.1")
    implementation(project(":crtm"))
    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}


ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
        allowZip64 = true
    }
}
