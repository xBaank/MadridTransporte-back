
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
 *         <element name="incidentsAffectations" type="{GEIS.MultimodalInfoWebService}ArrayOfIncidentAffectation" minOccurs="0"/>
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
    "incidentsAffectations"
})
@XmlRootElement(name = "IncidentsAffectationsResponse")
public class IncidentsAffectationsResponse {

    protected ArrayOfIncidentAffectation incidentsAffectations;

    /**
     * Gets the value of the incidentsAffectations property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfIncidentAffectation }
     *     
     */
    public ArrayOfIncidentAffectation getIncidentsAffectations() {
        return incidentsAffectations;
    }

    /**
     * Sets the value of the incidentsAffectations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfIncidentAffectation }
     *     
     */
    public void setIncidentsAffectations(ArrayOfIncidentAffectation value) {
        this.incidentsAffectations = value;
    }

}
