
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
 *         <element name="StopsEstimations" type="{GEIS.MultimodalInfoWebService}ArrayOfStopEstimations" minOccurs="0"/>
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
    "stopsEstimations"
})
@XmlRootElement(name = "EstimationsResponse")
public class EstimationsResponse {

    @XmlElement(name = "StopsEstimations")
    protected ArrayOfStopEstimations stopsEstimations;

    /**
     * Gets the value of the stopsEstimations property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStopEstimations }
     *     
     */
    public ArrayOfStopEstimations getStopsEstimations() {
        return stopsEstimations;
    }

    /**
     * Sets the value of the stopsEstimations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStopEstimations }
     *     
     */
    public void setStopsEstimations(ArrayOfStopEstimations value) {
        this.stopsEstimations = value;
    }

}
