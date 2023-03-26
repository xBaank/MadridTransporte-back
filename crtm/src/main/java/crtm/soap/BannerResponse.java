
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
 *         <element name="banner" type="{GEIS.MultimodalInfoWebService}BannerInfo" minOccurs="0"/>
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
    "banner"
})
@XmlRootElement(name = "BannerResponse")
public class BannerResponse {

    protected BannerInfo banner;

    /**
     * Gets the value of the banner property.
     * 
     * @return
     *     possible object is
     *     {@link BannerInfo }
     *     
     */
    public BannerInfo getBanner() {
        return banner;
    }

    /**
     * Sets the value of the banner property.
     * 
     * @param value
     *     allowed object is
     *     {@link BannerInfo }
     *     
     */
    public void setBanner(BannerInfo value) {
        this.banner = value;
    }

}
