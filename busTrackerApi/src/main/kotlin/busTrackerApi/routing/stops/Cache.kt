package busTrackerApi.routing.stops

import java.time.Instant

data class TimedCachedValue<out T>(val value: T, val createdAt: Instant)

fun <T> T.timed(): TimedCachedValue<T> = TimedCachedValue(this, Instant.now())