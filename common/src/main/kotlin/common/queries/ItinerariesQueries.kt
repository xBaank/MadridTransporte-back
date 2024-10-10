package common.queries

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import common.DB.itinerariesCollection
import common.models.Itinerary
import common.models.ItineraryWithStops
import common.models.StopOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull

fun getItinerariesByFullLineCode(fullLineCode: String, direction: Int, stopCode: String) =
    getItinerariesByFullLineCode(
        fullLineCode,
        direction
    ).filter { stopCode in it.stops.map(StopOrder::fullStopCode) }

suspend fun getItineraryByCode(itineraryCode: String): ItineraryWithStops? {
    val pipeline = listOf(
        Aggregates.match(
            Filters.and(
                Filters.eq(Itinerary::itineraryCode.name, itineraryCode),
            )
        ),
        Aggregates.lookup(
            /* from = */ StopOrder::class.simpleName!!,
            /* localField = */ StopOrder::tripId.name,
            /* foreignField = */ Itinerary::tripId.name,
            /* as = */ ItineraryWithStops::stops.name
        )
    )
    return itinerariesCollection.withDocumentClass<ItineraryWithStops>().aggregate(pipeline).firstOrNull()
}

fun getItinerariesByFullLineCode(fullLineCode: String, direction: Int): Flow<ItineraryWithStops> {
    val pipeline = listOf(
        Aggregates.match(
            Filters.and(
                Filters.eq(Itinerary::fullLineCode.name, fullLineCode),
                Filters.eq(Itinerary::direction.name, direction - 1)
            )
        ),
        Aggregates.lookup(
            /* from = */ StopOrder::class.simpleName!!,
            /* localField = */ StopOrder::tripId.name,
            /* foreignField = */ Itinerary::tripId.name,
            /* as = */ ItineraryWithStops::stops.name
        )
    )
    return itinerariesCollection.withDocumentClass<ItineraryWithStops>().aggregate(pipeline)
        .distinctUntilChangedBy { it.itineraryCode to it.direction }
}

