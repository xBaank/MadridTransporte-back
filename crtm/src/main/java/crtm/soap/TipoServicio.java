
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoServicio.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="TipoServicio">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="NORMAL"/>
 *     <enumeration value="NOCTURNO"/>
 *     <enumeration value="EXPRESS"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "TipoServicio")
@XmlEnum
public enum TipoServicio {

    NORMAL,
    NOCTURNO,
    EXPRESS;

    public String value() {
        return name();
    }

    public static TipoServicio fromValue(String v) {
        return valueOf(v);
    }

}
