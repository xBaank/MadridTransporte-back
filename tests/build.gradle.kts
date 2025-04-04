plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val simplejson_version: String by project
val arrow_version: String by project
val ktor_version: String by project

dependencies {
    implementation("io.github.xbaank:simpleJson-core:$simplejson_version")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("com.google.firebase:firebase-admin:9.4.3")
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":loader"))
    testImplementation(kotlin("test"))
    testImplementation("org.amshove.kluent:kluent:1.73")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("org.testcontainers:mongodb:1.20.4")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.junit.platform:junit-platform-reporting:1.11.4")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}