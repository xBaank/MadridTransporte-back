plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("loader.MainKt")
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
val logback_classic_version: String by project

dependencies {
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3") // for JVM platform
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("ch.qos.logback:logback-classic:$logback_classic_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation(project(":common"))
    testImplementation(kotlin("test"))
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "compileKotlin",
                "processResources"
            )
        )
        archiveFileName.set("${project.name}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

tasks.test {
    useJUnitPlatform()
}