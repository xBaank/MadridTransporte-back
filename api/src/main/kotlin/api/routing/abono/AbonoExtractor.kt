package api.routing.abono

import api.exceptions.BusTrackerException.NotFound
import arrow.core.raise.either
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun extractAbono(data: SS_prepagoConsultaSaldo) = either {
    if (data.ttpSearchResult?.value != 1) raise(NotFound("No se ha encontrado el abono"))

    val contracts =
        data.ttpSearchResult?.operationResult?.Contracts?.contractNumber?.filter { it.ContractCode != null }

    val contractsMapped = contracts?.map { contract ->
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

        Contract(
            contractCode = contract.ContractCode,
            contractName = contract.ContractName,
            contractCompanyPropietary = contract.ContractCompanyPropietary,
            contractUserProfileType = contract.ContractUserProfileType,
            contractUserProfilePropietaryCompany = contract.ContractUserProfilePropietaryCompany,
            chargeDate = contract.ContractRechargeDate ?: contract.ContractChargeDate,
            firstUseDateLimit = contract.RechargeFirstUseDate ?: contract.ChargeFirstUseDate,
            firstUseDate = contract.ContractRechargeStartDate ?: contract.ContractChargeStartDate,
            lastUseDate = lastUseDate,
            useDays = contract.InvalidityPeriod,
            leftDays = leftDays,
            charges = (contract.ContractChargeUnits ?: contract.ContractRechargeUnits)?.toIntOrNull(),
            remainingCharges = (contract.ChargeRemainUnits ?: contract.ContractRechargeUnits)?.toIntOrNull()
        )
    }

    Abono(
        serialNumber = data.ttpSearchResult?.ttpData?.SerialNumber,
        ttpNumber = data.ttpSearchResult?.ttpData?.TTPNumber,
        createdAt = data.ttpSearchResult?.ttpData?.UserGroupValidityDate,
        expireAt = data.ttpSearchResult?.ttpData?.UserGroupExpiryDate,
        contracts = contractsMapped ?: listOf()
    )
}