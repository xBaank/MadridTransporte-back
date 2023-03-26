
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="sponsors" type="{GEIS.MultimodalInfoWebService}ArrayOfSponsor" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sponsors"
})
@XmlRootElement(name = "SponsorResponse")
public class SponsorResponse {

    protected ArrayOfSponsor sponsors;

    /**
     * Gets the value of the sponsors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSponsor }
     *     
     */
    public ArrayOfSponsor getSponsors() {
        return sponsors;
    }

    /**
     * Sets the value of the sponsors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSponsor }
     *     
     */
    public void setSponsors(ArrayOfSponsor value) {
        this.sponsors = value;
    }

}
