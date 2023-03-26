
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Stop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Stop">
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
 *         <element name="codLines" type="{GEIS.MultimodalInfoWebService}ArrayOfString1" minOccurs="0"/>
 *         <element name="access" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="park" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="nightLinesService" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="stopZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="stopType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="parentStation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stop", propOrder = {
    "codStop",
    "shortCodStop",
    "codMode",
    "name",
    "address",
    "postCode",
    "codMunicipality",
    "coordinates",
    "codLines",
    "access",
    "park",
    "nightLinesService",
    "stopZone",
    "stopType",
    "parentStation"
})
public class Stop {

    protected String codStop;
    protected String shortCodStop;
    protected String codMode;
    protected String name;
    protected String address;
    protected String postCode;
    protected String codMunicipality;
    protected Coordinates coordinates;
    protected ArrayOfString1 codLines;
    protected int access;
    protected int park;
    protected int nightLinesService;
    protected String stopZone;
    protected int stopType;
    protected String parentStation;

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
     * Gets the value of the codLines property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString1 }
     *     
     */
    public ArrayOfString1 getCodLines() {
        return codLines;
    }

    /**
     * Sets the value of the codLines property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString1 }
     *     
     */
    public void setCodLines(ArrayOfString1 value) {
        this.codLines = value;
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

    /**
     * Gets the value of the stopZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopZone() {
        return stopZone;
    }

    /**
     * Sets the value of the stopZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopZone(String value) {
        this.stopZone = value;
    }

    /**
     * Gets the value of the stopType property.
     * 
     */
    public int getStopType() {
        return stopType;
    }

    /**
     * Sets the value of the stopType property.
     * 
     */
    public void setStopType(int value) {
        this.stopType = value;
    }

    /**
     * Gets the value of the parentStation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentStation() {
        return parentStation;
    }

    /**
     * Sets the value of the parentStation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentStation(String value) {
        this.parentStation = value;
    }

}
