
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="StopTimesDateList" type="{GEIS.MultimodalInfoWebService}ArrayOfStopTimesDate" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stopTimesDateList"
})
@XmlRootElement(name = "StopTimesDateResponse")
public class StopTimesDateResponse {

    @XmlElement(name = "StopTimesDateList")
    protected ArrayOfStopTimesDate stopTimesDateList;

    /**
     * Gets the value of the stopTimesDateList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStopTimesDate }
     *     
     */
    public ArrayOfStopTimesDate getStopTimesDateList() {
        return stopTimesDateList;
    }

    /**
     * Sets the value of the stopTimesDateList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStopTimesDate }
     *     
     */
    public void setStopTimesDateList(ArrayOfStopTimesDate value) {
        this.stopTimesDateList = value;
    }

}
