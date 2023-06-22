
package crtm.abono;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for Delegate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="Delegate">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <any processContents='skip' namespace='' maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <attribute ref="{http://schemas.microsoft.com/2003/10/Serialization/}FactoryType"/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Delegate", namespace = "http://schemas.datacontract.org/2004/07/System", propOrder = {
    "any"
})
@XmlSeeAlso({
    MulticastDelegate.class
})
public class Delegate {

    @XmlAnyElement
    protected List<Element> any;
    @XmlAttribute(name = "FactoryType", namespace = "http://schemas.microsoft.com/2003/10/Serialization/")
    protected QName factoryType;

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * 
     * 
     * @return
     *     The value of the any property.
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Gets the value of the factoryType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getFactoryType() {
        return factoryType;
    }

    /**
     * Sets the value of the factoryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setFactoryType(QName value) {
        this.factoryType = value;
    }

}
