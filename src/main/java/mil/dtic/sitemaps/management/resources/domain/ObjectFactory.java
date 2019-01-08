
package mil.dtic.sitemaps.management.resources.domain;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mil.dtic.sitemaps.management.resources.domain package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mil.dtic.sitemaps.management.resources.domain
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Sitemapindex }
     * 
     */
    public Sitemapindex createSitemapindex() {
        return new Sitemapindex();
    }

    /**
     * Create an instance of {@link TSitemap }
     * 
     */
    public TSitemap createTSitemap() {
        return new TSitemap();
    }
    
    /**
     * Create an instance of {@link Urlset }
     * 
     */
    public Urlset createUrlset() {
        return new Urlset();
    }

    /**
     * Create an instance of {@link TUrl }
     * 
     */
    public TUrl createTUrl() {
        return new TUrl();
    }

}
