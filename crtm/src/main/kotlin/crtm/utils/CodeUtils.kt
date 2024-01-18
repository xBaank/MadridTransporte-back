package crtm.utils

//TODO Only works for codMode 8 aka interurban
fun createLineCode(codMode: String, lineCode: String): String = "${codMode}__${lineCode}___"
fun createStopCode(codMode: String, stopCode: String) = "${codMode}_${stopCode}"
fun getCodModeFromLineCode(input: String): String = input.substringBefore("__")
fun getSimpleLineCodeFromLineCode(input: String): String = input.substringAfter("__").substringBefore("___")
fun getStopCodeFromFullStopCode(input: String): String = input.substringAfter("_")
