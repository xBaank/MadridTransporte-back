
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoEstacion.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="TipoEstacion">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="PARADA"/>
 *     <enumeration value="CABECERA"/>
 *     <enumeration value="PARADA_BAJO_DEMANDA"/>
 *     <enumeration value="FIN_DE_RUTA"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "TipoEstacion")
@XmlEnum
public enum TipoEstacion {

    PARADA,
    CABECERA,
    PARADA_BAJO_DEMANDA,
    FIN_DE_RUTA;

    public String value() {
        return name();
    }

    public static TipoEstacion fromValue(String v) {
        return valueOf(v);
    }

}
