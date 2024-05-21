package common.utils

//TODO Only works for codMode 8 aka interurban
fun createLineCode(codMode: String, lineCode: String): String = "${codMode}__${lineCode}___"
fun createStopCode(codMode: String, stopCode: String) = "${codMode}_${stopCode}"
fun getCodModeFromLineCode(input: String): String = input.substringBefore("__")
fun getSimpleLineCodeFromLineCode(input: String): String = input.substringAfter("__").substringBefore("___")
fun getStopCodeFromFullStopCode(input: String): String = input.substringAfter("_")
fun getCodModeFromFullStopCode(input: String): String = input.substringBefore("_")


const val metroCodMode = "4"
const val tramCodMode = "10"
const val trainCodMode = "5"
const val emtCodMode = "6"
const val busCodMode = "8"
const val urbanCodMode = "9"