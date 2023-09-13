import arrow.core.right
import busTrackerApi.routing.stops.*
import com.google.api.core.ApiFuture
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.*
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.jObject
import simpleJson.serialized
import utils.testApplicationBusTracker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

enum class Subscriptions(val url: String, val stopcode: String) {
    BUS("/v1/stops/bus/times", busStopCode),
    METRO("/v1/stops/metro/times", metroStopCode),
    EMT("/v1/stops/emt/times", emtStopCode),
    TRAM("/v1/stops/tram/times", tramStopCode)
}

val testStopTimes = StopTimes(
    codMode = 0,
    stopName = "",
    simpleStopCode = "",
    stopCode = "",
    coordinates = Coordinates(0.0, 0.0),
    arrives = listOf(),
    incidents = listOf()
)

class NotificationsTest {
    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should subscribe to line times, get subscription and unsubscribe`(subscription: Subscriptions) =
        testApplicationBusTracker {
            delayTime = 5.seconds
            val functionMocked: StopTimesF = { testStopTimes.right() }
            val firebaseMessaging = mockk<FirebaseMessaging>()
            mockkStatic(FirebaseMessaging::class)
            mockkStatic(::getFunctionByCodMode)
            coEvery { getFunctionByCodMode(any()) } returns functionMocked.right()
            every { FirebaseMessaging.getInstance() } returns firebaseMessaging
            every { firebaseMessaging.sendAsync(any()) } returns mockk<ApiFuture<String>>()

            val body = jObject {
                "deviceToken" += "token"
                "subscription" += jObject {
                    "stopCode" += subscription.stopcode
                    "lineDestination" += jObject {
                        "line" += "8"
                        "destination" += "asd"
                        "codMode" += 8
                    }
                }
            }.serialized()

            val subscribedResponse = client.post(subscription.url + "/subscribe") {
                setBody(body)
            }

            val subscriptionsResponse = client.post(subscription.url + "/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "stopCode" += subscription.stopcode
                }.serialized())
            }
            
            delay(delayTime)

            val unsubscribeResponse = client.post(subscription.url + "/unsubscribe") {
                setBody(body)
            }

            verify(timeout = delayTime.toLong(DurationUnit.MILLISECONDS)) { firebaseMessaging.sendAsync(any()) }
            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.isSuccess().shouldBe(true)
            unsubscribeResponse.status.isSuccess().shouldBe(true)
        }
}