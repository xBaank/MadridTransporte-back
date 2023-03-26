
package crtm.soap;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KML complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="KML">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="identifier" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="name" type="{GEIS.MultimodalInfoWebService}ArrayOfLanguageName" minOccurs="0"/>
 *         <element name="description" type="{GEIS.MultimodalInfoWebService}ArrayOfLanguageName" minOccurs="0"/>
 *         <element name="URL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="URLIcon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="updateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KML", propOrder = {
    "identifier",
    "name",
    "description",
    "url",
    "urlIcon",
    "updateDate"
})
public class KML {

    protected int identifier;
    protected ArrayOfLanguageName name;
    protected ArrayOfLanguageName description;
    @XmlElement(name = "URL")
    protected String url;
    @XmlElement(name = "URLIcon")
    protected String urlIcon;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updateDate;

    /**
     * Gets the value of the identifier property.
     * 
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     */
    public void setIdentifier(int value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLanguageName }
     *     
     */
    public ArrayOfLanguageName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLanguageName }
     *     
     */
    public void setName(ArrayOfLanguageName value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLanguageName }
     *     
     */
    public ArrayOfLanguageName getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLanguageName }
     *     
     */
    public void setDescription(ArrayOfLanguageName value) {
        this.description = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the urlIcon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURLIcon() {
        return urlIcon;
    }

    /**
     * Sets the value of the urlIcon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURLIcon(String value) {
        this.urlIcon = value;
    }

    /**
     * Gets the value of the updateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the value of the updateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdateDate(XMLGregorianCalendar value) {
        this.updateDate = value;
    }

}
