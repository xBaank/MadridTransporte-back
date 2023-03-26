
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopTimesDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="StopTimesDate">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="stopCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="StepsStopList" type="{GEIS.MultimodalInfoWebService}ArrayOfStepsStop" minOccurs="0"/>
 *         <element name="SAEsErrors" type="{GEIS.MultimodalInfoWebService}ArrayOfSAEError" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopTimesDate", propOrder = {
    "stopCode",
    "stepsStopList",
    "saEsErrors"
})
public class StopTimesDate {

    protected String stopCode;
    @XmlElement(name = "StepsStopList")
    protected ArrayOfStepsStop stepsStopList;
    @XmlElement(name = "SAEsErrors")
    protected ArrayOfSAEError saEsErrors;

    /**
     * Gets the value of the stopCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStopCode() {
        return stopCode;
    }

    /**
     * Sets the value of the stopCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStopCode(String value) {
        this.stopCode = value;
    }

    /**
     * Gets the value of the stepsStopList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStepsStop }
     *     
     */
    public ArrayOfStepsStop getStepsStopList() {
        return stepsStopList;
    }

    /**
     * Sets the value of the stepsStopList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStepsStop }
     *     
     */
    public void setStepsStopList(ArrayOfStepsStop value) {
        this.stepsStopList = value;
    }

    /**
     * Gets the value of the saEsErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSAEError }
     *     
     */
    public ArrayOfSAEError getSAEsErrors() {
        return saEsErrors;
    }

    /**
     * Sets the value of the saEsErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSAEError }
     *     
     */
    public void setSAEsErrors(ArrayOfSAEError value) {
        this.saEsErrors = value;
    }

}
