
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Expedicion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Expedicion">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codModo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="codLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codItinerario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nameItinerary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codExpedicion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombreExpedicion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="idaVuelta" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="tipoExpedicion" type="{GEIS.MultimodalInfoWebService}TipoExpedicion"/>
 *         <element name="codEstacionOrigen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codEstacionDestino" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="horaInicio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="horaFin" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="actividades" type="{GEIS.MultimodalInfoWebService}ArrayOfActividad" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Expedicion", propOrder = {
    "codModo",
    "codLinea",
    "codItinerario",
    "nameItinerary",
    "codExpedicion",
    "nombreExpedicion",
    "idaVuelta",
    "tipoExpedicion",
    "codEstacionOrigen",
    "codEstacionDestino",
    "horaInicio",
    "horaFin",
    "actividades"
})
public class Expedicion {

    protected int codModo;
    protected String codLinea;
    protected String codItinerario;
    protected String nameItinerary;
    protected String codExpedicion;
    protected String nombreExpedicion;
    protected int idaVuelta;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoExpedicion tipoExpedicion;
    protected String codEstacionOrigen;
    protected String codEstacionDestino;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar horaInicio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar horaFin;
    protected ArrayOfActividad actividades;

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
     * Gets the value of the nameItinerary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameItinerary() {
        return nameItinerary;
    }

    /**
     * Sets the value of the nameItinerary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameItinerary(String value) {
        this.nameItinerary = value;
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

    /**
     * Gets the value of the nombreExpedicion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreExpedicion() {
        return nombreExpedicion;
    }

    /**
     * Sets the value of the nombreExpedicion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreExpedicion(String value) {
        this.nombreExpedicion = value;
    }

    /**
     * Gets the value of the idaVuelta property.
     * 
     */
    public int getIdaVuelta() {
        return idaVuelta;
    }

    /**
     * Sets the value of the idaVuelta property.
     * 
     */
    public void setIdaVuelta(int value) {
        this.idaVuelta = value;
    }

    /**
     * Gets the value of the tipoExpedicion property.
     * 
     * @return
     *     possible object is
     *     {@link TipoExpedicion }
     *     
     */
    public TipoExpedicion getTipoExpedicion() {
        return tipoExpedicion;
    }

    /**
     * Sets the value of the tipoExpedicion property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoExpedicion }
     *     
     */
    public void setTipoExpedicion(TipoExpedicion value) {
        this.tipoExpedicion = value;
    }

    /**
     * Gets the value of the codEstacionOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodEstacionOrigen() {
        return codEstacionOrigen;
    }

    /**
     * Sets the value of the codEstacionOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodEstacionOrigen(String value) {
        this.codEstacionOrigen = value;
    }

    /**
     * Gets the value of the codEstacionDestino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodEstacionDestino() {
        return codEstacionDestino;
    }

    /**
     * Sets the value of the codEstacionDestino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodEstacionDestino(String value) {
        this.codEstacionDestino = value;
    }

    /**
     * Gets the value of the horaInicio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHoraInicio() {
        return horaInicio;
    }

    /**
     * Sets the value of the horaInicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHoraInicio(XMLGregorianCalendar value) {
        this.horaInicio = value;
    }

    /**
     * Gets the value of the horaFin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHoraFin() {
        return horaFin;
    }

    /**
     * Sets the value of the horaFin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHoraFin(XMLGregorianCalendar value) {
        this.horaFin = value;
    }

    /**
     * Gets the value of the actividades property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfActividad }
     *     
     */
    public ArrayOfActividad getActividades() {
        return actividades;
    }

    /**
     * Sets the value of the actividades property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfActividad }
     *     
     */
    public void setActividades(ArrayOfActividad value) {
        this.actividades = value;
    }

}
