package api.routing.abono

import api.db.models.AbonoSubscription
import simpleJson.asJson
import simpleJson.jObject

fun buildAbonoJson(abono: Abono) = jObject {
    "serialNumber" += abono.serialNumber
    "ttpNumber" += abono.ttpNumber
    "createdAt" += abono.createdAt
    "expireAt" += abono.expireAt
    "contracts" += abono.contracts.map { contract ->
        jObject {
            "contractCode" += contract.contractCode
            "contractName" += contract.contractName
            "contractCompanyPropietary" += contract.contractCompanyPropietary
            "contractUserProfileType" += contract.contractUserProfileType
            "contractUserProfilePropietaryCompany" += contract.contractUserProfilePropietaryCompany
            "chargeDate" += contract.chargeDate
            "firstUseDateLimit" += contract.firstUseDateLimit
            "firstUseDate" += contract.firstUseDate
            "lastUseDate" += contract.lastUseDate
            "useDays" += contract.useDays
            "leftDays" += contract.leftDays
            "charges" += contract.charges
            "remainingCharges" += contract.remainingCharges
        }
    }.asJson()
}

fun buildAbonoSubscriptionJson(abonoSubscription: AbonoSubscription) = jObject {
    "ttpNumber" += abonoSubscription.ttp
    "deviceToken" += abonoSubscription.token.token
    "name" += abonoSubscription.abonoName
}