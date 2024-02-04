package api.routing.abono

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class SS_prepagoConsultaSaldo(
    var ttpSearchResult: TTPSearchResult? = null,
    var version: String? = null,
    var fecha: String? = null,
    var xmlnsXsi: String? = null,
    var xsiNoNamespaceSchemaLocation: String? = null
)

@Serializable
data class TTPSearchResult(
    val ttpData: TTPData? = null,
    val operationResult: OperationResult? = null,
    val value: Int = 0,
    val desc: String? = null
)

@Serializable
data class OperationResult(
    @XmlElement
    val FEapValidityIndexAT: String? = null,
    @XmlElement
    val FEapValidityIndexT1: String? = null,
    @XmlElement
    val FEapValidityIndexT2: String? = null,
    @XmlElement
    val FEapValidityIndexT3: String? = null,
    @XmlElement
    val Contracts: Contracts? = null,
    val value: String? = null
)

@Serializable
data class Contracts(
    val contractNumber: Array<ContractNumber>
)

@Serializable
data class ContractNumber(
    @XmlElement
    val ContractCode: Int? = null,
    @XmlElement
    val ContractCompanyPropietary: Int? = null,
    @XmlElement
    val ContractName: String? = null,
    @XmlElement
    val InvalidityPeriod: Int? = null,
    @XmlElement
    val FirstUseValidityPeriod: String? = null,
    @XmlElement
    val ContractUserProfileType: String? = null,
    @XmlElement
    val ContractUserProfilePropietaryCompany: String? = null,
    @XmlElement
    val ContractUserProfileTypeName: String? = null,
    @XmlElement
    val ContractChargeDate: String? = null,
    @XmlElement
    val ContractChargeStartDate: String? = null,
    @XmlElement
    val ContractChargeEndDate: String? = null,
    @XmlElement
    val ContractChargeFare: String? = null,
    @XmlElement
    val ContractChargeUnits: String? = null,
    @XmlElement
    val ChargeFirstUseDate: String? = null,
    @XmlElement
    val ChargeEndDate: String? = null,
    @XmlElement
    val ChargeRemainUnits: String? = null,
    @XmlElement
    val ContractRechargeDate: String? = null,
    @XmlElement
    val ContractRechargeStartDate: String? = null,
    @XmlElement
    val ContractRechargeEndDate: String? = null,
    @XmlElement
    val ContractRechargeFare: String? = null,
    @XmlElement
    val ContractRechargeUnits: String? = null,
    @XmlElement
    val RechargeFirstUseDate: String? = null,
    @XmlElement
    val RechargeEndDate: String? = null,
    @XmlElement
    val RechargeRemainUnits: String? = null,
    @XmlElement
    val AccessEventInFirstPayDateCharge: String? = null,
    @XmlElement
    val AccessEventInFirstPayDateRecharge: String? = null,
    @XmlElement
    val AccessEventUnitsCharge: String? = null,
    @XmlElement
    val AccessEventUnitsRecharge: String? = null,
    @XmlElement
    val PossibleContractsToCharge: String? = null,
    @XmlSerialName("contract-number")
    val contractNumber: String? = null
)

@Serializable
data class AccessEventInFirstPayDateCharge(
    val xsiNil: String? = null
)

@Serializable
data class TTPNumber(
    val xsiNil: String? = null,
    val text: String? = null
)

@Serializable
data class TTPData(
    @XmlElement
    val SerialNumber: String? = null,
    @XmlElement
    val TTPNumber: String? = null,
    @XmlElement
    val OrderCode: String? = null,
    @XmlElement
    val CardOrderNumber: String? = null,
    @XmlElement
    val CardSaleType: String? = null,
    @XmlElement
    val TransportApplicationStartDate: String? = null,
    @XmlElement
    val TransportApplicationEndDate: String? = null,
    @XmlElement
    val LastUpdateDate: String? = null,
    @XmlElement
    val FEapValidityIndexAT: String? = null,
    @XmlElement
    val FEapValidityIndexT1: String? = null,
    @XmlElement
    val FEapValidityIndexT2: String? = null,
    @XmlElement
    val FEapValidityIndexT3: String? = null,
    @XmlElement
    val UserProfiles: UserProfiles? = null,
    @XmlElement
    val UserGroup: String? = null,
    @XmlElement
    val UserGroupName: String? = null,
    @XmlElement
    val UserGroupValidityDate: String? = null,
    @XmlElement
    val UserGroupExpiryDate: String? = null,
    @XmlElement
    val XsiNil: String? = null
)

@Serializable
data class UserProfiles(
    val userProfile: Array<UserProfile>
)

@Serializable
data class UserProfile(
    @XmlElement
    val UserProfileType: String? = null,
    @XmlElement
    val UserProfilePropietaryCompany: String? = null,
    @XmlElement
    val UserProfileTypeName: String? = null,
    @XmlElement
    val UserProfileValidityDate: String? = null,
    @XmlElement
    val UserProfileExpiryDate: String? = null,
    val index: String? = null
)


