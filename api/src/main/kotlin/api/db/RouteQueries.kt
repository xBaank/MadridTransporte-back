package api.db

import api.config.routesCollection
import api.db.models.Itinerary
import api.db.models.Route
import api.db.models.RouteWithItineraries
import api.exceptions.BusTrackerException.NotFound
import arrow.core.left
import arrow.core.right
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.AggregateFlow
import kotlinx.coroutines.flow.firstOrNull

suspend fun getRoute(simpleLineCode: String, codMode: String) = routesCollection.find(
    Filters.and(
        Filters.eq(Route::simpleLineCode.name, simpleLineCode),
        Filters.eq(Route::codMode.name, codMode)
    )
).firstOrNull()?.right() ?: NotFound().left()

suspend fun getRoute(fullLineCode: String) = routesCollection.find(
    Filters.and(
        Filters.eq(Route::fullLineCode.name, fullLineCode)
    )
).firstOrNull()?.right() ?: NotFound().left()

fun getRoutesWithItineraries(): AggregateFlow<RouteWithItineraries> {
    val pipeline = listOf(
        Aggregates.lookup(
            /* from = */ Itinerary::class.simpleName!!,
            /* localField = */ Route::fullLineCode.name,
            /* foreignField = */ Itinerary::fullLineCode.name,
            /* as = */ RouteWithItineraries::itineraries.name
        )
    )

    return routesCollection.withDocumentClass<RouteWithItineraries>().aggregate(pipeline)
}