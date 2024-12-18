package loader

import java.io.*
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.*

fun File.removeFirstLine(): File {
    var index = 0
    val newFile = createTempFile().toFile()
    newFile.deleteOnExit()
    val writer = newFile.writer()
    writer.use {
        useLines { lines ->
            lines.forEach {
                if (index == 0) {
                    index++
                    return@forEach
                }
                writer.appendLine(it)
                index += 1
            }
        }
        writer.flush()
    }
    return newFile
}

fun File.unzip(): Path {
    val dir = createTempDirectory().toString()
    unzip(dir)
    return Path.of(dir)
}


@Suppress("FunctionName")
suspend fun Path.`fix CRTM 💩`(): Path {
    fun deepFlatten(path: Path): Sequence<Path> = sequence {
        if (!path.isDirectory()) {
            yield(path)
            return@sequence
        }

        val entries = path.listDirectoryEntries()
        entries.forEach {
            when {
                it.isDirectory() -> yieldAll(deepFlatten(it))
                else -> yield(it)
            }
        }
    }

    val subFolderFiles = listDirectoryEntries().flatMap { deepFlatten(it) }

    subFolderFiles.filter { it.extension != "zip" }.forEach {
        val targetFile = this.resolve(it.name)
        it.moveTo(targetFile, overwrite = true)
        targetFile.toFile().deleteOnExit()
    }

    subFolderFiles.asSequence()
        .filter { it.extension == "zip" }
        .map { it.toFile().unzip() }
        .flatMap { it.listDirectoryEntries().filter { file -> file.extension == "txt" } }
        .groupBy { it.name }
        .map { it.key to getFromFile(it.value.map(Path::toString)) }.toList()
        .forEach {
            val targetFile = this.resolve(it.first)
            gtfsReader.openAsync(it.second) {
                val data = readAllAsSequence()
                gtfsWriter.openAsync(targetFile.toFile()) {
                    writeRows(data)
                }
            }
        }
    return this
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
                val dir = File(filePath)
                dir.deleteOnExit()

                if (!entry.isDirectory) {
                    // if the entry is a file, extracts it
                    extractFile(input, filePath)
                } else {
                    // if the entry is a directory, make the directory
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