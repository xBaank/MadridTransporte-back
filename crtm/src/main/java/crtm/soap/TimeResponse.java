
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
 *         <element name="actualTime" type="{GEIS.MultimodalInfoWebService}ActualTime" minOccurs="0"/>
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
    "actualTime"
})
@XmlRootElement(name = "TimeResponse")
public class TimeResponse {

    protected ActualTime actualTime;

    /**
     * Gets the value of the actualTime property.
     * 
     * @return
     *     possible object is
     *     {@link ActualTime }
     *     
     */
    public ActualTime getActualTime() {
        return actualTime;
    }

    /**
     * Sets the value of the actualTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActualTime }
     *     
     */
    public void setActualTime(ActualTime value) {
        this.actualTime = value;
    }

}
