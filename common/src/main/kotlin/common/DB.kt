package common

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import common.models.*

object DB {
    lateinit var db: MongoDatabase
    val stopsCollection: MongoCollection<Stop> by lazy { db.getCollection(Stop::class.simpleName!!) }
    val stopsInfoCollection: MongoCollection<StopInfo> by lazy { db.getCollection(StopInfo::class.simpleName!!) }
    val stopsSubscriptionsCollection: MongoCollection<StopsSubscription> by lazy { db.getCollection(StopsSubscription::class.simpleName!!) }
    val itinerariesCollection: MongoCollection<Itinerary> by lazy { db.getCollection(Itinerary::class.simpleName!!) }
    val shapesCollection: MongoCollection<Shape> by lazy { db.getCollection(Shape::class.simpleName!!) }
    val stopsOrderCollection: MongoCollection<StopOrder> by lazy { db.getCollection(StopOrder::class.simpleName!!) }
    val calendarsCollection: MongoCollection<Calendar> by lazy { db.getCollection(Calendar::class.simpleName!!) }
    val routesCollection: MongoCollection<Route> by lazy { db.getCollection(Route::class.simpleName!!) }

    fun setupMongo(connectionString: String) {
        db = MongoClient.create(connectionString).getDatabase("busTracker")
    }
}