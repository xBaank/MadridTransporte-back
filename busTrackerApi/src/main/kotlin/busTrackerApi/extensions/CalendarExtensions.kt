package busTrackerApi.extensions

import javax.xml.datatype.XMLGregorianCalendar

fun XMLGregorianCalendar.toMiliseconds() = this.toGregorianCalendar().timeInMillis