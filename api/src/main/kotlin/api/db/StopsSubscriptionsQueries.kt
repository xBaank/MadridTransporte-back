package api.db

import api.config.EnvVariables
import api.config.stopsSubscriptionsCollection
import api.db.models.DeviceToken
import api.db.models.LineDestination
import api.db.models.StopsSubscription
import api.exceptions.BusTrackerException
import api.exceptions.BusTrackerException.TooManyRequests
import arrow.core.Either
import arrow.core.raise.either
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull

fun getSubscriptions() = stopsSubscriptionsCollection.find()
suspend fun unsubscribeDevice(deviceToken: DeviceToken, stopCode: String, lineDestination: LineDestination) {
    val containDeviceToken = Filters.elemMatch(
        StopsSubscription::deviceTokens.name,
        Filters.eq(DeviceToken::token.name, deviceToken.token)
    )
    val equalsStopCode = Filters.eq(StopsSubscription::stopCode.name, stopCode)
    val filter = Filters.and(containDeviceToken, equalsStopCode)

    stopsSubscriptionsCollection.find(filter).collect { subscription ->
        subscription.linesByDeviceToken[deviceToken.token] = subscription.linesByDeviceToken[deviceToken.token]
            ?.filter { it != lineDestination } ?: emptyList()

        if (subscription.linesByDeviceToken[deviceToken.token].isNullOrEmpty())
            subscription.deviceTokens -= deviceToken

        val update = Updates.combine(
            Updates.set(StopsSubscription::linesByDeviceToken.name, subscription.linesByDeviceToken),
            Updates.set(StopsSubscription::deviceTokens.name, subscription.deviceTokens)
        )
        val option = UpdateOptions().upsert(true)
        stopsSubscriptionsCollection.updateOne(filter = filter, update = update, options = option)

    }

    stopsSubscriptionsCollection.deleteMany(Filters.size(StopsSubscription::deviceTokens.name, 0))
}

suspend fun unsubscribeAllDevice(deviceToken: DeviceToken) {
    val containsDeviceToken = Filters.elemMatch(
        StopsSubscription::deviceTokens.name,
        Filters.eq(DeviceToken::token.name, deviceToken.token)
    )

    stopsSubscriptionsCollection.find(containsDeviceToken).collect { subscription ->
        subscription.linesByDeviceToken[deviceToken.token] = emptyList()
        subscription.deviceTokens -= deviceToken

        val update = Updates.combine(
            Updates.set(StopsSubscription::linesByDeviceToken.name, subscription.linesByDeviceToken),
            Updates.set(StopsSubscription::deviceTokens.name, subscription.deviceTokens)
        )
        val option = UpdateOptions().upsert(true)
        stopsSubscriptionsCollection.updateOne(filter = containsDeviceToken, update = update, options = option)
    }

    stopsSubscriptionsCollection.deleteMany(Filters.size(StopsSubscription::deviceTokens.name, 0))
}

fun getSubscriptions(deviceToken: DeviceToken) = stopsSubscriptionsCollection
    .find(
        Filters.elemMatch(
            StopsSubscription::deviceTokens.name,
            Filters.eq(DeviceToken::token.name, deviceToken.token)
        )
    )

suspend fun getSubscription(deviceToken: DeviceToken, stopCode: String) = either {
    val containsDeviceToken = Filters.elemMatch(
        StopsSubscription::deviceTokens.name,
        Filters.eq(DeviceToken::token.name, deviceToken.token)
    )
    val sub = stopsSubscriptionsCollection
        .find(Filters.and(containsDeviceToken, Filters.eq(StopsSubscription::stopCode.name, stopCode)))
        .firstOrNull()

    sub ?: raise(BusTrackerException.NotFound("Subscription not found"))
}

suspend fun subscribeDevice(
    deviceToken: DeviceToken,
    stopId: String,
    lineDestination: LineDestination,
    codMode: String
): Either<BusTrackerException, Unit> = either {
    if (getSubscriptions(deviceToken).count() > EnvVariables.subscriptionsLimit)
        raise(TooManyRequests("Limit of subscriptions reached"))

    val subscription = stopsSubscriptionsCollection
        .find(
            Filters.and(
                Filters.eq(StopsSubscription::stopCode.name, stopId),
                Filters.eq(StopsSubscription::codMode.name, codMode)
            )
        )
        .firstOrNull()

    if (subscription != null
        &&
        deviceToken in subscription.deviceTokens
        &&
        subscription.linesByDeviceToken[deviceToken.token]?.contains(lineDestination) == true
    ) raise(BusTrackerException.Conflict("Device already subscribed"))

    if (subscription != null) {
        if (deviceToken !in subscription.deviceTokens) subscription.deviceTokens += deviceToken
        subscription.linesByDeviceToken[deviceToken.token] =
            subscription.linesByDeviceToken[deviceToken.token]?.plus(lineDestination) ?: listOf(lineDestination)
        stopsSubscriptionsCollection.replaceOne(Filters.eq(StopsSubscription::stopCode.name, stopId), subscription)
        return@either
    }

    val newSubscription = StopsSubscription(
        deviceTokens = mutableListOf(deviceToken),
        linesByDeviceToken = mutableMapOf(deviceToken.token to listOf(lineDestination)),
        stopCode = stopId,
        codMode = codMode,
        stopName = getStopNameByStopCode(stopId).bind()
    )
    stopsSubscriptionsCollection.insertOne(newSubscription)
}