
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StepsStop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StepsStop">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="vehicleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="StepsTimesStopList" type="{GEIS.MultimodalInfoWebService}ArrayOfStepsTimesStop" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StepsStop", propOrder = {
    "vehicleCode",
    "stepsTimesStopList"
})
public class StepsStop {

    protected String vehicleCode;
    @XmlElement(name = "StepsTimesStopList")
    protected ArrayOfStepsTimesStop stepsTimesStopList;

    /**
     * Gets the value of the vehicleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleCode() {
        return vehicleCode;
    }

    /**
     * Sets the value of the vehicleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleCode(String value) {
        this.vehicleCode = value;
    }

    /**
     * Gets the value of the stepsTimesStopList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStepsTimesStop }
     *     
     */
    public ArrayOfStepsTimesStop getStepsTimesStopList() {
        return stepsTimesStopList;
    }

    /**
     * Sets the value of the stepsTimesStopList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStepsTimesStop }
     *     
     */
    public void setStepsTimesStopList(ArrayOfStepsTimesStop value) {
        this.stepsTimesStopList = value;
    }

}
