//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.06 at 11:00:22 AM CEST 
//


package org.eobjects.analyzer.configuration.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for datastoreCatalogType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="datastoreCatalogType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jdbc-datastore" type="{http://eobjects.org/analyzerbeans/configuration/1.0}jdbcDatastoreType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="csv-datastore" type="{http://eobjects.org/analyzerbeans/configuration/1.0}csvDatastoreType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="composite-datastore" type="{http://eobjects.org/analyzerbeans/configuration/1.0}compositeDatastoreType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datastoreCatalogType", propOrder = {
    "jdbcDatastore",
    "csvDatastore",
    "compositeDatastore"
})
public class DatastoreCatalogType {

    @XmlElement(name = "jdbc-datastore")
    protected List<JdbcDatastoreType> jdbcDatastore;
    @XmlElement(name = "csv-datastore")
    protected List<CsvDatastoreType> csvDatastore;
    @XmlElement(name = "composite-datastore")
    protected List<CompositeDatastoreType> compositeDatastore;

    /**
     * Gets the value of the jdbcDatastore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jdbcDatastore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJdbcDatastore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JdbcDatastoreType }
     * 
     * 
     */
    public List<JdbcDatastoreType> getJdbcDatastore() {
        if (jdbcDatastore == null) {
            jdbcDatastore = new ArrayList<JdbcDatastoreType>();
        }
        return this.jdbcDatastore;
    }

    /**
     * Gets the value of the csvDatastore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the csvDatastore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCsvDatastore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CsvDatastoreType }
     * 
     * 
     */
    public List<CsvDatastoreType> getCsvDatastore() {
        if (csvDatastore == null) {
            csvDatastore = new ArrayList<CsvDatastoreType>();
        }
        return this.csvDatastore;
    }

    /**
     * Gets the value of the compositeDatastore property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compositeDatastore property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompositeDatastore().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositeDatastoreType }
     * 
     * 
     */
    public List<CompositeDatastoreType> getCompositeDatastore() {
        if (compositeDatastore == null) {
            compositeDatastore = new ArrayList<CompositeDatastoreType>();
        }
        return this.compositeDatastore;
    }

}
