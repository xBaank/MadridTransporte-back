
package crtm.abono;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyChangedEventHandler complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="PropertyChangedEventHandler">
 *   <complexContent>
 *     <extension base="{http://schemas.datacontract.org/2004/07/System}MulticastDelegate">
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyChangedEventHandler", namespace = "http://schemas.datacontract.org/2004/07/System.ComponentModel")
public class PropertyChangedEventHandler
    extends MulticastDelegate
{


}
