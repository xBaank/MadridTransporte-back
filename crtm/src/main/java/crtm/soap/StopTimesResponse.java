
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         <element name="stopTimes" type="{GEIS.MultimodalInfoWebService}StopTime" minOccurs="0"/>
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
    "stopTimes"
})
@XmlRootElement(name = "StopTimesResponse")
public class StopTimesResponse {

    protected StopTime stopTimes;

    /**
     * Gets the value of the stopTimes property.
     * 
     * @return
     *     possible object is
     *     {@link StopTime }
     *     
     */
    public StopTime getStopTimes() {
        return stopTimes;
    }

    /**
     * Sets the value of the stopTimes property.
     * 
     * @param value
     *     allowed object is
     *     {@link StopTime }
     *     
     */
    public void setStopTimes(StopTime value) {
        this.stopTimes = value;
    }

}
