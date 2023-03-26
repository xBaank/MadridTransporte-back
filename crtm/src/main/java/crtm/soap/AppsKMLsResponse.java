
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
 *         <element name="apps" type="{GEIS.MultimodalInfoWebService}ArrayOfApp" minOccurs="0"/>
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
    "apps"
})
@XmlRootElement(name = "AppsKMLsResponse")
public class AppsKMLsResponse {

    protected ArrayOfApp apps;

    /**
     * Gets the value of the apps property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfApp }
     *     
     */
    public ArrayOfApp getApps() {
        return apps;
    }

    /**
     * Sets the value of the apps property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfApp }
     *     
     */
    public void setApps(ArrayOfApp value) {
        this.apps = value;
    }

}
