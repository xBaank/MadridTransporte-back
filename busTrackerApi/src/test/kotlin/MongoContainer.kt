import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
object MongoContainer {

    @JvmField
    val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:latest")).withReuse(true)

    init {
        mongoDBContainer.start()
    }
}