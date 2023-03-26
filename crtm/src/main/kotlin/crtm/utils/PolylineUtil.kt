package crtm.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

internal object PolylineUtil {
    /**
     * Encodes a polyline using Google's polyline algorithm
     * (See http://code.google.com/apis/maps/documentation/polylinealgorithm.html for more information).
     *
     * code derived from : https://gist.github.com/signed0/2031157
     *
     * @param (x,y)-Coordinates
     * @return polyline-string
     */
    fun encode(coords: List<Coordinate>): String {
        val result: MutableList<String> = mutableListOf()

        var prevLat = 0
        var prevLong = 0

        for ((long, lat) in coords) {
            val iLat = (lat * 1e5).toInt()
            val iLong = (long * 1e5).toInt()

            val deltaLat = encodeValue(iLat - prevLat)
            val deltaLong = encodeValue(iLong - prevLong)

            prevLat = iLat
            prevLong = iLong

            result.add(deltaLat)
            result.add(deltaLong)
        }

        return result.joinToString("")
    }

    private fun encodeValue(value: Int): String {
        // Step 2 & 4
        var actualValue = if (value < 0) (value shl 1).inv() else (value shl 1)

        // Step 5-8
        val chunks: List<Int> = splitIntoChunks(actualValue)

        // Step 9-10
        return chunks.map { (it + 63).toChar() }.joinToString("")
    }

    private fun splitIntoChunks(toEncode: Int): List<Int> {
        // Step 5-8
        val chunks = mutableListOf<Int>()
        var value = toEncode
        while (value >= 32) {
            chunks.add((value and 31) or (0x20))
            value = value shr 5
        }
        chunks.add(value)
        return chunks
    }

    /**
     * Decodes a polyline that has been encoded using Google's algorithm
     * (http://code.google.com/apis/maps/documentation/polylinealgorithm.html)
     *
     * code derived from : https://gist.github.com/signed0/2031157
     *
     * @param polyline-string
     * @return (long,lat)-Coordinates
     */
    fun decode(polyline: String): List<Coordinate> {
        val coordinateChunks: MutableList<MutableList<Int>> = mutableListOf()
        coordinateChunks.add(mutableListOf())

        for (char in polyline.toCharArray()) {
            // convert each character to decimal from ascii
            var value = char.code - 63

            // values that have a chunk following have an extra 1 on the left
            val isLastOfChunk = (value and 0x20) == 0
            value = value and (0x1F)

            coordinateChunks.last().add(value)

            if (isLastOfChunk)
                coordinateChunks.add(mutableListOf())
        }

        coordinateChunks.removeAt(coordinateChunks.lastIndex)

        val coordinates: MutableList<Double> = mutableListOf()

        for (coordinateChunk in coordinateChunks) {
            var coordinate = coordinateChunk.mapIndexed { i, chunk -> chunk shl (i * 5) }.reduce { i, j -> i or j }

            // there is a 1 on the right if the coordinate is negative
            if (coordinate and 0x1 > 0)
                coordinate = (coordinate).inv()

            coordinate = coordinate shr 1
            coordinates.add((coordinate).toDouble() / 100000.0)
        }

        val points: MutableList<Coordinate> = mutableListOf()
        var previousX = 0.0
        var previousY = 0.0

        for (i in 0 until coordinates.size step 2) {
            if (coordinates[i] == 0.0 && coordinates[i + 1] == 0.0)
                continue

            previousX += coordinates[i + 1]
            previousY += coordinates[i]

            points.add(Coordinate(round(previousX, 5), round(previousY, 5)))
        }
        return points
    }

    private fun round(value: Double, precision: Int) =
        (value * 10.0.pow(precision.toDouble())).toInt().toDouble() / 10.0.pow(precision.toDouble())

    /**
     * https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm
     */
    fun simplify(points: List<Coordinate>, epsilon: Double): List<Coordinate> {
        // Find the point with the maximum distance
        var dmax = 0.0
        var index = 0
        val end = points.size

        for (i in 1..(end - 2)) {
            val d = perpendicularDistance(points[i], points[0], points[end - 1])
            if (d > dmax) {
                index = i
                dmax = d
            }
        }
        // If max distance is greater than epsilon, recursively simplify
        return if (dmax > epsilon) {
            // Recursive call
            val recResults1: List<Coordinate> = simplify(points.subList(0, index + 1), epsilon)
            val recResults2: List<Coordinate> = simplify(points.subList(index, end), epsilon)

            // Build the result list
            listOf(recResults1.subList(0, recResults1.lastIndex), recResults2).flatMap { it.toList() }
        } else {
            listOf(points[0], points[end - 1])
        }
    }

    private fun perpendicularDistance(pt: Coordinate, lineFrom: Coordinate, lineTo: Coordinate): Double =
        abs((lineTo.longitude - lineFrom.longitude) * (lineFrom.latitude - pt.latitude) - (lineFrom.longitude - pt.longitude) * (lineTo.latitude - lineFrom.latitude)) /
                sqrt(
                    (lineTo.longitude - lineFrom.longitude).pow(2.0) + (lineTo.latitude - lineFrom.latitude).pow(2.0)
                )

}

class Coordinate(val longitude: Double,val latitude: Double) {
    operator fun component1() = longitude
    operator fun component2() = latitude
}
