
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
 *     <enumeration value="PROBLEMA_T�CNICO"/>
 *     <enumeration value="HUELGA"/>
 *     <enumeration value="DEMOSTRACI�N"/>
 *     <enumeration value="ACCIDENTE"/>
 *     <enumeration value="VACACIONES"/>
 *     <enumeration value="TEMPORAL_ATMOSF�RICO"/>
 *     <enumeration value="MANTENIMIENTO"/>
 *     <enumeration value="OBRAS"/>
 *     <enumeration value="ACTIVIDAD_POLICIAL"/>
 *     <enumeration value="EMERGENCIA_M�DICA"/>
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
    PROBLEMA_T�CNICO,
    HUELGA,
    DEMOSTRACI�N,
    ACCIDENTE,
    VACACIONES,
    TEMPORAL_ATMOSF�RICO,
    MANTENIMIENTO,
    OBRAS,
    ACTIVIDAD_POLICIAL,
    EMERGENCIA_M�DICA;

    public String value() {
        return name();
    }

    public static AlertCause fromValue(String v) {
        return valueOf(v);
    }

}
