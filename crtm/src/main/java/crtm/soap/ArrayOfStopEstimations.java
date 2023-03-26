
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfStopEstimations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfStopEstimations">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StopEstimations" type="{GEIS.MultimodalInfoWebService}StopEstimations" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStopEstimations", propOrder = {
    "stopEstimations"
})
public class ArrayOfStopEstimations {

    @XmlElement(name = "StopEstimations", nillable = true)
    protected List<StopEstimations> stopEstimations;

    /**
     * Gets the value of the stopEstimations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the stopEstimations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStopEstimations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StopEstimations }
     * 
     * 
     * @return
     *     The value of the stopEstimations property.
     */
    public List<StopEstimations> getStopEstimations() {
        if (stopEstimations == null) {
            stopEstimations = new ArrayList<>();
        }
        return this.stopEstimations;
    }

}
