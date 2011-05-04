//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.16 at 10:28:41 AM GMT 
//


package uk.ac.ebi.pride.data.jaxb.pridexml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Data type for additional data vectors (beyond m/z and intensity).
 * 
 * <p>Java class for supDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="supDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arrayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="float" type="{http://www.w3.org/2001/XMLSchema}float" maxOccurs="unbounded"/>
 *           &lt;element name="double" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
 *           &lt;element name="int" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *           &lt;element name="boolean" type="{http://www.w3.org/2001/XMLSchema}boolean" maxOccurs="unbounded"/>
 *           &lt;element name="string" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *           &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}float" maxOccurs="unbounded"/>
 *           &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="length" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="indexed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="offset" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supDataType", propOrder = {
    "arrayName",
    "_float",
    "_double",
    "_int",
    "_boolean",
    "string",
    "time",
    "uri"
})
public class SupDataType {

    @XmlElement(required = true)
    protected String arrayName;
    @XmlElement(name = "float", type = Float.class)
    protected List<Float> _float;
    @XmlElement(name = "double", type = Double.class)
    protected List<Double> _double;
    @XmlElement(name = "int", type = Integer.class)
    protected List<Integer> _int;
    @XmlElement(name = "boolean", type = Boolean.class)
    protected List<Boolean> _boolean;
    protected List<String> string;
    @XmlElement(type = Float.class)
    protected List<Float> time;
    @XmlElement(name = "URI")
    @XmlSchemaType(name = "anyURI")
    protected List<String> uri;
    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute(required = true)
    protected int length;
    @XmlAttribute(required = true)
    protected boolean indexed;
    @XmlAttribute
    protected Integer offset;

    /**
     * Gets the value of the arrayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrayName() {
        return arrayName;
    }

    /**
     * Sets the value of the arrayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrayName(String value) {
        this.arrayName = value;
    }

    /**
     * Gets the value of the float property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the float property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFloat().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Float }
     * 
     * 
     */
    public List<Float> getFloat() {
        if (_float == null) {
            _float = new ArrayList<Float>();
        }
        return this._float;
    }

    /**
     * Gets the value of the double property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the double property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDouble().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getDouble() {
        if (_double == null) {
            _double = new ArrayList<Double>();
        }
        return this._double;
    }

    /**
     * Gets the value of the int property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the int property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getInt() {
        if (_int == null) {
            _int = new ArrayList<Integer>();
        }
        return this._int;
    }

    /**
     * Gets the value of the boolean property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boolean property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoolean().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * 
     * 
     */
    public List<Boolean> getBoolean() {
        if (_boolean == null) {
            _boolean = new ArrayList<Boolean>();
        }
        return this._boolean;
    }

    /**
     * Gets the value of the string property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the string property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getString().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getString() {
        if (string == null) {
            string = new ArrayList<String>();
        }
        return this.string;
    }

    /**
     * Gets the value of the time property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the time property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Float }
     * 
     * 
     */
    public List<Float> getTime() {
        if (time == null) {
            time = new ArrayList<Float>();
        }
        return this.time;
    }

    /**
     * Gets the value of the uri property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uri property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getURI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getURI() {
        if (uri == null) {
            uri = new ArrayList<String>();
        }
        return this.uri;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(int value) {
        this.length = value;
    }

    /**
     * Gets the value of the indexed property.
     * 
     */
    public boolean isIndexed() {
        return indexed;
    }

    /**
     * Sets the value of the indexed property.
     * 
     */
    public void setIndexed(boolean value) {
        this.indexed = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getOffset() {
        if (offset == null) {
            return  0;
        } else {
            return offset;
        }
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOffset(Integer value) {
        this.offset = value;
    }

}
