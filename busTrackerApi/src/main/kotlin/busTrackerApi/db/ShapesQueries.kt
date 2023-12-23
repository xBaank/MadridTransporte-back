package busTrackerApi.db

import busTrackerApi.config.shapesCollection
import busTrackerApi.db.models.Shape
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow

fun getShapesByItineraryCode(itineraryCode: String): FindFlow<Shape> =
    shapesCollection.find(Filters.eq(Shape::itineraryId.name, itineraryCode))