package api.db

import api.config.abonosSubscriptionsCollection
import api.db.models.AbonoSubscription
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull

fun getAbonoSubscriptions() = abonosSubscriptionsCollection.find()

suspend fun getAbonoSubscription(abonoSubscription: AbonoSubscription) = abonosSubscriptionsCollection.find(
    Filters.and(
        Filters.eq(AbonoSubscription::token.name, abonoSubscription.token),
        Filters.eq(AbonoSubscription::ttp.name, abonoSubscription.ttp),
    )
).firstOrNull()

suspend fun addAbonoSubscription(abonoSubscription: AbonoSubscription) {
    if (getAbonoSubscription(abonoSubscription) != null) return
    abonosSubscriptionsCollection.insertOne(abonoSubscription)
}

suspend fun removeAbonoSubscription(abonoSubscription: AbonoSubscription) = abonosSubscriptionsCollection.deleteOne(
    Filters.and(
        Filters.eq(AbonoSubscription::token.name, abonoSubscription.token),
        Filters.eq(AbonoSubscription::ttp.name, abonoSubscription.ttp),
    )
)