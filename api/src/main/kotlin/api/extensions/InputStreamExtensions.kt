package api.extensions

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


fun InputStream.unzip(): Map<String, String> {
    val entries = mutableMapOf<String, String>()

    ZipInputStream(this).use { zipStream ->
        var entry: ZipEntry? = zipStream.nextEntry
        while (entry != null) {
            val content = ByteArrayOutputStream()
            zipStream.copyTo(content)
            entries[entry.name] = content.toString("UTF-8")
            entry = zipStream.nextEntry
        }
    }

    return entries
}
