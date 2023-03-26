
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Poi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Poi">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codPoi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codSponsor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="openTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="latitude" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="longitude" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Poi", propOrder = {
    "codPoi",
    "codSponsor",
    "address",
    "openTime",
    "contact",
    "latitude",
    "longitude"
})
public class Poi {

    protected String codPoi;
    protected String codSponsor;
    protected String address;
    protected String openTime;
    protected String contact;
    protected double latitude;
    protected double longitude;

    /**
     * Gets the value of the codPoi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodPoi() {
        return codPoi;
    }

    /**
     * Sets the value of the codPoi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodPoi(String value) {
        this.codPoi = value;
    }

    /**
     * Gets the value of the codSponsor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodSponsor() {
        return codSponsor;
    }

    /**
     * Sets the value of the codSponsor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodSponsor(String value) {
        this.codSponsor = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the openTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * Sets the value of the openTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenTime(String value) {
        this.openTime = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContact(String value) {
        this.contact = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     */
    public void setLatitude(double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     */
    public void setLongitude(double value) {
        this.longitude = value;
    }

}
