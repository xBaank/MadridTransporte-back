
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCompany complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfCompany">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Company" type="{GEIS.MultimodalInfoWebService}Company" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCompany", propOrder = {
    "company"
})
public class ArrayOfCompany {

    @XmlElement(name = "Company", nillable = true)
    protected List<Company> company;

    /**
     * Gets the value of the company property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the company property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompany().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Company }
     * 
     * 
     * @return
     *     The value of the company property.
     */
    public List<Company> getCompany() {
        if (company == null) {
            company = new ArrayList<>();
        }
        return this.company;
    }

}
