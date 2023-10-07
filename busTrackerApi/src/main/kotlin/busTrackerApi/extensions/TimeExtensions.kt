package busTrackerApi.extensions

import java.time.Instant
import java.time.ZoneId
import java.util.*

fun TimeZone.toZoneOffset() = ZoneId.of(this.id).rules.getOffset(Instant.now())