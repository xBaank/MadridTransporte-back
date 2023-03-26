
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StopInformation">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="shortCodStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="postCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codMunicipality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="coordinates" type="{GEIS.MultimodalInfoWebService}Coordinates" minOccurs="0"/>
 *         <element name="lines" type="{GEIS.MultimodalInfoWebService}ArrayOfLine" minOccurs="0"/>
 *         <element name="access" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="park" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="nightLinesService" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopInformation", propOrder = {
    "codStop",
    "shortCodStop",
    "codMode",
    "name",
    "address",
    "postCode",
    "codMunicipality",
    "coordinates",
    "lines",
    "access",
    "park",
    "nightLinesService"
})
public class StopInformation {

    protected String codStop;
    protected String shortCodStop;
    protected String codMode;
    protected String name;
    protected String address;
    protected String postCode;
    protected String codMunicipality;
    protected Coordinates coordinates;
    protected ArrayOfLine lines;
    protected int access;
    protected int park;
    protected int nightLinesService;

    /**
     * Gets the value of the codStop property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodStop() {
        return codStop;
    }

    /**
     * Sets the value of the codStop property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodStop(String value) {
        this.codStop = value;
    }

    /**
     * Gets the value of the shortCodStop property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortCodStop() {
        return shortCodStop;
    }

    /**
     * Sets the value of the shortCodStop property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortCodStop(String value) {
        this.shortCodStop = value;
    }

    /**
     * Gets the value of the codMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMode() {
        return codMode;
    }

    /**
     * Sets the value of the codMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMode(String value) {
        this.codMode = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the codMunicipality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMunicipality() {
        return codMunicipality;
    }

    /**
     * Sets the value of the codMunicipality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMunicipality(String value) {
        this.codMunicipality = value;
    }

    /**
     * Gets the value of the coordinates property.
     * 
     * @return
     *     possible object is
     *     {@link Coordinates }
     *     
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coordinates }
     *     
     */
    public void setCoordinates(Coordinates value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the lines property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLine }
     *     
     */
    public ArrayOfLine getLines() {
        return lines;
    }

    /**
     * Sets the value of the lines property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLine }
     *     
     */
    public void setLines(ArrayOfLine value) {
        this.lines = value;
    }

    /**
     * Gets the value of the access property.
     * 
     */
    public int getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     * 
     */
    public void setAccess(int value) {
        this.access = value;
    }

    /**
     * Gets the value of the park property.
     * 
     */
    public int getPark() {
        return park;
    }

    /**
     * Sets the value of the park property.
     * 
     */
    public void setPark(int value) {
        this.park = value;
    }

    /**
     * Gets the value of the nightLinesService property.
     * 
     */
    public int getNightLinesService() {
        return nightLinesService;
    }

    /**
     * Sets the value of the nightLinesService property.
     * 
     */
    public void setNightLinesService(int value) {
        this.nightLinesService = value;
    }

}
