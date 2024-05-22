package common.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")

fun TimeZone.toZoneOffset() = ZoneId.of(this.id).rules.getOffset(Instant.now())

