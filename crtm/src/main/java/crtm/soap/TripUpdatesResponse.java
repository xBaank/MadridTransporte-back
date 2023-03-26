
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
 *         <element name="TripUpdates" type="{GEIS.MultimodalInfoWebService}ArrayOfTripUpdate" minOccurs="0"/>
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
    "tripUpdates"
})
@XmlRootElement(name = "TripUpdatesResponse")
public class TripUpdatesResponse {

    @XmlElement(name = "TripUpdates")
    protected ArrayOfTripUpdate tripUpdates;

    /**
     * Gets the value of the tripUpdates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTripUpdate }
     *     
     */
    public ArrayOfTripUpdate getTripUpdates() {
        return tripUpdates;
    }

    /**
     * Sets the value of the tripUpdates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTripUpdate }
     *     
     */
    public void setTripUpdates(ArrayOfTripUpdate value) {
        this.tripUpdates = value;
    }

}
