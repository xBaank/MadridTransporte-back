
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoDia.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="TipoDia">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="LB"/>
 *     <enumeration value="LV"/>
 *     <enumeration value="VF"/>
 *     <enumeration value="FV"/>
 *     <enumeration value="FN"/>
 *     <enumeration value="TO"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "TipoDia")
@XmlEnum
public enum TipoDia {

    LB,
    LV,
    VF,
    FV,
    FN,
    TO;

    public String value() {
        return name();
    }

    public static TipoDia fromValue(String v) {
        return valueOf(v);
    }

}
