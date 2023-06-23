
package crtm.abono;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
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
 *         <element name="sNumeroTP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sLenguaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sTipoApp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sNumeroTP",
    "sLenguaje",
    "sTipoApp"
})
@XmlRootElement(name = "ConsultaSaldoTarjeta1")
public class ConsultaSaldoTarjeta1 {

    @XmlElementRef(name = "sNumeroTP", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> sNumeroTP;
    @XmlElementRef(name = "sLenguaje", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> sLenguaje;
    @XmlElementRef(name = "sTipoApp", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> sTipoApp;

    /**
     * Gets the value of the sNumeroTP property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSNumeroTP() {
        return sNumeroTP;
    }

    /**
     * Sets the value of the sNumeroTP property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSNumeroTP(JAXBElement<String> value) {
        this.sNumeroTP = value;
    }

    /**
     * Gets the value of the sLenguaje property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSLenguaje() {
        return sLenguaje;
    }

    /**
     * Sets the value of the sLenguaje property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSLenguaje(JAXBElement<String> value) {
        this.sLenguaje = value;
    }

    /**
     * Gets the value of the sTipoApp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSTipoApp() {
        return sTipoApp;
    }

    /**
     * Sets the value of the sTipoApp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSTipoApp(JAXBElement<String> value) {
        this.sTipoApp = value;
    }

}
