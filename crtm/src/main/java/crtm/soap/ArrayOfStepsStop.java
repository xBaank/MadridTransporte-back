
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfStepsStop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfStepsStop">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StepsStop" type="{GEIS.MultimodalInfoWebService}StepsStop" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStepsStop", propOrder = {
    "stepsStop"
})
public class ArrayOfStepsStop {

    @XmlElement(name = "StepsStop", nillable = true)
    protected List<StepsStop> stepsStop;

    /**
     * Gets the value of the stepsStop property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the stepsStop property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStepsStop().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepsStop }
     * 
     * 
     * @return
     *     The value of the stepsStop property.
     */
    public List<StepsStop> getStepsStop() {
        if (stepsStop == null) {
            stepsStop = new ArrayList<>();
        }
        return this.stepsStop;
    }

}
