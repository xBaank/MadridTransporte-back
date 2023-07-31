package crtm

import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import crtm.utils.authHeader

val defaultClient by lazy { MultimodalInformation_Service().basicHttp }

private val privateKey = "pruebapruebapruebapruebaprueba12".toByteArray()
fun MultimodalInformation.auth(): AuthHeader {
    val key = getPublicKey(PublicKeyRequest())
    return authHeader(key.key.toByteArray(), privateKey)
}