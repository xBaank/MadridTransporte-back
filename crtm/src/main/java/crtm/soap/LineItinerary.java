
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LineItinerary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="LineItinerary">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codItinerary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="direction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="kml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="stops" type="{GEIS.MultimodalInfoWebService}ArrayOfShortStop" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineItinerary", propOrder = {
    "codItinerary",
    "name",
    "direction",
    "kml",
    "stops"
})
public class LineItinerary {

    protected String codItinerary;
    protected String name;
    protected int direction;
    protected String kml;
    protected ArrayOfShortStop stops;

    /**
     * Gets the value of the codItinerary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodItinerary() {
        return codItinerary;
    }

    /**
     * Sets the value of the codItinerary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodItinerary(String value) {
        this.codItinerary = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     */
    public void setDirection(int value) {
        this.direction = value;
    }

    /**
     * Gets the value of the kml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKml() {
        return kml;
    }

    /**
     * Sets the value of the kml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKml(String value) {
        this.kml = value;
    }

    /**
     * Gets the value of the stops property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfShortStop }
     *     
     */
    public ArrayOfShortStop getStops() {
        return stops;
    }

    /**
     * Sets the value of the stops property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfShortStop }
     *     
     */
    public void setStops(ArrayOfShortStop value) {
        this.stops = value;
    }

}
