package busTrackerApi.routing.stops.train.cano

import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val SLASH = "/"

abstract class ElcanoAuth {
    protected var contentType: String? = null
    protected var elcanoAccessKey: String? = null
    protected var elcanoSecretKey: String? = null
    protected var headers: TreeMap<String, String?>? = null
    protected val hexArray: CharArray = "0123456789ABCDEF".toCharArray()
    protected var host: String? = null
    protected var httpMethodName: String? = null
    protected var params: String? = null
    protected var path: String? = null
    protected var payload: String? = null
    protected var requestDate: Date? = null
    protected var signedHeaders: String? = null
    protected var xElcanoClient: String? = null
    protected var xElcanoDate: String? = null
    protected var xElcanoDateSimple: String? = null
    protected var xElcanoUserId: String? = null

    fun getHeaders(): Map<String, String?>? {
        val treeMap = TreeMap<String, String?>()
        this.headers = treeMap
        treeMap[HEADER_X_ELCANO_HOST] = host
        headers!![HEADER_CONTENT_CONTENTTYPE] = this.contentType
        headers!![HEADER_X_ELCANO_CLIENT] = this.xElcanoClient
        headers!![HEADER_X_ELCANO_DATE] = this.timeStamp
        headers!![HEADER_X_ELCANO_USERID] = this.xElcanoUserId
        headers!!["Authorization"] = calculateHeaderAuthorization()
        return this.headers
    }

    val headerAuthorization: String get() = calculateHeaderAuthorization()

    private fun calculateHeaderAuthorization(): String {
        val calculateSignature = calculateSignature(prepareStringToSign(prepareCanonicalRequest()))
        return buildAuthorizationString(calculateSignature)
    }

    fun prepareCanonicalRequest(): String {
        val sb = StringBuilder()
        sb.append(this.httpMethodName)
        sb.append("\n")
        sb.append(this.path)
        sb.append("\n")
        sb.append(this.params)
        sb.append("\n")
        sb.append(HEADER_CONTENT_CONTENTTYPE.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(this.contentType)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_HOST.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(this.host)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_CLIENT.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(this.xElcanoClient)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_DATE.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(this.xElcanoDate)
        sb.append("\n")
        sb.append(HEADER_X_ELCANO_USERID.lowercase(Locale.getDefault()))
        sb.append(":")
        sb.append(this.xElcanoUserId)
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
        this.signedHeaders =
            HEADER_CONTENT_CONTENTTYPE.lowercase(Locale.getDefault()) + ";" + HEADER_X_ELCANO_HOST.lowercase(
                Locale.getDefault()
            ) + ";" + HEADER_X_ELCANO_CLIENT.lowercase(Locale.getDefault()) + ";" + HEADER_X_ELCANO_DATE.lowercase(
                Locale.getDefault()
            ) + ";" + HEADER_X_ELCANO_USERID.lowercase(Locale.getDefault())
        val formatPayload = formatPayload(this.payload)
        this.payload = formatPayload
        sb.append(toHex(formatPayload))
        return sb.toString()
    }

    fun prepareStringToSign(str: String): String {
        return ((((("""
    HMAC-SHA256
    ${xElcanoDate}
    
    """.trimIndent()) + this.xElcanoDateSimple + SLASH) + this.xElcanoClient + SLASH) + this.xElcanoUserId + SLASH) + ELCANO_REQUEST + "\n") + toHex(
            str
        )
    }

    fun calculateSignature(str: String?): String {
        try {
            return bytesToHex(
                hmacSha256(
                    getSignatureKey(
                        this.elcanoSecretKey,
                        this.xElcanoDateSimple,
                        this.xElcanoClient
                    ), str
                )
            )
        } catch (e: Exception) {
            throw Exception("The calculation of the signature throws an exception. Ex: " + e.message)
        }
    }

    fun buildAuthorizationString(str: String): String {
        return (((("HMAC-SHA256 Credential=" + this.elcanoAccessKey + SLASH) + this.xElcanoDateSimple + SLASH) + this.xElcanoClient + SLASH) + this.xElcanoUserId + SLASH) + ELCANO_REQUEST + ",SignedHeaders=" + this.signedHeaders + ",Signature=" + str
    }

    fun formatPayload(str: String?): String {
        var str = str
        if (str == null) {
            str = ""
        }
        return str.replace("\r", "").replace("\n", "").replace(" ", "")
    }

    fun toHex(str: String): String {
        try {
            val instance = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256)
            instance.update(str.toByteArray(StandardCharsets.UTF_8))
            return String.format("%064x", *arrayOf<Any>(BigInteger(1, instance.digest())))
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("The algorithm " + MessageDigestAlgorithms.SHA_256 + " is not supported. Ex: " + e.message)
        }
    }

    fun hmacSha256(bArr: ByteArray?, str: String?): ByteArray {
        try {
            val instance = Mac.getInstance("HmacSHA256")
            instance.init(SecretKeySpec(bArr, "HmacSHA256"))
            return instance.doFinal(str!!.toByteArray(StandardCharsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("The algorithm " + "HmacSHA256" + " is not supported. Ex: " + e.message)
        } catch (e2: InvalidKeyException) {
            throw Exception("The key is not valid. Ex: " + e2.message)
        }
    }

    fun getSignatureKey(str: String?, str2: String?, str3: String?): ByteArray {
        return hmacSha256(hmacSha256(hmacSha256(str!!.toByteArray(StandardCharsets.UTF_8), str2), str3), ELCANO_REQUEST)
    }

    fun bytesToHex(bArr: ByteArray) = bArr.joinToString("") { "%02x".format(it) }

    val timeStamp: String get() = getTimeStamp(this.requestDate)

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