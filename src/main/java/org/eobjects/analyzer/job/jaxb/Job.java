//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.06 at 11:00:21 AM CEST 
//


package org.eobjects.analyzer.job.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="job-metadata" type="{http://eobjects.org/analyzerbeans/job/1.0}jobMetadataType" minOccurs="0"/>
 *         &lt;element name="source" type="{http://eobjects.org/analyzerbeans/job/1.0}sourceType" minOccurs="0"/>
 *         &lt;element name="transformation" type="{http://eobjects.org/analyzerbeans/job/1.0}transformationType" minOccurs="0"/>
 *         &lt;element name="analysis" type="{http://eobjects.org/analyzerbeans/job/1.0}analysisType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "jobMetadata",
    "source",
    "transformation",
    "analysis"
})
@XmlRootElement(name = "job")
public class Job {

    @XmlElement(name = "job-metadata")
    protected JobMetadataType jobMetadata;
    protected SourceType source;
    protected TransformationType transformation;
    @XmlElement(required = true)
    protected AnalysisType analysis;

    /**
     * Gets the value of the jobMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link JobMetadataType }
     *     
     */
    public JobMetadataType getJobMetadata() {
        return jobMetadata;
    }

    /**
     * Sets the value of the jobMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobMetadataType }
     *     
     */
    public void setJobMetadata(JobMetadataType value) {
        this.jobMetadata = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link SourceType }
     *     
     */
    public SourceType getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceType }
     *     
     */
    public void setSource(SourceType value) {
        this.source = value;
    }

    /**
     * Gets the value of the transformation property.
     * 
     * @return
     *     possible object is
     *     {@link TransformationType }
     *     
     */
    public TransformationType getTransformation() {
        return transformation;
    }

    /**
     * Sets the value of the transformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransformationType }
     *     
     */
    public void setTransformation(TransformationType value) {
        this.transformation = value;
    }

    /**
     * Gets the value of the analysis property.
     * 
     * @return
     *     possible object is
     *     {@link AnalysisType }
     *     
     */
    public AnalysisType getAnalysis() {
        return analysis;
    }

    /**
     * Sets the value of the analysis property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnalysisType }
     *     
     */
    public void setAnalysis(AnalysisType value) {
        this.analysis = value;
    }

}
