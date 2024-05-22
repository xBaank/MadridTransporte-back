package common.queries

import arrow.core.left
import arrow.core.right
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import common.DB.routesCollection
import common.exceptions.BusTrackerException
import common.models.Itinerary
import common.models.Route
import common.models.RouteWithItineraries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

suspend fun getRoute(simpleLineCode: String, codMode: String) = routesCollection.find(
    Filters.and(
        Filters.eq(Route::simpleLineCode.name, simpleLineCode),
        Filters.eq(Route::codMode.name, codMode)
    )
).firstOrNull()?.right() ?: BusTrackerException.NotFound().left()

suspend fun getRoute(simpleLineCode: String, codModes: List<String>) = routesCollection.find(
    Filters.and(
        Filters.eq(Route::simpleLineCode.name, simpleLineCode),
        Filters.`in`(Route::codMode.name, codModes)
    )
).firstOrNull()?.right() ?: BusTrackerException.NotFound().left()

suspend fun getRouteByFullLineCode(fullLineCode: String) = routesCollection.find(
    Filters.and(
        Filters.eq(Route::fullLineCode.name, fullLineCode)
    )
).firstOrNull()?.right() ?: BusTrackerException.NotFound().left()

fun getRoutesWithItineraries(): Flow<RouteWithItineraries> {
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