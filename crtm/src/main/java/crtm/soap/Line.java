
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Line complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Line">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="shortDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="updateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="updateKmlDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="nightService" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="shortItinerary" type="{GEIS.MultimodalInfoWebService}ArrayOfItinerary" minOccurs="0"/>
 *         <element name="URLLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="colorLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="text_colorLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Line", propOrder = {
    "codLine",
    "shortDescription",
    "description",
    "codMode",
    "updateDate",
    "updateKmlDate",
    "nightService",
    "active",
    "shortItinerary",
    "urlLine",
    "colorLine",
    "textColorLine",
    "companyCode"
})
public class Line {

    protected String codLine;
    protected String shortDescription;
    protected String description;
    protected String codMode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updateDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updateKmlDate;
    protected int nightService;
    protected boolean active;
    protected ArrayOfItinerary shortItinerary;
    @XmlElement(name = "URLLine")
    protected String urlLine;
    protected String colorLine;
    @XmlElement(name = "text_colorLine")
    protected String textColorLine;
    protected String companyCode;

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
     * Gets the value of the shortDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the value of the shortDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortDescription(String value) {
        this.shortDescription = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the updateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the value of the updateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdateDate(XMLGregorianCalendar value) {
        this.updateDate = value;
    }

    /**
     * Gets the value of the updateKmlDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdateKmlDate() {
        return updateKmlDate;
    }

    /**
     * Sets the value of the updateKmlDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdateKmlDate(XMLGregorianCalendar value) {
        this.updateKmlDate = value;
    }

    /**
     * Gets the value of the nightService property.
     * 
     */
    public int getNightService() {
        return nightService;
    }

    /**
     * Sets the value of the nightService property.
     * 
     */
    public void setNightService(int value) {
        this.nightService = value;
    }

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the shortItinerary property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfItinerary }
     *     
     */
    public ArrayOfItinerary getShortItinerary() {
        return shortItinerary;
    }

    /**
     * Sets the value of the shortItinerary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfItinerary }
     *     
     */
    public void setShortItinerary(ArrayOfItinerary value) {
        this.shortItinerary = value;
    }

    /**
     * Gets the value of the urlLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURLLine() {
        return urlLine;
    }

    /**
     * Sets the value of the urlLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURLLine(String value) {
        this.urlLine = value;
    }

    /**
     * Gets the value of the colorLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorLine() {
        return colorLine;
    }

    /**
     * Sets the value of the colorLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorLine(String value) {
        this.colorLine = value;
    }

    /**
     * Gets the value of the textColorLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextColorLine() {
        return textColorLine;
    }

    /**
     * Sets the value of the textColorLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextColorLine(String value) {
        this.textColorLine = value;
    }

    /**
     * Gets the value of the companyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * Sets the value of the companyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyCode(String value) {
        this.companyCode = value;
    }

}
