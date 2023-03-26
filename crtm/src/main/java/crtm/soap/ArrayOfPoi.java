
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPoi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfPoi">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Poi" type="{GEIS.MultimodalInfoWebService}Poi" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPoi", propOrder = {
    "poi"
})
public class ArrayOfPoi {

    @XmlElement(name = "Poi", nillable = true)
    protected List<Poi> poi;

    /**
     * Gets the value of the poi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the poi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Poi }
     * 
     * 
     * @return
     *     The value of the poi property.
     */
    public List<Poi> getPoi() {
        if (poi == null) {
            poi = new ArrayList<>();
        }
        return this.poi;
    }

}
