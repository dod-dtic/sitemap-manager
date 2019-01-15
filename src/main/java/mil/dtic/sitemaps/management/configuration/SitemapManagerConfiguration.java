package mil.dtic.sitemaps.management.configuration;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.resources.domain.TChangeFreq;

/**
 * Manages configurations for sitemap manager
 * @author Battelle
 */
@Component
@ConfigurationProperties("sitemap.manager")
public class SitemapManagerConfiguration {
    private String rootPath;
    private String rootPathWeb;
    private int keyLength;
    private BigDecimal defaultPriority;
    private TChangeFreq defaultChangeFrequency;

    /**
     * 
     * @return The directory path in which sitemap files are created
     */
    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * 
     * @return The root web path for sitemap locations
     */
    public String getRootPathWeb() {
        return rootPathWeb;
    }

    public void setRootPathWeb(String rootPathWeb) {
        this.rootPathWeb = rootPathWeb;
    }

    /**
     * 
     * @return The number of characters to use in automatically determining sitemap file names.
     */
    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    /**
     * 
     * @return The default priority level to use if no priority is given, for an entry.
     */
    public BigDecimal getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(BigDecimal defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    /**
     * 
     * @return The default change frequency to use if no change frequency is given, for an entry.
     */
    public TChangeFreq getDefaultChangeFrequency() {
        return defaultChangeFrequency;
    }

    public void setDefaultChangeFrequency(TChangeFreq defaultChangeFrequency) {
        this.defaultChangeFrequency = defaultChangeFrequency;
    }

}