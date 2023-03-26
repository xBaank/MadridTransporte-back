package crtm.utils

import crtm.soap.AuthHeader
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val ivParameterSpec = IvParameterSpec(ByteArray(16))
fun encrypt(inputText: ByteArray, key: SecretKeySpec, iv: IvParameterSpec): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)
    val cipherText = cipher.doFinal(inputText)
    return Base64.getEncoder().encodeToString(cipherText)
}

fun authHeader(publicKey : ByteArray, privateKey : ByteArray) = AuthHeader().apply {
    connectionKey = encrypt(publicKey, SecretKeySpec(privateKey,"AES"), ivParameterSpec)
}
