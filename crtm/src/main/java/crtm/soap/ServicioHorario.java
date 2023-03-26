
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServicioHorario complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ServicioHorario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codServicioHorario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codModo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="nombreServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="tipoServicio" type="{GEIS.MultimodalInfoWebService}TipoServicio"/>
 *         <element name="diaInicio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="diaFin" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="tipoDia" type="{GEIS.MultimodalInfoWebService}TipoDia"/>
 *         <element name="dias" type="{GEIS.MultimodalInfoWebService}ArrayOfString" minOccurs="0"/>
 *         <element name="diasNoValidos" type="{GEIS.MultimodalInfoWebService}ArrayOfDateTime" minOccurs="0"/>
 *         <element name="diasAniadidos" type="{GEIS.MultimodalInfoWebService}ArrayOfDateTime" minOccurs="0"/>
 *         <element name="expediciones" type="{GEIS.MultimodalInfoWebService}ArrayOfString" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServicioHorario", propOrder = {
    "codServicioHorario",
    "codModo",
    "nombreServicio",
    "tipoServicio",
    "diaInicio",
    "diaFin",
    "tipoDia",
    "dias",
    "diasNoValidos",
    "diasAniadidos",
    "expediciones"
})
public class ServicioHorario {

    protected String codServicioHorario;
    protected int codModo;
    protected String nombreServicio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoServicio tipoServicio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar diaInicio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar diaFin;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoDia tipoDia;
    protected ArrayOfString dias;
    protected ArrayOfDateTime diasNoValidos;
    protected ArrayOfDateTime diasAniadidos;
    protected ArrayOfString expediciones;

    /**
     * Gets the value of the codServicioHorario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodServicioHorario() {
        return codServicioHorario;
    }

    /**
     * Sets the value of the codServicioHorario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodServicioHorario(String value) {
        this.codServicioHorario = value;
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
     * Gets the value of the nombreServicio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreServicio() {
        return nombreServicio;
    }

    /**
     * Sets the value of the nombreServicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreServicio(String value) {
        this.nombreServicio = value;
    }

    /**
     * Gets the value of the tipoServicio property.
     * 
     * @return
     *     possible object is
     *     {@link TipoServicio }
     *     
     */
    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    /**
     * Sets the value of the tipoServicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoServicio }
     *     
     */
    public void setTipoServicio(TipoServicio value) {
        this.tipoServicio = value;
    }

    /**
     * Gets the value of the diaInicio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDiaInicio() {
        return diaInicio;
    }

    /**
     * Sets the value of the diaInicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDiaInicio(XMLGregorianCalendar value) {
        this.diaInicio = value;
    }

    /**
     * Gets the value of the diaFin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDiaFin() {
        return diaFin;
    }

    /**
     * Sets the value of the diaFin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDiaFin(XMLGregorianCalendar value) {
        this.diaFin = value;
    }

    /**
     * Gets the value of the tipoDia property.
     * 
     * @return
     *     possible object is
     *     {@link TipoDia }
     *     
     */
    public TipoDia getTipoDia() {
        return tipoDia;
    }

    /**
     * Sets the value of the tipoDia property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoDia }
     *     
     */
    public void setTipoDia(TipoDia value) {
        this.tipoDia = value;
    }

    /**
     * Gets the value of the dias property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getDias() {
        return dias;
    }

    /**
     * Sets the value of the dias property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setDias(ArrayOfString value) {
        this.dias = value;
    }

    /**
     * Gets the value of the diasNoValidos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDateTime }
     *     
     */
    public ArrayOfDateTime getDiasNoValidos() {
        return diasNoValidos;
    }

    /**
     * Sets the value of the diasNoValidos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDateTime }
     *     
     */
    public void setDiasNoValidos(ArrayOfDateTime value) {
        this.diasNoValidos = value;
    }

    /**
     * Gets the value of the diasAniadidos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDateTime }
     *     
     */
    public ArrayOfDateTime getDiasAniadidos() {
        return diasAniadidos;
    }

    /**
     * Sets the value of the diasAniadidos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDateTime }
     *     
     */
    public void setDiasAniadidos(ArrayOfDateTime value) {
        this.diasAniadidos = value;
    }

    /**
     * Gets the value of the expediciones property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getExpediciones() {
        return expediciones;
    }

    /**
     * Sets the value of the expediciones property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setExpediciones(ArrayOfString value) {
        this.expediciones = value;
    }

}
