package busTrackerApi.routing.abono

import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException.NotFound
import simpleJson.JsonObject
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun isFound(value: SS_prepagoConsultaSaldo) =
    if (value.ttpSearchResult?.value == 1) Unit.right()
    else NotFound("No se ha encontrado el abono").left()

fun buildAbonoJson(data: SS_prepagoConsultaSaldo): JsonObject {
    val contracts =
        data.ttpSearchResult?.operationResult?.Contracts?.contractNumber?.filter { it.ContractCode != null }

    val json = jObject {
        "serialNumber" += data.ttpSearchResult?.ttpData?.SerialNumber
        "ttpNumber" += data.ttpSearchResult?.ttpData?.TTPNumber
        "createdAt" += data.ttpSearchResult?.ttpData?.UserGroupValidityDate
        "expireAt" += data.ttpSearchResult?.ttpData?.UserGroupExpiryDate
        "contracts" += contracts?.map { contract ->
            val now = LocalDate.now()
            val firstUseDate = LocalDate.parse(
                contract.ContractRechargeStartDate
                    ?: contract.ContractChargeStartDate
                    ?: now.format(DateTimeFormatter.ISO_DATE)
            )
            val lastUseDay =
                if (contract.InvalidityPeriod == 0) null else firstUseDate.plusDays(contract.InvalidityPeriod!!.toLong())

            val lastUseDate = lastUseDay?.format(DateTimeFormatter.ISO_DATE)
            val leftDays = if (lastUseDay != null) now.until(lastUseDay).days.takeIf { it > 0 } ?: 0 else null

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
                "charges" += contract.ContractChargeUnits ?: contract.ContractRechargeUnits
                "remainingCharges" += contract.ChargeRemainUnits ?: contract.ContractRechargeUnits
            }
        }?.asJson() ?: jArray {}
    }
    return json
}