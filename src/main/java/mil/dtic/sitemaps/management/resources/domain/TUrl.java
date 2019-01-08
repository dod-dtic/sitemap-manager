
package mil.dtic.sitemaps.management.resources.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


/**
 * 
 *         Container for the data needed to describe a document to crawl.
 *       
 * 
 * <p>Java class for tUrl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tUrl">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loc" type="{http://www.sitemaps.org/schemas/sitemap/0.9}tLoc"/>
 *         &lt;element name="lastmod" type="{http://www.sitemaps.org/schemas/sitemap/0.9}tLastmod" minOccurs="0"/>
 *         &lt;element name="changefreq" type="{http://www.sitemaps.org/schemas/sitemap/0.9}tChangeFreq" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.sitemaps.org/schemas/sitemap/0.9}tPriority" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tUrl", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9", propOrder = {
    "loc",
    "lastmod",
    "changefreq",
    "priority"
})
public class TUrl {

    @XmlElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9", required = true)
    @XmlSchemaType(name = "anyURI")
    @JacksonXmlProperty(namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
    protected String loc;
    @XmlElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @JacksonXmlProperty(namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    protected Date lastmod;
    @XmlElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @XmlSchemaType(name = "string")
    @JacksonXmlProperty(namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
    protected TChangeFreq changefreq;
    @XmlElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @JacksonXmlProperty(namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
    protected BigDecimal priority;

    /**
     * Gets the value of the loc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the value of the loc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoc(String value) {
        this.loc = value;
    }

    /**
     * Gets the value of the lastmod property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getLastmod() {
        return lastmod;
    }

    /**
     * Sets the value of the lastmod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setLastmod(Date value) {
        this.lastmod = value;
    }

    /**
     * Gets the value of the changefreq property.
     * 
     * @return
     *     possible object is
     *     {@link TChangeFreq }
     *     
     */
    public TChangeFreq getChangefreq() {
        return changefreq;
    }

    /**
     * Sets the value of the changefreq property.
     * 
     * @param value
     *     allowed object is
     *     {@link TChangeFreq }
     *     
     */
    public void setChangefreq(TChangeFreq value) {
        this.changefreq = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPriority(BigDecimal value) {
        this.priority = value;
    }


}
