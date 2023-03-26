
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AlertCause.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="AlertCause">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="DESCONOCIDA"/>
 *     <enumeration value="OTRA_CAUSA"/>
 *     <enumeration value="PROBLEMA_TÉCNICO"/>
 *     <enumeration value="HUELGA"/>
 *     <enumeration value="DEMOSTRACIÓN"/>
 *     <enumeration value="ACCIDENTE"/>
 *     <enumeration value="VACACIONES"/>
 *     <enumeration value="TEMPORAL_ATMOSFÉRICO"/>
 *     <enumeration value="MANTENIMIENTO"/>
 *     <enumeration value="OBRAS"/>
 *     <enumeration value="ACTIVIDAD_POLICIAL"/>
 *     <enumeration value="EMERGENCIA_MÉDICA"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "AlertCause")
@XmlEnum
public enum AlertCause {

    DESCONOCIDA,
    OTRA_CAUSA,
    PROBLEMA_TÉCNICO,
    HUELGA,
    DEMOSTRACIÓN,
    ACCIDENTE,
    VACACIONES,
    TEMPORAL_ATMOSFÉRICO,
    MANTENIMIENTO,
    OBRAS,
    ACTIVIDAD_POLICIAL,
    EMERGENCIA_MÉDICA;

    public String value() {
        return name();
    }

    public static AlertCause fromValue(String v) {
        return valueOf(v);
    }

}
