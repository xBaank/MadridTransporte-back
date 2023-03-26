
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ServiciosHorarios" type="{GEIS.MultimodalInfoWebService}ArrayOfServicioHorario" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviciosHorarios"
})
@XmlRootElement(name = "CalendarsResponse")
public class CalendarsResponse {

    @XmlElement(name = "ServiciosHorarios")
    protected ArrayOfServicioHorario serviciosHorarios;

    /**
     * Gets the value of the serviciosHorarios property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfServicioHorario }
     *     
     */
    public ArrayOfServicioHorario getServiciosHorarios() {
        return serviciosHorarios;
    }

    /**
     * Sets the value of the serviciosHorarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfServicioHorario }
     *     
     */
    public void setServiciosHorarios(ArrayOfServicioHorario value) {
        this.serviciosHorarios = value;
    }

}
