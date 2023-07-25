package busTrackerApi.routing.favorites

import simpleJson.jObject

fun buildFavoriteJson(favorite : Favorite) = jObject {
    "stopType" += favorite.stopType
    "stopId" += favorite.stopId
    "name" += favorite.name
    "email" += favorite.email
}