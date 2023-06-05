import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

val ktor_version: String by project


plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
}

application {
    mainClass.set("busTrackerApi.MainKt")
}


dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    //arrow kt
    implementation("io.arrow-kt:arrow-core:1.1.5")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.github.xbaank:simpleJson-core:2.1.3")
    implementation("org.litote.kmongo:kmongo-async:4.9.0")
    implementation("net.axay:simplekotlinmail-core:1.4.0")
    implementation("net.axay:simplekotlinmail-client:1.4.0")
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    implementation(project(":crtm"))

}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

ktor {
    fatJar {
        archiveFileName.set("${project.name}.jar")
    }
}
