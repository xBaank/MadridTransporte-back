
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LineStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="LineStatus">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="line" type="{GEIS.MultimodalInfoWebService}ShortLine" minOccurs="0"/>
 *         <element name="SAEStatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineStatus", propOrder = {
    "line",
    "saeStatus"
})
public class LineStatus {

    protected ShortLine line;
    @XmlElement(name = "SAEStatus")
    protected boolean saeStatus;

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link ShortLine }
     *     
     */
    public ShortLine getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShortLine }
     *     
     */
    public void setLine(ShortLine value) {
        this.line = value;
    }

    /**
     * Gets the value of the saeStatus property.
     * 
     */
    public boolean isSAEStatus() {
        return saeStatus;
    }

    /**
     * Sets the value of the saeStatus property.
     * 
     */
    public void setSAEStatus(boolean value) {
        this.saeStatus = value;
    }

}
