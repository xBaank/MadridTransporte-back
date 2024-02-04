package api.extensions

import javax.xml.datatype.XMLGregorianCalendar

fun XMLGregorianCalendar.toMiliseconds() = this.toGregorianCalendar().timeInMillis