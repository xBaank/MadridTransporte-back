
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoActividad.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="TipoActividad">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="SALIDA_DE_COCHERAS"/>
 *     <enumeration value="LLEGADA_A_COCHERAS"/>
 *     <enumeration value="SALIDA_DE_CABECERA"/>
 *     <enumeration value="LLEGADA_A_TERMINAL"/>
 *     <enumeration value="SALIDA_DE_ESTACION"/>
 *     <enumeration value="LLEGADA_A_ESTACION"/>
 *     <enumeration value="SALIDA_DE_REGULACION"/>
 *     <enumeration value="LLEGADA_A_REGULACION"/>
 *     <enumeration value="OTRO"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "TipoActividad")
@XmlEnum
public enum TipoActividad {

    SALIDA_DE_COCHERAS,
    LLEGADA_A_COCHERAS,
    SALIDA_DE_CABECERA,
    LLEGADA_A_TERMINAL,
    SALIDA_DE_ESTACION,
    LLEGADA_A_ESTACION,
    SALIDA_DE_REGULACION,
    LLEGADA_A_REGULACION,
    OTRO;

    public String value() {
        return name();
    }

    public static TipoActividad fromValue(String v) {
        return valueOf(v);
    }

}
