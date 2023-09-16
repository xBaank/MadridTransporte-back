import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jaxws by configurations.creating

repositories {
    mavenCentral()
}



plugins {
    kotlin("jvm") version "1.8.0"
    `maven-publish`
    application
}

dependencies {
    jaxws("com.sun.xml.ws:jaxws-tools:4.0.0")
    implementation("com.sun.xml.ws:jaxws-tools:4.0.1")
    //junit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    //kluent
    testImplementation("org.amshove.kluent:kluent:1.72")
}


//publish
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
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

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.encoding = "ISO-8859-1"
}

task("wsimport-myservice-buses") {
    group = BasePlugin.BUILD_GROUP
    val destDir = file("$projectDir/src/main/java")
    destDir.mkdirs()
    val sourceDestDir = file("$projectDir/src/main/java")
    val async = file("$projectDir/src/main/resources/async.xml")
    sourceDestDir.mkdirs()
    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "wsimport",
                "classname" to "com.sun.tools.ws.ant.WsImport",
                "classpath" to jaxws.asPath
            )

            "wsimport"(
                "binding" to async,
                "keep" to true,
                "sourcedestdir" to sourceDestDir,
                //"destDir" to destDir, alreaddy compiled java classes, not needed
                "package" to "crtm.soap",
                "wsdl" to "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc?wsdl",
            ) {
                "xjcarg"("value" to "-XautoNameResolution")
            }
        }
    }
}


task("wsimport-myservice-abono") {
    group = BasePlugin.BUILD_GROUP
    val destDir = file("$projectDir/src/main/java")
    destDir.mkdirs()
    val sourceDestDir = file("$projectDir/src/main/java")
    val async = file("$projectDir/src/main/resources/async.xml")
    sourceDestDir.mkdirs()
    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "wsimport",
                "classname" to "com.sun.tools.ws.ant.WsImport",
                "classpath" to jaxws.asPath
            )

            "wsimport"(
                "binding" to async,
                "keep" to true,
                "sourcedestdir" to sourceDestDir,
                //"destDir" to destDir, alreaddy compiled java classes, not needed
                "package" to "crtm.abono",
                "wsdl" to "http://www.citram.es:50081/VENTAPREPAGOTITULO/VentaPrepagoTitulo.svc?wsdl",
            ) {
                "xjcarg"("value" to "-XautoNameResolution")
            }
        }
    }
}
