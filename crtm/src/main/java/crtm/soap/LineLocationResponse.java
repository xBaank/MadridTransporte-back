
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
 *         <element name="vehiclesLocation" type="{GEIS.MultimodalInfoWebService}ArrayOfVehicleLocation" minOccurs="0"/>
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
    "vehiclesLocation"
})
@XmlRootElement(name = "LineLocationResponse")
public class LineLocationResponse {

    protected ArrayOfVehicleLocation vehiclesLocation;

    /**
     * Gets the value of the vehiclesLocation property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVehicleLocation }
     *     
     */
    public ArrayOfVehicleLocation getVehiclesLocation() {
        return vehiclesLocation;
    }

    /**
     * Sets the value of the vehiclesLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVehicleLocation }
     *     
     */
    public void setVehiclesLocation(ArrayOfVehicleLocation value) {
        this.vehiclesLocation = value;
    }

}
