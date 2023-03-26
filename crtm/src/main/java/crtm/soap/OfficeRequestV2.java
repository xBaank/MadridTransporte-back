
package crtm.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="authentication" type="{GEIS.MultimodalInfoWebService}AuthHeader" minOccurs="0"/>
 *         <element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="filterOffices" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="filterPoints" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="postCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codMunicipality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "authentication",
    "type",
    "filterOffices",
    "filterPoints",
    "postCode",
    "codMunicipality"
})
@XmlRootElement(name = "OfficeRequestV2")
public class OfficeRequestV2 {

    protected AuthHeader authentication;
    protected String type;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer filterOffices;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer filterPoints;
    @XmlElement(required = true, nillable = true)
    protected String postCode;
    protected String codMunicipality;

    /**
     * Gets the value of the authentication property.
     * 
     * @return
     *     possible object is
     *     {@link AuthHeader }
     *     
     */
    public AuthHeader getAuthentication() {
        return authentication;
    }

    /**
     * Sets the value of the authentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthHeader }
     *     
     */
    public void setAuthentication(AuthHeader value) {
        this.authentication = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the filterOffices property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFilterOffices() {
        return filterOffices;
    }

    /**
     * Sets the value of the filterOffices property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFilterOffices(Integer value) {
        this.filterOffices = value;
    }

    /**
     * Gets the value of the filterPoints property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFilterPoints() {
        return filterPoints;
    }

    /**
     * Sets the value of the filterPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFilterPoints(Integer value) {
        this.filterPoints = value;
    }

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the codMunicipality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMunicipality() {
        return codMunicipality;
    }

    /**
     * Sets the value of the codMunicipality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMunicipality(String value) {
        this.codMunicipality = value;
    }

}
