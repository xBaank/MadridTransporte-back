
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
 *         <element name="linesTimePlanning" type="{GEIS.MultimodalInfoWebService}ArrayOfTimePlanning" minOccurs="0"/>
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
    "linesTimePlanning"
})
@XmlRootElement(name = "TimePlanningResponse")
public class TimePlanningResponse {

    protected ArrayOfTimePlanning linesTimePlanning;

    /**
     * Gets the value of the linesTimePlanning property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTimePlanning }
     *     
     */
    public ArrayOfTimePlanning getLinesTimePlanning() {
        return linesTimePlanning;
    }

    /**
     * Sets the value of the linesTimePlanning property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTimePlanning }
     *     
     */
    public void setLinesTimePlanning(ArrayOfTimePlanning value) {
        this.linesTimePlanning = value;
    }

}
