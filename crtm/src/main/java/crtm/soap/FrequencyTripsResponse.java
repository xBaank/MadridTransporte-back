
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
 *         <element name="Expediciones_de_Frecuencia" type="{GEIS.MultimodalInfoWebService}ArrayOfExpedicionFrecuencia" minOccurs="0"/>
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
    "expedicionesDeFrecuencia"
})
@XmlRootElement(name = "FrequencyTripsResponse")
public class FrequencyTripsResponse {

    @XmlElement(name = "Expediciones_de_Frecuencia")
    protected ArrayOfExpedicionFrecuencia expedicionesDeFrecuencia;

    /**
     * Gets the value of the expedicionesDeFrecuencia property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfExpedicionFrecuencia }
     *     
     */
    public ArrayOfExpedicionFrecuencia getExpedicionesDeFrecuencia() {
        return expedicionesDeFrecuencia;
    }

    /**
     * Sets the value of the expedicionesDeFrecuencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfExpedicionFrecuencia }
     *     
     */
    public void setExpedicionesDeFrecuencia(ArrayOfExpedicionFrecuencia value) {
        this.expedicionesDeFrecuencia = value;
    }

}
