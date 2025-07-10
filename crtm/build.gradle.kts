import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val jaxws by configurations.creating

repositories {
    mavenCentral()
}



plugins {
    kotlin("jvm")
    `maven-publish`
    application
}

dependencies {
    jaxws("com.sun.xml.ws:jaxws-tools:4.0.3")
    implementation("com.sun.xml.ws:jaxws-tools:4.0.3")
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


kotlin {
    jvmToolchain(11)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.encoding = "ISO-8859-1"
}

tasks.register("wsimport-myservice-buses") {
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


tasks.register("wsimport-myservice-abono") {
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
