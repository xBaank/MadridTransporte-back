
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimePlanning complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="TimePlanning">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codItinerary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="startService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="endService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="active" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimePlanning", propOrder = {
    "codLine",
    "codItinerary",
    "type",
    "startService",
    "endService",
    "active"
})
public class TimePlanning {

    protected String codLine;
    protected String codItinerary;
    protected String type;
    protected String startService;
    protected String endService;
    protected int active;

    /**
     * Gets the value of the codLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodLine() {
        return codLine;
    }

    /**
     * Sets the value of the codLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodLine(String value) {
        this.codLine = value;
    }

    /**
     * Gets the value of the codItinerary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodItinerary() {
        return codItinerary;
    }

    /**
     * Sets the value of the codItinerary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodItinerary(String value) {
        this.codItinerary = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the startService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartService() {
        return startService;
    }

    /**
     * Sets the value of the startService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartService(String value) {
        this.startService = value;
    }

    /**
     * Gets the value of the endService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndService() {
        return endService;
    }

    /**
     * Sets the value of the endService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndService(String value) {
        this.endService = value;
    }

    /**
     * Gets the value of the active property.
     * 
     */
    public int getActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(int value) {
        this.active = value;
    }

}
