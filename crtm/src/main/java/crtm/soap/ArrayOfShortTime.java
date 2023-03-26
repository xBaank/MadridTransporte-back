
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfShortTime complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfShortTime">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ShortTime" type="{GEIS.MultimodalInfoWebService}ShortTime" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfShortTime", propOrder = {
    "shortTime"
})
public class ArrayOfShortTime {

    @XmlElement(name = "ShortTime", nillable = true)
    protected List<ShortTime> shortTime;

    /**
     * Gets the value of the shortTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the shortTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShortTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShortTime }
     * 
     * 
     * @return
     *     The value of the shortTime property.
     */
    public List<ShortTime> getShortTime() {
        if (shortTime == null) {
            shortTime = new ArrayList<>();
        }
        return this.shortTime;
    }

}
