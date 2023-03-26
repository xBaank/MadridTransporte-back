
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StepsTimesStop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StepsTimesStop">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="lineCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="destStopCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="itineraryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="observations" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="issueCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="issueCodeOp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StepsTimesStop", propOrder = {
    "lineCode",
    "destStopCode",
    "itineraryCode",
    "observations",
    "issueCode",
    "issueCodeOp",
    "time"
})
public class StepsTimesStop {

    protected String lineCode;
    protected String destStopCode;
    protected String itineraryCode;
    protected String observations;
    protected String issueCode;
    protected String issueCodeOp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;

    /**
     * Gets the value of the lineCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineCode() {
        return lineCode;
    }

    /**
     * Sets the value of the lineCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineCode(String value) {
        this.lineCode = value;
    }

    /**
     * Gets the value of the destStopCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestStopCode() {
        return destStopCode;
    }

    /**
     * Sets the value of the destStopCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestStopCode(String value) {
        this.destStopCode = value;
    }

    /**
     * Gets the value of the itineraryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItineraryCode() {
        return itineraryCode;
    }

    /**
     * Sets the value of the itineraryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItineraryCode(String value) {
        this.itineraryCode = value;
    }

    /**
     * Gets the value of the observations property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservations() {
        return observations;
    }

    /**
     * Sets the value of the observations property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservations(String value) {
        this.observations = value;
    }

    /**
     * Gets the value of the issueCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssueCode() {
        return issueCode;
    }

    /**
     * Sets the value of the issueCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssueCode(String value) {
        this.issueCode = value;
    }

    /**
     * Gets the value of the issueCodeOp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssueCodeOp() {
        return issueCodeOp;
    }

    /**
     * Sets the value of the issueCodeOp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssueCodeOp(String value) {
        this.issueCodeOp = value;
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

}
