
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopTime complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StopTime">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="actualDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="stop" type="{GEIS.MultimodalInfoWebService}ShortStop" minOccurs="0"/>
 *         <element name="times" type="{GEIS.MultimodalInfoWebService}ArrayOfTime" minOccurs="0"/>
 *         <element name="linesStatus" type="{GEIS.MultimodalInfoWebService}ArrayOfLineStatus" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopTime", propOrder = {
    "actualDate",
    "stop",
    "times",
    "linesStatus"
})
public class StopTime {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actualDate;
    protected ShortStop stop;
    protected ArrayOfTime times;
    protected ArrayOfLineStatus linesStatus;

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
     * Gets the value of the stop property.
     * 
     * @return
     *     possible object is
     *     {@link ShortStop }
     *     
     */
    public ShortStop getStop() {
        return stop;
    }

    /**
     * Sets the value of the stop property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShortStop }
     *     
     */
    public void setStop(ShortStop value) {
        this.stop = value;
    }

    /**
     * Gets the value of the times property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTime }
     *     
     */
    public ArrayOfTime getTimes() {
        return times;
    }

    /**
     * Sets the value of the times property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTime }
     *     
     */
    public void setTimes(ArrayOfTime value) {
        this.times = value;
    }

    /**
     * Gets the value of the linesStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLineStatus }
     *     
     */
    public ArrayOfLineStatus getLinesStatus() {
        return linesStatus;
    }

    /**
     * Sets the value of the linesStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLineStatus }
     *     
     */
    public void setLinesStatus(ArrayOfLineStatus value) {
        this.linesStatus = value;
    }

}
