
package crtm.soap;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AlertEffect.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>{@code
 * <simpleType name="AlertEffect">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="SIN_SERVICIO"/>
 *     <enumeration value="SERVICIO_REDUCIDO"/>
 *     <enumeration value="RETRASOS_IMPORTANTES"/>
 *     <enumeration value="DESVÍO"/>
 *     <enumeration value="SERVICIO_ADICIONAL"/>
 *     <enumeration value="SERVICIO_MODIFICADO"/>
 *     <enumeration value="OTRO_EFECTO"/>
 *     <enumeration value="EFECTO_DESCONOCIDO"/>
 *     <enumeration value="MOVIMIENTO_PARADO"/>
 *   </restriction>
 * </simpleType>
 * }</pre>
 * 
 */
@XmlType(name = "AlertEffect")
@XmlEnum
public enum AlertEffect {

    SIN_SERVICIO,
    SERVICIO_REDUCIDO,
    RETRASOS_IMPORTANTES,
    DESVÍO,
    SERVICIO_ADICIONAL,
    SERVICIO_MODIFICADO,
    OTRO_EFECTO,
    EFECTO_DESCONOCIDO,
    MOVIMIENTO_PARADO;

    public String value() {
        return name();
    }

    public static AlertEffect fromValue(String v) {
        return valueOf(v);
    }

}
