package busTrackerApi.extensions

import java.io.*
import java.util.zip.ZipFile
import kotlin.io.path.createTempDirectory

fun File.unzip() = unzip(createTempDirectory().toString())
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