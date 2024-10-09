package utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongoContainer {
    private var initialized = false
    private val startUpMutex = Mutex()

    suspend fun start() = startUpMutex.withLock {
        if (initialized) return

        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest"))
        mongoDBContainer.start()

        if (System.getProperty("MONGO_CONNECTION_STRING") == null)
            System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)

        loader.main()

        initialized = true
    }
}