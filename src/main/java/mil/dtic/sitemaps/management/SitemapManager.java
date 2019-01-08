package mil.dtic.sitemaps.management;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.factories.IndexedLocationMapFactory;
import mil.dtic.sitemaps.management.factories.UrlFactory;
import mil.dtic.sitemaps.management.resources.IndexedLocation;
import mil.dtic.sitemaps.management.resources.IndexedLocationList;
import mil.dtic.sitemaps.management.resources.IndexedLocationMap;
import mil.dtic.sitemaps.management.resources.domain.ObjectFactory;
import mil.dtic.sitemaps.management.resources.domain.Sitemapindex;
import mil.dtic.sitemaps.management.resources.domain.TSitemap;
import mil.dtic.sitemaps.management.resources.domain.TUrl;
import mil.dtic.sitemaps.management.resources.domain.Urlset;
import mil.dtic.sitemaps.management.resources.util.IOUtility;
import mil.dtic.sitemaps.management.resources.util.SitemapIndexKeyUtility;

@Component
public class SitemapManager {
    @Autowired
    protected SitemapManagerConfiguration configuration;
    
    @Autowired
    protected IndexedLocationMapFactory indexedLocationMapFactory;
    
    @Autowired
    protected UrlFactory urlFactory;
    
    @Autowired
    protected IOUtility ioUtility;

	@Autowired
	protected SitemapIndexKeyUtility sitemapIndexKeyUtility;
    
    public boolean addOrUpdateLocationIndices(IndexedLocationList locationList) {
    	boolean success = true;
    	ObjectFactory objectFactory = new ObjectFactory();
    	
    	IndexedLocationMap indexedLocationMap = indexedLocationMapFactory.createIndexedLocationMap(locationList);
    	Sitemapindex sitemapIndex = ioUtility.loadSitemapIndex();
    	
    	Map<String,List<IndexedLocation>> locationMap = indexedLocationMap.getLocationMap();
    	List<TSitemap> sitemapList = sitemapIndex.getSitemap();
    	if(locationMap != null && locationMap.size() > 0) {
	    	for(String locationKey : locationMap.keySet()) {
	    		//determine what the web location would be for current location key
	    		String currentWebPath = sitemapIndexKeyUtility.getSitemapWebPath(configuration.getRootPathWeb(), locationKey);
	    		//iterate over the sitemap filenames looking for a match based on the current key
	    		TSitemap match = null;
	    		for(TSitemap currentSitemap : sitemapList) {
	    			if(currentSitemap.getLoc().equalsIgnoreCase(currentWebPath)) {
	    				match = currentSitemap;
	    				break;
	    			}
	    		}
	    		//if no match was found, add a new sitemap to the index
	    		if(match == null) {
	    			match = objectFactory.createTSitemap();
	    			match.setLoc(currentWebPath);
	    			sitemapList.add(match);
	    		}
	    		success = addOrUpdateLocations(locationKey, locationMap.get(locationKey));
	    		if(!success) { 
	    			break; 
    			}
    			match.setLastmod(new Date());
	    	}
    	}
    	
    	if(success) {
    		ioUtility.saveSitemapIndex(sitemapIndex);
    	}
    	return success;
    }
    
    public boolean addOrUpdateLocations(String locationKey, List<IndexedLocation> locationList) {
    	boolean success = true;
    	Urlset currentSitemap = ioUtility.loadSitemap(locationKey);
    	
    	List<TUrl> urlList = currentSitemap.getUrl();
    	
    	if(locationList != null && locationList.size() > 0) {
	    	for(IndexedLocation currentLocation : locationList) {
	    		//search through the existing urlList to look for a matching item
	    		TUrl match = null;
	    		for(TUrl currentUrl : urlList) {
	    			if(currentUrl.getLoc().equalsIgnoreCase(currentLocation.getLocation())) {
	    				match = currentUrl;
	    				break;
	    			}
	    		}
	    		//if no match was found, add a new URL to the list
	    		if(match == null) {
	    			match = urlFactory.createTUrl(currentLocation);
	    			urlList.add(match);
    			//otherwise, modify the existing value
	    		} else {
	    			urlFactory.updateTUrl(match, currentLocation);
	    		}
	    	}
    	}
    	
    	if(success) {
    		ioUtility.saveSitemap(locationKey, currentSitemap);
    	}
    	return success;
    }
}
