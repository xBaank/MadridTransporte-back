package api.db

import api.config.abonosSubscriptionsCollection
import api.db.models.AbonoSubscription
import api.db.models.DeviceToken
import api.exceptions.BusTrackerException.Conflict
import api.exceptions.BusTrackerException.TooManyRequests
import arrow.core.continuations.either
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull

fun getAbonoSubscriptions() = abonosSubscriptionsCollection.find()

suspend fun getAbonoSubscription(token: DeviceToken, ttp: String) = abonosSubscriptionsCollection.find(
    Filters.and(
        Filters.eq(AbonoSubscription::token.name, token),
        Filters.eq(AbonoSubscription::ttp.name, ttp),
    )
).firstOrNull()

fun getAbonoSubscriptions(deviceToken: DeviceToken) = abonosSubscriptionsCollection.find(
    Filters.eq(DeviceToken::token.name, deviceToken.token)
)

suspend fun addAbonoSubscription(abonoSubscription: AbonoSubscription) = either {
    if (getAbonoSubscription(abonoSubscription.token, abonoSubscription.ttp) != null) shift<Nothing>(Conflict())
    if (getAbonoSubscriptions(abonoSubscription.token).count() > 5) shift<Nothing>(TooManyRequests("Limit of subscriptions reached"))
    abonosSubscriptionsCollection.insertOne(abonoSubscription)
}

suspend fun removeAbonoSubscription(token: DeviceToken, ttp: String) = abonosSubscriptionsCollection.deleteOne(
    Filters.and(
        Filters.eq(AbonoSubscription::token.name, token),
        Filters.eq(AbonoSubscription::ttp.name, ttp),
    )
)