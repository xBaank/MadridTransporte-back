package common.queries

import com.mongodb.client.model.Filters
import common.DB.shapesCollection
import common.models.Shape
import kotlinx.coroutines.flow.Flow

fun getShapesByItineraryCode(itineraryCode: String): Flow<Shape> =
    shapesCollection.find(Filters.eq(Shape::itineraryId.name, itineraryCode))