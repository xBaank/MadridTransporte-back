
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         <element name="alerts" type="{GEIS.MultimodalInfoWebService}ArrayOfAlert" minOccurs="0"/>
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
    "alerts"
})
@XmlRootElement(name = "AlertsResponse")
public class AlertsResponse {

    protected ArrayOfAlert alerts;

    /**
     * Gets the value of the alerts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAlert }
     *     
     */
    public ArrayOfAlert getAlerts() {
        return alerts;
    }

    /**
     * Sets the value of the alerts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAlert }
     *     
     */
    public void setAlerts(ArrayOfAlert value) {
        this.alerts = value;
    }

}
