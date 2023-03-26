
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Time complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Time">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="line" type="{GEIS.MultimodalInfoWebService}Line" minOccurs="0"/>
 *         <element name="direction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="destination" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="destinationStop" type="{GEIS.MultimodalInfoWebService}ShortStop" minOccurs="0"/>
 *         <element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="codVehicle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time", propOrder = {
    "line",
    "direction",
    "destination",
    "destinationStop",
    "time",
    "codVehicle",
    "codIssue"
})
public class Time {

    protected Line line;
    protected int direction;
    protected String destination;
    protected ShortStop destinationStop;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;
    protected String codVehicle;
    protected String codIssue;

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link Line }
     *     
     */
    public Line getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link Line }
     *     
     */
    public void setLine(Line value) {
        this.line = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     */
    public void setDirection(int value) {
        this.direction = value;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestination(String value) {
        this.destination = value;
    }

    /**
     * Gets the value of the destinationStop property.
     * 
     * @return
     *     possible object is
     *     {@link ShortStop }
     *     
     */
    public ShortStop getDestinationStop() {
        return destinationStop;
    }

    /**
     * Sets the value of the destinationStop property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShortStop }
     *     
     */
    public void setDestinationStop(ShortStop value) {
        this.destinationStop = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the codVehicle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodVehicle() {
        return codVehicle;
    }

    /**
     * Sets the value of the codVehicle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodVehicle(String value) {
        this.codVehicle = value;
    }

    /**
     * Gets the value of the codIssue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodIssue() {
        return codIssue;
    }

    /**
     * Sets the value of the codIssue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodIssue(String value) {
        this.codIssue = value;
    }

}
