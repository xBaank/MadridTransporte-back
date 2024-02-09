package api.routing.stops.train.cano

import api.exceptions.BusTrackerException
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val SLASH = "/"

abstract class ElcanoAuth {
    protected var contentType: String? = null
    protected var elcanoAccessKey: String? = null
    protected var elcanoSecretKey: String? = null
    protected var host: String? = null
    protected var httpMethodName: String? = null
    protected var params: String? = null
    protected var path: String? = null
    protected var payload: String? = null
    protected var requestDate: Date? = null
    private var signedHeaders: String? = null
    protected var xElcanoClient: String? = null
    protected var xElcanoDate: String? = null
    protected var xElcanoDateSimple: String? = null
    protected var xElcanoUserId: String? = null

    fun getHeaders(): Map<String, String?> {
        val headers = TreeMap<String, String?>()
        headers[HEADER_X_ELCANO_HOST] = host
        headers[HEADER_CONTENT_CONTENTTYPE] = contentType
        headers[HEADER_X_ELCANO_CLIENT] = xElcanoClient
        headers[HEADER_X_ELCANO_DATE] = timeStamp
        headers[HEADER_X_ELCANO_USERID] = xElcanoUserId
        headers["Authorization"] = calculateHeaderAuthorization()
        return headers
    }

    private fun calculateHeaderAuthorization(): String {
        val calculateSignature = calculateSignature(prepareStringToSign(prepareCanonicalRequest()))
        return buildAuthorizationString(calculateSignature)
    }

    private fun prepareCanonicalRequest(): String {
        val sb = StringBuilder()
        sb.append(httpMethodName)
        sb.append("\n")
        sb.append(path)
        sb.append("\n")
        sb.append(params)
        sb.append("\n")
        sb.append(HEADER_CONTENT_CONTENTTYPE.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(contentType)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_HOST.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(host)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_CLIENT.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(xElcanoClient)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_DATE.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(xElcanoDate)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_USERID.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(xElcanoUserId)
        sb.append("\n")
        sb.append(HEADER_CONTENT_CONTENTTYPE.lowercase(Locale.getDefault()))
        sb.append(";")
        sb.append(HEADER_X_ELCANO_HOST.lowercase(Locale.getDefault()))
        sb.append(";")
        sb.append(HEADER_X_ELCANO_CLIENT.lowercase(Locale.getDefault()))
        sb.append(";")
        sb.append(HEADER_X_ELCANO_DATE.lowercase(Locale.getDefault()))
        sb.append(";")
        sb.append(HEADER_X_ELCANO_USERID.lowercase(Locale.getDefault()))
        sb.append("\n")
        signedHeaders =
            HEADER_CONTENT_CONTENTTYPE.lowercase(Locale.getDefault()) + ";" + HEADER_X_ELCANO_HOST.lowercase(
                Locale.getDefault()
            ) + ";" + HEADER_X_ELCANO_CLIENT.lowercase(Locale.getDefault()) + ";" + HEADER_X_ELCANO_DATE.lowercase(
                Locale.getDefault()
            ) + ";" + HEADER_X_ELCANO_USERID.lowercase(Locale.getDefault())
        val formatPayload = formatPayload(payload)
        payload = formatPayload
        sb.append(toHex(formatPayload))
        return sb.toString()
    }

    private fun prepareStringToSign(str: String): String =
        ("HMAC-SHA256\n$xElcanoDate\n${xElcanoDateSimple}$SLASH${xElcanoClient}$SLASH${xElcanoUserId}$SLASH$ELCANO_REQUEST\n") + toHex(
            str
        )

    private fun calculateSignature(str: String): String = bytesToHex(
        hmacSha256(
            getSignatureKey(
                elcanoSecretKey ?: throw BusTrackerException.InternalServerError("Key is null"),
                xElcanoDateSimple ?: throw BusTrackerException.InternalServerError("Date is null"),
                xElcanoClient ?: throw BusTrackerException.InternalServerError("Client is null")
            ), str
        )
    )

    private fun buildAuthorizationString(str: String): String =
        "${"HMAC-SHA256 Credential=${elcanoAccessKey}$SLASH"}${xElcanoDateSimple}$SLASH${xElcanoClient}$SLASH${xElcanoUserId}$SLASH$ELCANO_REQUEST,SignedHeaders=${signedHeaders},Signature=$str"

    private fun formatPayload(str: String?): String = str?.replace("\r", "")
        ?.replace("\n", "")
        ?.replace(" ", "")
        ?: ""

    private fun toHex(str: String): String {
        val instance = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256)
        instance.update(str.toByteArray(StandardCharsets.UTF_8))
        return String.format("%064x", *arrayOf<Any>(BigInteger(1, instance.digest())))
    }

    private fun hmacSha256(bArr: ByteArray?, str: String): ByteArray {
        val instance = Mac.getInstance("HmacSHA256")
        instance.init(SecretKeySpec(bArr, "HmacSHA256"))
        return instance.doFinal(str.toByteArray(StandardCharsets.UTF_8))
    }

    private fun getSignatureKey(str: String, str2: String, str3: String): ByteArray {
        return hmacSha256(hmacSha256(hmacSha256(str.toByteArray(StandardCharsets.UTF_8), str2), str3), ELCANO_REQUEST)
    }

    private fun bytesToHex(bArr: ByteArray) = bArr.joinToString("") { "%02x".format(it) }

    private val timeStamp: String get() = getTimeStamp(requestDate)

    companion object {
        protected const val ELCANO_REQUEST: String = "elcano_request"
        const val HEADER_CONTENT_CONTENTTYPE: String = "Content-type"
        const val HEADER_X_ELCANO_CLIENT: String = "X-Elcano-Client"
        const val HEADER_X_ELCANO_DATE: String = "X-Elcano-Date"
        const val HEADER_X_ELCANO_HOST: String = "X-Elcano-Host"
        const val HEADER_X_ELCANO_USERID: String = "X-Elcano-UserId"

        @JvmStatic
        protected fun getTimeStamp(date: Date?): String {
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.format(date)
        }

        @JvmStatic
        protected fun getDate(date: Date?): String {
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.format(date)
        }
    }
}