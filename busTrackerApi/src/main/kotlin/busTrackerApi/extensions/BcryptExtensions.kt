package busTrackerApi.extensions

import com.toxicbakery.bcrypt.Bcrypt

fun Bcrypt.hashAsString(input: String, saltRounds: Int = 10): String =
    hash(input, saltRounds).toString(Charsets.UTF_8)

fun Bcrypt.verifyHash(input: String, hash: String): Boolean =
    verify(input, hash.toByteArray(Charsets.UTF_8))

