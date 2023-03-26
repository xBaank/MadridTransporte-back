
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
 *         <element name="pois" type="{GEIS.MultimodalInfoWebService}ArrayOfPoi" minOccurs="0"/>
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
    "pois"
})
@XmlRootElement(name = "PoiResponse")
public class PoiResponse {

    protected ArrayOfPoi pois;

    /**
     * Gets the value of the pois property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPoi }
     *     
     */
    public ArrayOfPoi getPois() {
        return pois;
    }

    /**
     * Sets the value of the pois property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPoi }
     *     
     */
    public void setPois(ArrayOfPoi value) {
        this.pois = value;
    }

}
