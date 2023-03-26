
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IncidentAffectation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="IncidentAffectation">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="eng_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="stopsAffectated" type="{GEIS.MultimodalInfoWebService}ArrayOfShortStop" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IncidentAffectation", propOrder = {
    "codMode",
    "codLine",
    "type",
    "description",
    "engDescription",
    "stopsAffectated"
})
public class IncidentAffectation {

    protected String codMode;
    protected String codLine;
    protected String type;
    protected String description;
    @XmlElement(name = "eng_description")
    protected String engDescription;
    protected ArrayOfShortStop stopsAffectated;

    /**
     * Gets the value of the codMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMode() {
        return codMode;
    }

    /**
     * Sets the value of the codMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMode(String value) {
        this.codMode = value;
    }

    /**
     * Gets the value of the codLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodLine() {
        return codLine;
    }

    /**
     * Sets the value of the codLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodLine(String value) {
        this.codLine = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the engDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngDescription() {
        return engDescription;
    }

    /**
     * Sets the value of the engDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngDescription(String value) {
        this.engDescription = value;
    }

    /**
     * Gets the value of the stopsAffectated property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfShortStop }
     *     
     */
    public ArrayOfShortStop getStopsAffectated() {
        return stopsAffectated;
    }

    /**
     * Sets the value of the stopsAffectated property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfShortStop }
     *     
     */
    public void setStopsAffectated(ArrayOfShortStop value) {
        this.stopsAffectated = value;
    }

}
