package busTrackerApi.routing.abono

import simpleJson.JsonObject
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun isFound(value: SS_prepagoConsultaSaldo) = value.ttpSearchResult?.value == 1
fun buildAbonoJson(data: SS_prepagoConsultaSaldo): JsonObject {
    val contracts =
        data.ttpSearchResult?.operationResult?.Contracts?.contractNumber?.filter { it.ContractCode != null }

    val json = jObject {
        "serialNumber" += data.ttpSearchResult?.ttpData?.SerialNumber
        "ttpNumber" += data.ttpSearchResult?.ttpData?.TTPNumber
        "createdAt" += data.ttpSearchResult?.ttpData?.UserGroupValidityDate
        "expireAt" += data.ttpSearchResult?.ttpData?.UserGroupExpiryDate
        "contracts" += contracts?.map { contract ->
            val firstUseDate = LocalDate.parse(contract.ContractRechargeStartDate ?: contract.ContractChargeStartDate)
            //TODO maybe add +1 to invalidty period?
            val lastUseDay = firstUseDate.plusDays(contract.InvalidityPeriod!!.toLong())
            val lastUseDate = lastUseDay.format(DateTimeFormatter.ISO_DATE)
            val leftDays = LocalDate.now().until(lastUseDay).days.takeIf { it > 0 } ?: 0

            jObject {
                "contractCode" += contract.ContractCode
                "contractName" += contract.ContractName
                "contractCompanyPropietary" += contract.ContractCompanyPropietary
                "contractUserProfileType" += contract.ContractUserProfileType
                "contractUserProfilePropietaryCompany" += contract.ContractUserProfilePropietaryCompany
                "chargeDate" += contract.ContractRechargeDate ?: contract.ContractChargeDate
                "firstUseDateLimit" += contract.RechargeFirstUseDate ?: contract.ChargeFirstUseDate
                "firstUseDate" += contract.ContractRechargeStartDate ?: contract.ContractChargeStartDate
                "lastUseDate" += lastUseDate
                "useDays" += contract.InvalidityPeriod
                "leftDays" += leftDays
            }
        }?.asJson() ?: jArray {}
    }
    return json
}