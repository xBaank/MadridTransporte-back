
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoExpedicion.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="TipoExpedicion">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="CAMBIO_DE_LINEA"/>
 *     <enumeration value="ENCIERRO"/>
 *     <enumeration value="EN_SERVICIO"/>
 *     <enumeration value="REFUERZO"/>
 *     <enumeration value="SALIDA"/>
 *     <enumeration value="EN_VACIO"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "TipoExpedicion")
@XmlEnum
public enum TipoExpedicion {

    CAMBIO_DE_LINEA,
    ENCIERRO,
    EN_SERVICIO,
    REFUERZO,
    SALIDA,
    EN_VACIO;

    public String value() {
        return name();
    }

    public static TipoExpedicion fromValue(String v) {
        return valueOf(v);
    }

}
