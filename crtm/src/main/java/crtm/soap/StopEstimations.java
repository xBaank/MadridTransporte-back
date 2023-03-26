
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopEstimations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StopEstimations">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="actualDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="stopCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="stopName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="timesInfo" type="{GEIS.MultimodalInfoWebService}ArrayOfTimeInfo" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopEstimations", propOrder = {
    "actualDate",
    "stopCode",
    "stopName",
    "timesInfo"
})
public class StopEstimations {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actualDate;
    protected String stopCode;
    protected String stopName;
    protected ArrayOfTimeInfo timesInfo;

    /**
     * Gets the value of the actualDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getActualDate() {
        return actualDate;
    }

    /**
     * Sets the value of the actualDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setActualDate(XMLGregorianCalendar value) {
        this.actualDate = value;
    }

    /**
     * Gets the value of the stopCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopCode() {
        return stopCode;
    }

    /**
     * Sets the value of the stopCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopCode(String value) {
        this.stopCode = value;
    }

    /**
     * Gets the value of the stopName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopName() {
        return stopName;
    }

    /**
     * Sets the value of the stopName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopName(String value) {
        this.stopName = value;
    }

    /**
     * Gets the value of the timesInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTimeInfo }
     *     
     */
    public ArrayOfTimeInfo getTimesInfo() {
        return timesInfo;
    }

    /**
     * Sets the value of the timesInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTimeInfo }
     *     
     */
    public void setTimesInfo(ArrayOfTimeInfo value) {
        this.timesInfo = value;
    }

}
