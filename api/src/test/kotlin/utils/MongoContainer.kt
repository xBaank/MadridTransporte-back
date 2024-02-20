package utils

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongoContainer {
    private var initialized = false
    fun start() {
        if (initialized) return

        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
        mongoDBContainer.start()

        if (System.getProperty("MONGO_CONNECTION_STRING") == null)
            System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)
        
        initialized = true
    }
}