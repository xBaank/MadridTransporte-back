
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Actividad complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Actividad">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="hora" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="codEstacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombreEstacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="segundosAnt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="segundosPost" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="tipo_actividad" type="{GEIS.MultimodalInfoWebService}TipoActividad"/>
 *         <element name="tipo_estacion" type="{GEIS.MultimodalInfoWebService}TipoEstacion"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Actividad", propOrder = {
    "hora",
    "codEstacion",
    "nombreEstacion",
    "segundosAnt",
    "segundosPost",
    "tipoActividad",
    "tipoEstacion"
})
public class Actividad {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar hora;
    protected String codEstacion;
    protected String nombreEstacion;
    protected int segundosAnt;
    protected int segundosPost;
    @XmlElement(name = "tipo_actividad", required = true)
    @XmlSchemaType(name = "string")
    protected TipoActividad tipoActividad;
    @XmlElement(name = "tipo_estacion", required = true)
    @XmlSchemaType(name = "string")
    protected TipoEstacion tipoEstacion;

    /**
     * Gets the value of the hora property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHora() {
        return hora;
    }

    /**
     * Sets the value of the hora property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHora(XMLGregorianCalendar value) {
        this.hora = value;
    }

    /**
     * Gets the value of the codEstacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodEstacion() {
        return codEstacion;
    }

    /**
     * Sets the value of the codEstacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodEstacion(String value) {
        this.codEstacion = value;
    }

    /**
     * Gets the value of the nombreEstacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreEstacion() {
        return nombreEstacion;
    }

    /**
     * Sets the value of the nombreEstacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreEstacion(String value) {
        this.nombreEstacion = value;
    }

    /**
     * Gets the value of the segundosAnt property.
     * 
     */
    public int getSegundosAnt() {
        return segundosAnt;
    }

    /**
     * Sets the value of the segundosAnt property.
     * 
     */
    public void setSegundosAnt(int value) {
        this.segundosAnt = value;
    }

    /**
     * Gets the value of the segundosPost property.
     * 
     */
    public int getSegundosPost() {
        return segundosPost;
    }

    /**
     * Sets the value of the segundosPost property.
     * 
     */
    public void setSegundosPost(int value) {
        this.segundosPost = value;
    }

    /**
     * Gets the value of the tipoActividad property.
     * 
     * @return
     *     possible object is
     *     {@link TipoActividad }
     *     
     */
    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    /**
     * Sets the value of the tipoActividad property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoActividad }
     *     
     */
    public void setTipoActividad(TipoActividad value) {
        this.tipoActividad = value;
    }

    /**
     * Gets the value of the tipoEstacion property.
     * 
     * @return
     *     possible object is
     *     {@link TipoEstacion }
     *     
     */
    public TipoEstacion getTipoEstacion() {
        return tipoEstacion;
    }

    /**
     * Sets the value of the tipoEstacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoEstacion }
     *     
     */
    public void setTipoEstacion(TipoEstacion value) {
        this.tipoEstacion = value;
    }

}
