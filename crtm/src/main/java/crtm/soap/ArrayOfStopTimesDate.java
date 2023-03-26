
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfStopTimesDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfStopTimesDate">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StopTimesDate" type="{GEIS.MultimodalInfoWebService}StopTimesDate" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStopTimesDate", propOrder = {
    "stopTimesDate"
})
public class ArrayOfStopTimesDate {

    @XmlElement(name = "StopTimesDate", nillable = true)
    protected List<StopTimesDate> stopTimesDate;

    /**
     * Gets the value of the stopTimesDate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the stopTimesDate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStopTimesDate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StopTimesDate }
     * 
     * 
     * @return
     *     The value of the stopTimesDate property.
     */
    public List<StopTimesDate> getStopTimesDate() {
        if (stopTimesDate == null) {
            stopTimesDate = new ArrayList<>();
        }
        return this.stopTimesDate;
    }

}
