
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
 *         <element name="ConsultaSaldoTarjeta1Result" type="{http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.InformacionTarjetaWS}respuestaInformacionTarjetaConsultaSaldo1" minOccurs="0"/>
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
    "consultaSaldoTarjeta1Result"
})
@XmlRootElement(name = "ConsultaSaldoTarjeta1Response")
public class ConsultaSaldoTarjeta1Response {

    @XmlElementRef(name = "ConsultaSaldoTarjeta1Result", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<RespuestaInformacionTarjetaConsultaSaldo1> consultaSaldoTarjeta1Result;

    /**
     * Gets the value of the consultaSaldoTarjeta1Result property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     *     
     */
    public JAXBElement<RespuestaInformacionTarjetaConsultaSaldo1> getConsultaSaldoTarjeta1Result() {
        return consultaSaldoTarjeta1Result;
    }

    /**
     * Sets the value of the consultaSaldoTarjeta1Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     *     
     */
    public void setConsultaSaldoTarjeta1Result(JAXBElement<RespuestaInformacionTarjetaConsultaSaldo1> value) {
        this.consultaSaldoTarjeta1Result = value;
    }

}
