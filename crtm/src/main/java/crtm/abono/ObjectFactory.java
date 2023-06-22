
package crtm.abono;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the crtm.abono package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RespuestaVentaPrepagoTituloConsultaSaldo1_QNAME = new QName("http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.VentaPrepagoTituloWS", "respuestaVentaPrepagoTituloConsultaSaldo1");
    private final static QName _PropertyChangedEventHandler_QNAME = new QName("http://schemas.datacontract.org/2004/07/System.ComponentModel", "PropertyChangedEventHandler");
    private final static QName _MulticastDelegate_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "MulticastDelegate");
    private final static QName _Delegate_QNAME = new QName("http://schemas.datacontract.org/2004/07/System", "Delegate");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _RespuestaInformacionTarjetaConsultaSaldo1_QNAME = new QName("http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.InformacionTarjetaWS", "respuestaInformacionTarjetaConsultaSaldo1");
    private final static QName _ConsultaSaldo1SNumeroTTP_QNAME = new QName("http://tempuri.org/", "sNumeroTTP");
    private final static QName _ConsultaSaldo1ResponseConsultaSaldo1Result_QNAME = new QName("http://tempuri.org/", "ConsultaSaldo1Result");
    private final static QName _ConsultaSaldoTarjeta1SNumeroTP_QNAME = new QName("http://tempuri.org/", "sNumeroTP");
    private final static QName _ConsultaSaldoTarjeta1SLenguaje_QNAME = new QName("http://tempuri.org/", "sLenguaje");
    private final static QName _ConsultaSaldoTarjeta1STipoApp_QNAME = new QName("http://tempuri.org/", "sTipoApp");
    private final static QName _ConsultaSaldoTarjeta1ResponseConsultaSaldoTarjeta1Result_QNAME = new QName("http://tempuri.org/", "ConsultaSaldoTarjeta1Result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: crtm.abono
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultaSaldo1 }
     * 
     * @return
     *     the new instance of {@link ConsultaSaldo1 }
     */
    public ConsultaSaldo1 createConsultaSaldo1() {
        return new ConsultaSaldo1();
    }

    /**
     * Create an instance of {@link ConsultaSaldo1Response }
     * 
     * @return
     *     the new instance of {@link ConsultaSaldo1Response }
     */
    public ConsultaSaldo1Response createConsultaSaldo1Response() {
        return new ConsultaSaldo1Response();
    }

    /**
     * Create an instance of {@link RespuestaVentaPrepagoTituloConsultaSaldo1 }
     * 
     * @return
     *     the new instance of {@link RespuestaVentaPrepagoTituloConsultaSaldo1 }
     */
    public RespuestaVentaPrepagoTituloConsultaSaldo1 createRespuestaVentaPrepagoTituloConsultaSaldo1() {
        return new RespuestaVentaPrepagoTituloConsultaSaldo1();
    }

    /**
     * Create an instance of {@link ConsultaSaldoTarjeta1 }
     * 
     * @return
     *     the new instance of {@link ConsultaSaldoTarjeta1 }
     */
    public ConsultaSaldoTarjeta1 createConsultaSaldoTarjeta1() {
        return new ConsultaSaldoTarjeta1();
    }

    /**
     * Create an instance of {@link ConsultaSaldoTarjeta1Response }
     * 
     * @return
     *     the new instance of {@link ConsultaSaldoTarjeta1Response }
     */
    public ConsultaSaldoTarjeta1Response createConsultaSaldoTarjeta1Response() {
        return new ConsultaSaldoTarjeta1Response();
    }

    /**
     * Create an instance of {@link RespuestaInformacionTarjetaConsultaSaldo1 }
     * 
     * @return
     *     the new instance of {@link RespuestaInformacionTarjetaConsultaSaldo1 }
     */
    public RespuestaInformacionTarjetaConsultaSaldo1 createRespuestaInformacionTarjetaConsultaSaldo1() {
        return new RespuestaInformacionTarjetaConsultaSaldo1();
    }

    /**
     * Create an instance of {@link PropertyChangedEventHandler }
     * 
     * @return
     *     the new instance of {@link PropertyChangedEventHandler }
     */
    public PropertyChangedEventHandler createPropertyChangedEventHandler() {
        return new PropertyChangedEventHandler();
    }

    /**
     * Create an instance of {@link MulticastDelegate }
     * 
     * @return
     *     the new instance of {@link MulticastDelegate }
     */
    public MulticastDelegate createMulticastDelegate() {
        return new MulticastDelegate();
    }

    /**
     * Create an instance of {@link Delegate }
     * 
     * @return
     *     the new instance of {@link Delegate }
     */
    public Delegate createDelegate() {
        return new Delegate();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.VentaPrepagoTituloWS", name = "respuestaVentaPrepagoTituloConsultaSaldo1")
    public JAXBElement<RespuestaVentaPrepagoTituloConsultaSaldo1> createRespuestaVentaPrepagoTituloConsultaSaldo1(RespuestaVentaPrepagoTituloConsultaSaldo1 value) {
        return new JAXBElement<>(_RespuestaVentaPrepagoTituloConsultaSaldo1_QNAME, RespuestaVentaPrepagoTituloConsultaSaldo1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyChangedEventHandler }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PropertyChangedEventHandler }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System.ComponentModel", name = "PropertyChangedEventHandler")
    public JAXBElement<PropertyChangedEventHandler> createPropertyChangedEventHandler(PropertyChangedEventHandler value) {
        return new JAXBElement<>(_PropertyChangedEventHandler_QNAME, PropertyChangedEventHandler.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MulticastDelegate }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link MulticastDelegate }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "MulticastDelegate")
    public JAXBElement<MulticastDelegate> createMulticastDelegate(MulticastDelegate value) {
        return new JAXBElement<>(_MulticastDelegate_QNAME, MulticastDelegate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Delegate }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Delegate }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/System", name = "Delegate")
    public JAXBElement<Delegate> createDelegate(Delegate value) {
        return new JAXBElement<>(_Delegate_QNAME, Delegate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Double }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Float }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link QName }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Short }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Long }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/ProxyVentaPrepagoTituloWS.InformacionTarjetaWS", name = "respuestaInformacionTarjetaConsultaSaldo1")
    public JAXBElement<RespuestaInformacionTarjetaConsultaSaldo1> createRespuestaInformacionTarjetaConsultaSaldo1(RespuestaInformacionTarjetaConsultaSaldo1 value) {
        return new JAXBElement<>(_RespuestaInformacionTarjetaConsultaSaldo1_QNAME, RespuestaInformacionTarjetaConsultaSaldo1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sNumeroTTP", scope = ConsultaSaldo1 .class)
    public JAXBElement<String> createConsultaSaldo1SNumeroTTP(String value) {
        return new JAXBElement<>(_ConsultaSaldo1SNumeroTTP_QNAME, String.class, ConsultaSaldo1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaVentaPrepagoTituloConsultaSaldo1 }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "ConsultaSaldo1Result", scope = ConsultaSaldo1Response.class)
    public JAXBElement<RespuestaVentaPrepagoTituloConsultaSaldo1> createConsultaSaldo1ResponseConsultaSaldo1Result(RespuestaVentaPrepagoTituloConsultaSaldo1 value) {
        return new JAXBElement<>(_ConsultaSaldo1ResponseConsultaSaldo1Result_QNAME, RespuestaVentaPrepagoTituloConsultaSaldo1 .class, ConsultaSaldo1Response.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sNumeroTP", scope = ConsultaSaldoTarjeta1 .class)
    public JAXBElement<String> createConsultaSaldoTarjeta1SNumeroTP(String value) {
        return new JAXBElement<>(_ConsultaSaldoTarjeta1SNumeroTP_QNAME, String.class, ConsultaSaldoTarjeta1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sLenguaje", scope = ConsultaSaldoTarjeta1 .class)
    public JAXBElement<String> createConsultaSaldoTarjeta1SLenguaje(String value) {
        return new JAXBElement<>(_ConsultaSaldoTarjeta1SLenguaje_QNAME, String.class, ConsultaSaldoTarjeta1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sTipoApp", scope = ConsultaSaldoTarjeta1 .class)
    public JAXBElement<String> createConsultaSaldoTarjeta1STipoApp(String value) {
        return new JAXBElement<>(_ConsultaSaldoTarjeta1STipoApp_QNAME, String.class, ConsultaSaldoTarjeta1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaInformacionTarjetaConsultaSaldo1 }{@code >}
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "ConsultaSaldoTarjeta1Result", scope = ConsultaSaldoTarjeta1Response.class)
    public JAXBElement<RespuestaInformacionTarjetaConsultaSaldo1> createConsultaSaldoTarjeta1ResponseConsultaSaldoTarjeta1Result(RespuestaInformacionTarjetaConsultaSaldo1 value) {
        return new JAXBElement<>(_ConsultaSaldoTarjeta1ResponseConsultaSaldoTarjeta1Result_QNAME, RespuestaInformacionTarjetaConsultaSaldo1 .class, ConsultaSaldoTarjeta1Response.class, value);
    }

}
