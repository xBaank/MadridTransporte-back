package utils

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongoContainer {
    private var initialized = false
    fun start() {
        if (initialized) return
        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
        mongoDBContainer.start()
        System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)
        initialized = true
    }
}