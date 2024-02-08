package api.routing.abono

data class Contract(
    val contractCode: Int?,
    val contractName: String?,
    val contractCompanyPropietary: Int?,
    val contractUserProfileType: String?,
    val contractUserProfilePropietaryCompany: String?,
    val chargeDate: String?,
    val firstUseDateLimit: String?,
    val firstUseDate: String?,
    val lastUseDate: String?,
    val useDays: Int?,
    val leftDays: Int?,
    val charges: Int?,
    val remainingCharges: Int?
)

data class Abono(
    val serialNumber: String?,
    val ttpNumber: String?,
    val createdAt: String?,
    val expireAt: String?,
    val contracts: List<Contract>
)
