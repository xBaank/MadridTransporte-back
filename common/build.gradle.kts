import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val arrow_version: String by project
val mongo_version: String by project
val okhttp_version: String by project
val okhttpcoroutines_version: String by project
val simplejson_version: String by project

dependencies {
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:$okhttpcoroutines_version")
    implementation("io.github.xbaank:simpleJson-core:$simplejson_version")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

tasks.test {
    useJUnitPlatform()
}