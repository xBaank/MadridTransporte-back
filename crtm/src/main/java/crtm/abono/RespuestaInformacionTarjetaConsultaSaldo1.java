
package crtm.abono;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for respuestaInformacionTarjetaConsultaSaldo1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="respuestaInformacionTarjetaConsultaSaldo1">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="PropertyChanged" type="{http://schemas.datacontract.org/2004/07/System.ComponentModel}PropertyChangedEventHandler"/>
 *         <element name="iCallIdField" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="iCallIdFieldSpecified" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="iCallLogField" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="iResultCodeField" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         <element name="iResultCodeFieldSpecified" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="sResulXMLField" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="sResulXMLSchemaVersionField" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaInformacionTarjetaConsultaSaldo1", namespace = "http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.InformacionTarjetaWS", propOrder = {
    "propertyChanged",
    "iCallIdField",
    "iCallIdFieldSpecified",
    "iCallLogField",
    "iResultCodeField",
    "iResultCodeFieldSpecified",
    "sResulXMLField",
    "sResulXMLSchemaVersionField"
})
public class RespuestaInformacionTarjetaConsultaSaldo1 {

    @XmlElement(name = "PropertyChanged", required = true, nillable = true)
    protected PropertyChangedEventHandler propertyChanged;
    protected long iCallIdField;
    protected boolean iCallIdFieldSpecified;
    @XmlElement(required = true, nillable = true)
    protected String iCallLogField;
    protected short iResultCodeField;
    protected boolean iResultCodeFieldSpecified;
    @XmlElement(required = true, nillable = true)
    protected String sResulXMLField;
    @XmlElement(required = true, nillable = true)
    protected String sResulXMLSchemaVersionField;

    /**
     * Gets the value of the propertyChanged property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyChangedEventHandler }
     *     
     */
    public PropertyChangedEventHandler getPropertyChanged() {
        return propertyChanged;
    }

    /**
     * Sets the value of the propertyChanged property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyChangedEventHandler }
     *     
     */
    public void setPropertyChanged(PropertyChangedEventHandler value) {
        this.propertyChanged = value;
    }

    /**
     * Gets the value of the iCallIdField property.
     * 
     */
    public long getICallIdField() {
        return iCallIdField;
    }

    /**
     * Sets the value of the iCallIdField property.
     * 
     */
    public void setICallIdField(long value) {
        this.iCallIdField = value;
    }

    /**
     * Gets the value of the iCallIdFieldSpecified property.
     * 
     */
    public boolean isICallIdFieldSpecified() {
        return iCallIdFieldSpecified;
    }

    /**
     * Sets the value of the iCallIdFieldSpecified property.
     * 
     */
    public void setICallIdFieldSpecified(boolean value) {
        this.iCallIdFieldSpecified = value;
    }

    /**
     * Gets the value of the iCallLogField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getICallLogField() {
        return iCallLogField;
    }

    /**
     * Sets the value of the iCallLogField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setICallLogField(String value) {
        this.iCallLogField = value;
    }

    /**
     * Gets the value of the iResultCodeField property.
     * 
     */
    public short getIResultCodeField() {
        return iResultCodeField;
    }

    /**
     * Sets the value of the iResultCodeField property.
     * 
     */
    public void setIResultCodeField(short value) {
        this.iResultCodeField = value;
    }

    /**
     * Gets the value of the iResultCodeFieldSpecified property.
     * 
     */
    public boolean isIResultCodeFieldSpecified() {
        return iResultCodeFieldSpecified;
    }

    /**
     * Sets the value of the iResultCodeFieldSpecified property.
     * 
     */
    public void setIResultCodeFieldSpecified(boolean value) {
        this.iResultCodeFieldSpecified = value;
    }

    /**
     * Gets the value of the sResulXMLField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSResulXMLField() {
        return sResulXMLField;
    }

    /**
     * Sets the value of the sResulXMLField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSResulXMLField(String value) {
        this.sResulXMLField = value;
    }

    /**
     * Gets the value of the sResulXMLSchemaVersionField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSResulXMLSchemaVersionField() {
        return sResulXMLSchemaVersionField;
    }

    /**
     * Sets the value of the sResulXMLSchemaVersionField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSResulXMLSchemaVersionField(String value) {
        this.sResulXMLSchemaVersionField = value;
    }

}
