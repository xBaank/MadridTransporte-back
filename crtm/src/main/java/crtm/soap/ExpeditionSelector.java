
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExpeditionSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ExpeditionSelector">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codItinerary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codExpedition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpeditionSelector", propOrder = {
    "codCompany",
    "codLine",
    "codItinerary",
    "codService",
    "codExpedition"
})
public class ExpeditionSelector {

    protected String codCompany;
    protected String codLine;
    protected String codItinerary;
    protected String codService;
    protected String codExpedition;

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
     * Gets the value of the codService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodService() {
        return codService;
    }

    /**
     * Sets the value of the codService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodService(String value) {
        this.codService = value;
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

}
