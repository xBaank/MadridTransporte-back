package api.db.models

data class AbonoSubscription(
    val ttp: String,
    val token: DeviceToken
)