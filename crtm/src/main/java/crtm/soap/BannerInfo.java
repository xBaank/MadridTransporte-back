
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BannerInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="BannerInfo">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="banners" type="{GEIS.MultimodalInfoWebService}ArrayOfBanner" minOccurs="0"/>
 *         <element name="max" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BannerInfo", propOrder = {
    "banners",
    "max"
})
public class BannerInfo {

    protected ArrayOfBanner banners;
    protected int max;

    /**
     * Gets the value of the banners property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBanner }
     *     
     */
    public ArrayOfBanner getBanners() {
        return banners;
    }

    /**
     * Sets the value of the banners property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBanner }
     *     
     */
    public void setBanners(ArrayOfBanner value) {
        this.banners = value;
    }

    /**
     * Gets the value of the max property.
     * 
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     */
    public void setMax(int value) {
        this.max = value;
    }

}
