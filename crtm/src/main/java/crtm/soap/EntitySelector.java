
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntitySelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="EntitySelector">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codItinerary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codExpedition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="stopSequence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntitySelector", propOrder = {
    "codCompany",
    "codLine",
    "codItinerary",
    "codExpedition",
    "codStop",
    "stopSequence"
})
public class EntitySelector {

    protected String codCompany;
    protected String codLine;
    protected String codItinerary;
    protected String codExpedition;
    protected String codStop;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer stopSequence;

    /**
     * Gets the value of the codCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodCompany() {
        return codCompany;
    }

    /**
     * Sets the value of the codCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodCompany(String value) {
        this.codCompany = value;
    }

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
     * Gets the value of the codExpedition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodExpedition() {
        return codExpedition;
    }

    /**
     * Sets the value of the codExpedition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodExpedition(String value) {
        this.codExpedition = value;
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
     * Gets the value of the stopSequence property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStopSequence() {
        return stopSequence;
    }

    /**
     * Sets the value of the stopSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStopSequence(Integer value) {
        this.stopSequence = value;
    }

}
