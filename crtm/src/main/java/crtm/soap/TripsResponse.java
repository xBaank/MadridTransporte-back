
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
 *         <element name="Expediciones" type="{GEIS.MultimodalInfoWebService}ArrayOfExpedicion" minOccurs="0"/>
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
    "expediciones"
})
@XmlRootElement(name = "TripsResponse")
public class TripsResponse {

    @XmlElement(name = "Expediciones")
    protected ArrayOfExpedicion expediciones;

    /**
     * Gets the value of the expediciones property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfExpedicion }
     *     
     */
    public ArrayOfExpedicion getExpediciones() {
        return expediciones;
    }

    /**
     * Sets the value of the expediciones property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfExpedicion }
     *     
     */
    public void setExpediciones(ArrayOfExpedicion value) {
        this.expediciones = value;
    }

}
