package busTrackerApi.routing.abono

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
class SS_prepagoConsultaSaldo {
    var ttpSearchResult: TTPSearchResult? = null
    var version: String? = null
    var fecha: String? = null
    var xmlnsXsi: String? = null
    var xsiNoNamespaceSchemaLocation: String? = null
}

@Serializable
class TTPSearchResult {
    var ttpData: TTPData? = null
    var operationResult: OperationResult? = null
    var value: String? = null
    var desc: String? = null
}

@Serializable
class OperationResult {
    @XmlElement
    var FEapValidityIndexAT: String? = null

    @XmlElement
    var FEapValidityIndexT1: String? = null

    @XmlElement
    var FEapValidityIndexT2: String? = null

    @XmlElement
    var FEapValidityIndexT3: String? = null

    @XmlElement
    var Contracts: Contracts? = null
    var value: String? = null
}

@Serializable
// Contracts.java
class Contracts {
    lateinit var contractNumber: Array<ContractNumber>
}

@Serializable
// ContractNumber.jav
class ContractNumber {
    @XmlElement
    var ContractCode: String? = null

    @XmlElement
    var ContractCompanyPropietary: String? = null

    @XmlElement
    var ContractName: String? = null

    @XmlElement
    var InvalidityPeriod: String? = null

    @XmlElement
    var FirstUseValidityPeriod: String? = null

    @XmlElement
    var ContractUserProfileType: String? = null

    @XmlElement
    var ContractUserProfilePropietaryCompany: String? = null

    @XmlElement
    var ContractUserProfileTypeName: String? = null

    @XmlElement
    var ContractChargeDate: String? = null

    @XmlElement
    var ContractChargeStartDate: String? = null

    @XmlElement
    var ContractChargeEndDate: String? = null

    @XmlElement
    var ContractChargeFare: String? = null

    @XmlElement
    var ContractChargeUnits: String? = null

    @XmlElement
    var ChargeFirstUseDate: String? = null

    @XmlElement
    var ChargeEndDate: String? = null

    @XmlElement
    var ChargeRemainUnits: String? = null

    @XmlElement
    var ContractRechargeDate: String? = null

    @XmlElement
    var ContractRechargeStartDate: String? = null

    @XmlElement
    var ContractRechargeEndDate: String? = null

    @XmlElement
    var ContractRechargeFare: String? = null

    @XmlElement
    var ContractRechargeUnits: String? = null

    @XmlElement
    var RechargeFirstUseDate: String? = null

    @XmlElement
    var RechargeEndDate: String? = null

    @XmlElement
    var RechargeRemainUnits: String? = null

    @XmlElement
    var AccessEventInFirstPayDateCharge: String? = null

    @XmlElement
    var AccessEventInFirstPayDateRecharge: String? = null

    @XmlElement
    var AccessEventUnitsCharge: String? = null

    @XmlElement
    var AccessEventUnitsRecharge: String? = null

    @XmlElement
    var PossibleContractsToCharge: String? = null

    @XmlSerialName("contract-number")
    var contractNumber: String? = null
}

@Serializable
// AccessEventInFirstPayDa
class AccessEventInFirstPayDateCharge {
    var xsiNil: String? = null
}

@Serializable
// TtpNumber.java
class TTPNumber {
    var xsiNil: String? = null
    var text: String? = null
}

@Serializable
// TTPData.java
class TTPData {
    @XmlElement
    var SerialNumber: String? = null

    @XmlElement
    var TTPNumber: String? = null

    @XmlElement
    var OrderCode: String? = null

    @XmlElement
    var CardOrderNumber: String? = null

    @XmlElement
    var CardSaleType: String? = null

    @XmlElement
    var TransportApplicationStartDate: String? = null

    @XmlElement
    var TransportApplicationEndDate: String? = null

    @XmlElement
    var LastUpdateDate: String? = null

    @XmlElement
    var FEapValidityIndexAT: String? = null

    @XmlElement
    var FEapValidityIndexT1: String? = null

    @XmlElement
    var FEapValidityIndexT2: String? = null

    @XmlElement
    var FEapValidityIndexT3: String? = null

    @XmlElement
    var UserProfiles: UserProfiles? = null

    @XmlElement
    var UserGroup: String? = null

    @XmlElement
    var UserGroupName: String? = null

    @XmlElement
    var UserGroupValidityDate: String? = null

    @XmlElement
    var UserGroupExpiryDate: String? = null

    @XmlElement
    var XsiNil: String? = null
}

@Serializable
class UserProfiles {
    lateinit var userProfile: Array<UserProfile>
}

@Serializable
// UserProfile.java
class UserProfile {
    @XmlElement
    var UserProfileType: String? = null

    @XmlElement
    var UserProfilePropietaryCompany: String? = null

    @XmlElement
    var UserProfileTypeName: String? = null

    @XmlElement
    var UserProfileValidityDate: String? = null

    @XmlElement
    var UserProfileExpiryDate: String? = null

    var index: String? = null
}


@Serializer(forClass = LocalDate::class)
object DateSerializer : KSerializer<LocalDate> {

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

@Serializer(forClass = OffsetDateTime::class)
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }
}

