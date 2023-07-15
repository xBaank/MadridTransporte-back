import arrow.core.getOrElse
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get
import utils.*
import java.net.URLEncoder

class FavouritesTest : TestBase {

    @Test
    fun `should add and get favourites`() = testApplicationBusTracker {
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()


        register(mail, username, password)
        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val accessToken = login(mail, password).bodyAsText().deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        val getFavouritesResponse = getFavourites(accessToken)

        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
        getFavouritesResponse.status.shouldBe(HttpStatusCode.OK)
        getFavouritesResponse.bodyAsText().deserialized().getOrElse { throw it }.shouldBeInstanceOf(JsonArray::class)
        getFavouritesResponse.bodyAsText().deserialized().getOrElse { throw it }[0]["stopId"].asString()
            .getOrElse { throw it }.shouldBeEqualTo("123")
    }

    @Test
    fun `should add and delete`() = testApplicationBusTracker {
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)
        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, password).bodyAsText()
        val accessToken = response.deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        val deletedResponse = deleteFavourite(accessToken, "123")

        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
        deletedResponse.status.shouldBe(HttpStatusCode.NoContent)
    }

    @Test
    fun `should add and getById`() = testApplicationBusTracker {
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)
        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, password).bodyAsText()
        val accessToken = response.deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        val byIdResponse = getFavourite(accessToken, "123")

        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
        byIdResponse.status.shouldBe(HttpStatusCode.OK)

    }

    @Test
    fun `should not add favourites`() = testApplicationBusTracker {
        val favouritesResponse = addFavourite("asd", "bus", "123")
        favouritesResponse.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}