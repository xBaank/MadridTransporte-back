package busTrackerApi.db

import busTrackerApi.config.itinerariesCollection
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

suspend fun getItinerariesByFullLineCode(fullLineCode: String, direction: Int) =
    itinerariesCollection.find(
        Filters.and(
            Filters.eq(Itinerary::fullLineCode.name, fullLineCode),
            Filters.eq(Itinerary::direction.name, direction),
        )
    )
        .toList()

suspend fun getItineraryByFullLineCode(fullLineCode: String, direction: Int) =
    itinerariesCollection.find(
        Filters.and(
            Filters.eq(Itinerary::fullLineCode.name, fullLineCode),
            Filters.eq(Itinerary::direction.name, direction),
        )
    ).firstOrNull()