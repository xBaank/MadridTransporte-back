
package crtm.abono;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MulticastDelegate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="MulticastDelegate">
 *   <complexContent>
 *     <extension base="{http://schemas.datacontract.org/2004/07/System}Delegate">
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MulticastDelegate", namespace = "http://schemas.datacontract.org/2004/07/System")
@XmlSeeAlso({
    PropertyChangedEventHandler.class
})
public class MulticastDelegate
    extends Delegate
{


}
