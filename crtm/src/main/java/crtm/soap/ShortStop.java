
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShortStop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ShortStop">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="shortCodStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "ShortStop", propOrder = {
    "codMode",
    "codStop",
    "shortCodStop",
    "name",
    "park",
    "nightLinesService"
})
public class ShortStop {

    protected String codMode;
    protected String codStop;
    protected String shortCodStop;
    protected String name;
    protected int park;
    protected int nightLinesService;

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
