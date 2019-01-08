package mil.dtic.sitemaps.management.configuration;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.resources.domain.TChangeFreq;

@Component
@ConfigurationProperties("sitemap.manager")
public class SitemapManagerConfiguration {
    private String rootPath;
    private String rootPathWeb;
    private int keyLength;
    private BigDecimal defaultPriority;
    private TChangeFreq defaultChangeFrequency;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPathWeb() {
        return rootPathWeb;
    }

    public void setRootPathWeb(String rootPathWeb) {
        this.rootPathWeb = rootPathWeb;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public BigDecimal getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(BigDecimal defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public TChangeFreq getDefaultChangeFrequency() {
        return defaultChangeFrequency;
    }

    public void setDefaultChangeFrequency(TChangeFreq defaultChangeFrequency) {
        this.defaultChangeFrequency = defaultChangeFrequency;
    }

}