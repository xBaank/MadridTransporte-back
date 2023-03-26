
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StopSelector">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nameStop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="arrival" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="departure" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopSelector", propOrder = {
    "codStop",
    "nameStop",
    "sequenceNumber",
    "arrival",
    "departure"
})
public class StopSelector {

    protected String codStop;
    protected String nameStop;
    protected int sequenceNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arrival;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar departure;

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
     * Gets the value of the nameStop property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameStop() {
        return nameStop;
    }

    /**
     * Sets the value of the nameStop property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameStop(String value) {
        this.nameStop = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     */
    public void setSequenceNumber(int value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the arrival property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArrival() {
        return arrival;
    }

    /**
     * Sets the value of the arrival property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArrival(XMLGregorianCalendar value) {
        this.arrival = value;
    }

    /**
     * Gets the value of the departure property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeparture() {
        return departure;
    }

    /**
     * Sets the value of the departure property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeparture(XMLGregorianCalendar value) {
        this.departure = value;
    }

}
