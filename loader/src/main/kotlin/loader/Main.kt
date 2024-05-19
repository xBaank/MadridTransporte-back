package loader

import common.DB
import common.extensions.getOrThrow

suspend fun main() {
    DB.setupMongo(EnvVariables.mongoConnectionString.getOrThrow())
    loadDataIntoDb()
}