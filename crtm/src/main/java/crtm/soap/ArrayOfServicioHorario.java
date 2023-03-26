
package crtm.soap;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfServicioHorario complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ArrayOfServicioHorario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ServicioHorario" type="{GEIS.MultimodalInfoWebService}ServicioHorario" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfServicioHorario", propOrder = {
    "servicioHorario"
})
public class ArrayOfServicioHorario {

    @XmlElement(name = "ServicioHorario", nillable = true)
    protected List<ServicioHorario> servicioHorario;

    /**
     * Gets the value of the servicioHorario property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the servicioHorario property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicioHorario().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServicioHorario }
     * 
     * 
     * @return
     *     The value of the servicioHorario property.
     */
    public List<ServicioHorario> getServicioHorario() {
        if (servicioHorario == null) {
            servicioHorario = new ArrayList<>();
        }
        return this.servicioHorario;
    }

}
