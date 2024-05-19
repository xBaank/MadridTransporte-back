package common.models

data class LineDestination(val line: String = "", val destination: String = "", val codMode: Int = 0)
data class DeviceToken(val token: String = "")
data class StopsSubscription(
    val deviceTokens: MutableList<DeviceToken> = mutableListOf(),
    val linesByDeviceToken: MutableMap<String, List<LineDestination>> = mutableMapOf(),
    val stopCode: String = "",
    val codMode: String = "",
    val stopName: String = "",
)

fun String.toDeviceToken() = DeviceToken(this)