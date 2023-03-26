
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TripUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="TripUpdate">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="updateType" type="{GEIS.MultimodalInfoWebService}UpdateType" minOccurs="0"/>
 *         <element name="expedition" type="{GEIS.MultimodalInfoWebService}ExpeditionSelector" minOccurs="0"/>
 *         <element name="stops" type="{GEIS.MultimodalInfoWebService}ArrayOfStopSelector" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TripUpdate", propOrder = {
    "codMode",
    "creationDate",
    "updateType",
    "expedition",
    "stops"
})
public class TripUpdate {

    protected String codMode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected UpdateType updateType;
    protected ExpeditionSelector expedition;
    protected ArrayOfStopSelector stops;

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
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the updateType property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateType }
     *     
     */
    public UpdateType getUpdateType() {
        return updateType;
    }

    /**
     * Sets the value of the updateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     *     
     */
    public void setUpdateType(UpdateType value) {
        this.updateType = value;
    }

    /**
     * Gets the value of the expedition property.
     * 
     * @return
     *     possible object is
     *     {@link ExpeditionSelector }
     *     
     */
    public ExpeditionSelector getExpedition() {
        return expedition;
    }

    /**
     * Sets the value of the expedition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpeditionSelector }
     *     
     */
    public void setExpedition(ExpeditionSelector value) {
        this.expedition = value;
    }

    /**
     * Gets the value of the stops property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStopSelector }
     *     
     */
    public ArrayOfStopSelector getStops() {
        return stops;
    }

    /**
     * Sets the value of the stops property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStopSelector }
     *     
     */
    public void setStops(ArrayOfStopSelector value) {
        this.stops = value;
    }

}
