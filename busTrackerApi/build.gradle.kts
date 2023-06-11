repositories {
    mavenCentral()
}

val ktor_version: String by project
val test_containers_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
}

application {
    mainClass.set("busTrackerApi.MainKt")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    //arrow kt
    implementation("io.arrow-kt:arrow-core:1.1.5")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.github.xbaank:simpleJson-core:2.1.3")
    implementation("org.litote.kmongo:kmongo-async:4.9.0")
    implementation("org.simplejavamail:simple-java-mail:8.1.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.9.0")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.10.0")
    // https://mvnrepository.com/artifact/io.insert-koin/koin-core
    runtimeOnly("io.insert-koin:koin-core:3.4.1")
    // https://mvnrepository.com/artifact/io.insert-koin/koin-ktor
    implementation("io.insert-koin:koin-ktor:3.4.1")
    implementation("com.ToxicBakery.library.bcrypt:bcrypt:+")
    // https://mvnrepository.com/artifact/org.amshove.kluent/kluent
    testImplementation("org.amshove.kluent:kluent:1.72")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.testcontainers:mongodb:$test_containers_version")
    testImplementation("org.testcontainers:junit-jupiter:$test_containers_version")
    testImplementation("io.github.serpro69:kotlin-faker:1.14.0")
    implementation(project(":crtm"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}



ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}
