
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfVehicleLocation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfVehicleLocation">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="VehicleLocation" type="{GEIS.MultimodalInfoWebService}VehicleLocation" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVehicleLocation", propOrder = {
    "vehicleLocation"
})
public class ArrayOfVehicleLocation {

    @XmlElement(name = "VehicleLocation", nillable = true)
    protected List<VehicleLocation> vehicleLocation;

    /**
     * Gets the value of the vehicleLocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the vehicleLocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicleLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VehicleLocation }
     * 
     * 
     * @return
     *     The value of the vehicleLocation property.
     */
    public List<VehicleLocation> getVehicleLocation() {
        if (vehicleLocation == null) {
            vehicleLocation = new ArrayList<>();
        }
        return this.vehicleLocation;
    }

}
