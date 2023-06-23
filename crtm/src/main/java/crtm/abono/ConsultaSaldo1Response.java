
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
 *         <element name="ConsultaSaldo1Result" type="{http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.VentaPrepagoTituloWS}respuestaVentaPrepagoTituloConsultaSaldo1" minOccurs="0"/>
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
    "consultaSaldo1Result"
})
@XmlRootElement(name = "ConsultaSaldo1Response")
public class ConsultaSaldo1Response {

    @XmlElementRef(name = "ConsultaSaldo1Result", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<RespuestaVentaPrepagoTituloConsultaSaldo1> consultaSaldo1Result;

    /**
     * Gets the value of the consultaSaldo1Result property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     *     
     */
    public JAXBElement<RespuestaVentaPrepagoTituloConsultaSaldo1> getConsultaSaldo1Result() {
        return consultaSaldo1Result;
    }

    /**
     * Sets the value of the consultaSaldo1Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     *     
     */
    public void setConsultaSaldo1Result(JAXBElement<RespuestaVentaPrepagoTituloConsultaSaldo1> value) {
        this.consultaSaldo1Result = value;
    }

}
