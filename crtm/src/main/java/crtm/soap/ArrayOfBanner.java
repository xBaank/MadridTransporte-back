
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBanner complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfBanner">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Banner" type="{GEIS.MultimodalInfoWebService}Banner" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBanner", propOrder = {
    "banner"
})
public class ArrayOfBanner {

    @XmlElement(name = "Banner", nillable = true)
    protected List<Banner> banner;

    /**
     * Gets the value of the banner property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the banner property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBanner().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Banner }
     * 
     * 
     * @return
     *     The value of the banner property.
     */
    public List<Banner> getBanner() {
        if (banner == null) {
            banner = new ArrayList<>();
        }
        return this.banner;
    }

}
