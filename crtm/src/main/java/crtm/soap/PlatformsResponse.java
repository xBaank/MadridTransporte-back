
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
 *         <element name="platforms" type="{GEIS.MultimodalInfoWebService}ArrayOfPlatform" minOccurs="0"/>
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
    "platforms"
})
@XmlRootElement(name = "PlatformsResponse")
public class PlatformsResponse {

    protected ArrayOfPlatform platforms;

    /**
     * Gets the value of the platforms property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPlatform }
     *     
     */
    public ArrayOfPlatform getPlatforms() {
        return platforms;
    }

    /**
     * Sets the value of the platforms property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPlatform }
     *     
     */
    public void setPlatforms(ArrayOfPlatform value) {
        this.platforms = value;
    }

}
