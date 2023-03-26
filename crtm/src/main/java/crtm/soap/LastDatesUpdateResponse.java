
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
 *         <element name="lastDatesUpdate" type="{GEIS.MultimodalInfoWebService}LastDatesUpdate" minOccurs="0"/>
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
    "lastDatesUpdate"
})
@XmlRootElement(name = "LastDatesUpdateResponse")
public class LastDatesUpdateResponse {

    protected LastDatesUpdate lastDatesUpdate;

    /**
     * Gets the value of the lastDatesUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link LastDatesUpdate }
     *     
     */
    public LastDatesUpdate getLastDatesUpdate() {
        return lastDatesUpdate;
    }

    /**
     * Sets the value of the lastDatesUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link LastDatesUpdate }
     *     
     */
    public void setLastDatesUpdate(LastDatesUpdate value) {
        this.lastDatesUpdate = value;
    }

}
