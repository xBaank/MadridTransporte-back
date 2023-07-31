package crtm

import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import crtm.utils.authHeader

//TODO Initliaze this in a better way, here it can throw an exception
val defaultClient = MultimodalInformation_Service().basicHttp

val privateKey = "pruebapruebapruebapruebaprueba12".toByteArray()
fun MultimodalInformation.auth(): AuthHeader {
    val key = getPublicKey(PublicKeyRequest())
    return authHeader(key.key.toByteArray(), privateKey)
}