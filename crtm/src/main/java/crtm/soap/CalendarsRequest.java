
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
 *         <element name="authentication" type="{GEIS.MultimodalInfoWebService}AuthHeader" minOccurs="0"/>
 *         <element name="codModo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="codLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codItinerario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codExpedicion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "authentication",
    "codModo",
    "codLinea",
    "codItinerario",
    "codExpedicion"
})
@XmlRootElement(name = "CalendarsRequest")
public class CalendarsRequest {

    protected AuthHeader authentication;
    protected int codModo;
    protected String codLinea;
    protected String codItinerario;
    protected String codExpedicion;

    /**
     * Gets the value of the authentication property.
     * 
     * @return
     *     possible object is
     *     {@link AuthHeader }
     *     
     */
    public AuthHeader getAuthentication() {
        return authentication;
    }

    /**
     * Sets the value of the authentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthHeader }
     *     
     */
    public void setAuthentication(AuthHeader value) {
        this.authentication = value;
    }

    /**
     * Gets the value of the codModo property.
     * 
     */
    public int getCodModo() {
        return codModo;
    }

    /**
     * Sets the value of the codModo property.
     * 
     */
    public void setCodModo(int value) {
        this.codModo = value;
    }

    /**
     * Gets the value of the codLinea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodLinea() {
        return codLinea;
    }

    /**
     * Sets the value of the codLinea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodLinea(String value) {
        this.codLinea = value;
    }

    /**
     * Gets the value of the codItinerario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodItinerario() {
        return codItinerario;
    }

    /**
     * Sets the value of the codItinerario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodItinerario(String value) {
        this.codItinerario = value;
    }

    /**
     * Gets the value of the codExpedicion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodExpedicion() {
        return codExpedicion;
    }

    /**
     * Sets the value of the codExpedicion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodExpedicion(String value) {
        this.codExpedicion = value;
    }

}
