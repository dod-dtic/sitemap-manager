package mil.dtic.sitemaps.management.factories;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.IndexedLocation;
import mil.dtic.sitemaps.management.resources.domain.ObjectFactory;
import mil.dtic.sitemaps.management.resources.domain.TUrl;

/**
 * Factory for URLs
 * @author Battelle
 */
@Component
public class UrlFactory {

    @Autowired
    protected SitemapManagerConfiguration configuration;
    
    /**
     * Create a new URL from a given location
     * @param location Location from which to create a URL
     * @return URL for given location
     */
    public TUrl createTUrl (IndexedLocation location) {
    	ObjectFactory objectFactory = new ObjectFactory();
    	TUrl returnUrl = objectFactory.createTUrl();
    	
    	return updateTUrl(returnUrl, location);
    }
    
    /**
     * Update TUrl object with given location
     * @param url URL to update
     * @param location Location from which to update URL
     * @return TUrl passed in as argument
     */
    public TUrl updateTUrl (TUrl url, IndexedLocation location) {
    	url.setLoc(location.getLocation());
    	
    	//only update this value if given a non-null value, or if a default is needed
    	if(location.getLastModified() != null) {
    		url.setLastmod(location.getLastModified());
    	} else if(url.getLastmod() == null) {
    		url.setLastmod(new Date());
    	}

    	//only update this value if given a non-null value, or if a default is needed
    	if(location.getPriority() != null) {
    		url.setPriority(location.getPriority());
    	} else if(url.getPriority() == null) {
    		url.setPriority(configuration.getDefaultPriority());
    	}

    	//only update this value if given a non-null value, or if a default is needed
    	if(location.getChangeFrequency() != null) {
    		url.setChangefreq(location.getChangeFrequency());
    	} else if(url.getChangefreq() == null) {
    		url.setChangefreq(configuration.getDefaultChangeFrequency());
    	}
    	
    	return url;
    }
}
