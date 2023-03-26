
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSAEError complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfSAEError">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="SAEError" type="{GEIS.MultimodalInfoWebService}SAEError" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSAEError", propOrder = {
    "saeError"
})
public class ArrayOfSAEError {

    @XmlElement(name = "SAEError", nillable = true)
    protected List<SAEError> saeError;

    /**
     * Gets the value of the saeError property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the saeError property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSAEError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SAEError }
     * 
     * 
     * @return
     *     The value of the saeError property.
     */
    public List<SAEError> getSAEError() {
        if (saeError == null) {
            saeError = new ArrayList<>();
        }
        return this.saeError;
    }

}
