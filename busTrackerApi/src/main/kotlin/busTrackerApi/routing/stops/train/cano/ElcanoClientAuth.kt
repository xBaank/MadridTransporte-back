package busTrackerApi.routing.stops.train.cano

import java.util.*

class ElcanoClientAuth : ElcanoAuth {

    class Builder(val elcanoAccessKey: String, val elcanoSecretKey: String) {
        var contentType: String? = null

        var host: String? = null

        var httpMethodName: String? = null

        var params: String? = null

        var path: String? = null

        var payload: String? = null

        var requestDate: Date? = null

        var xElcanoClient: String? = null

        var xElcanoUserId: String? = null

        fun host(str: String?): Builder {
            this.host = str
            return this
        }

        fun path(str: String?): Builder {
            this.path = str
            return this
        }

        fun params(str: String?): Builder {
            this.params = str
            return this
        }

        fun contentType(str: String?): Builder {
            this.contentType = str
            return this
        }

        fun requestDate(date: Date?): Builder {
            this.requestDate = date
            return this
        }

        fun xElcanoClient(str: String?): Builder {
            this.xElcanoClient = str
            return this
        }

        fun xElcanoUserId(str: String?): Builder {
            this.xElcanoUserId = str
            return this
        }

        fun httpMethodName(str: String?): Builder {
            this.httpMethodName = str
            return this
        }

        fun payload(str: String?): Builder {
            this.payload = str
            return this
        }

        fun build(): ElcanoClientAuth {
            return ElcanoClientAuth(this)
        }
    }

    protected constructor(builder: Builder) {
        this.elcanoAccessKey = builder.elcanoAccessKey
        this.elcanoSecretKey = builder.elcanoSecretKey
        this.host = builder.host
        this.path = builder.path
        this.params = builder.params
        this.httpMethodName = builder.httpMethodName
        this.payload = builder.payload
        this.contentType = builder.contentType
        this.xElcanoClient = builder.xElcanoClient
        this.requestDate = if (builder.requestDate == null) Date() else builder.requestDate
        this.xElcanoDate = getTimeStamp(this.requestDate)
        this.xElcanoDateSimple = getDate(this.requestDate)
        this.xElcanoUserId = builder.xElcanoUserId
    }
}