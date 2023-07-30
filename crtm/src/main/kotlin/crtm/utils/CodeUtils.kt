package crtm.utils

//TODO Only works for codMode 8 aka interurban
fun createLineCode(codMode: String, lineCode: String) = "${codMode}__${lineCode}___"
fun createStopCode(codMode: String, stopCode: String) = "${codMode}_${stopCode}"
fun getCodModeFromLineCode(input: String): String = input.substringBefore("__")
fun getSimpleLineCodeFromLineCode(input: String): String = input.substringAfter("__").substringBefore("___")


fun getCodStopFromStopCode(input: String): String = input.substringAfter("_")

fun isValidLineCode(input: String): Boolean = input.matches(Regex("\\d__\\d{0,3}___|\\d__\\d__\\d+_"))
fun isValidStopCode(input: String): Boolean = input.matches(Regex("^[0-9]_[0-9]{3}$"))
