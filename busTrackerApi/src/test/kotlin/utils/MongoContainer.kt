package utils

import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongoContainer {
    private var initialized = false
    fun start() = runBlocking {
        if (initialized) return@runBlocking

        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
        mongoDBContainer.start()

        System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)
        initialized = true
    }
}