
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfStopSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfStopSelector">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StopSelector" type="{GEIS.MultimodalInfoWebService}StopSelector" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStopSelector", propOrder = {
    "stopSelector"
})
public class ArrayOfStopSelector {

    @XmlElement(name = "StopSelector", nillable = true)
    protected List<StopSelector> stopSelector;

    /**
     * Gets the value of the stopSelector property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the stopSelector property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStopSelector().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StopSelector }
     * 
     * 
     * @return
     *     The value of the stopSelector property.
     */
    public List<StopSelector> getStopSelector() {
        if (stopSelector == null) {
            stopSelector = new ArrayList<>();
        }
        return this.stopSelector;
    }

}
