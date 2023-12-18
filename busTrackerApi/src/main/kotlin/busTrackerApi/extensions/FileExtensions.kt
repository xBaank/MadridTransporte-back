package busTrackerApi.extensions

import java.io.*
import java.util.zip.ZipFile
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

fun File.removeFirstLine(): File {
    var index = 0
    val newFile = createTempFile().toFile()
    newFile.deleteOnExit()
    useLines { lines ->
        lines.forEach {
            if (index == 0) {
                index++
                return@useLines
            }
            newFile.appendText(it)
            index += 1
        }
    }
    return newFile
}

fun File.unzip(): String {
    val dir = createTempDirectory().toString()
    unzip(dir)
    return dir
}

fun File.unzip(destDirectory: String) {
    File(destDirectory).run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipFile(path).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = destDirectory + File.separator + entry.name

                if (!entry.isDirectory) {
                    // if the entry is a file, extracts it
                    extractFile(input, filePath)
                } else {
                    // if the entry is a directory, make the directory
                    val dir = File(filePath)
                    dir.mkdir()
                }
            }
        }
    }
}

@Throws(IOException::class)
private fun extractFile(inputStream: InputStream, destFilePath: String) {
    val bos = BufferedOutputStream(FileOutputStream(destFilePath))
    val bytesIn = ByteArray(4096)
    var read: Int
    while (inputStream.read(bytesIn).also { read = it } != -1) {
        bos.write(bytesIn, 0, read)
    }
    bos.close()
}