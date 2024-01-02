package busTrackerApi.db

import busTrackerApi.config.itinerariesCollection
import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.ItineraryWithStops
import busTrackerApi.db.models.StopOrder
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull

suspend fun getItineraryByFullLineCode(fullLineCode: String, direction: Int) =
    getItinerariesByFullLineCode(fullLineCode, direction).firstOrNull()

fun getItinerariesByFullLineCode(fullLineCode: String, direction: Int): Flow<ItineraryWithStops> {
    val pipeline = listOf(
        Aggregates.match(
            Filters.and(
                Filters.eq(Itinerary::fullLineCode.name, fullLineCode),
                Filters.eq(Itinerary::direction.name, direction)
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