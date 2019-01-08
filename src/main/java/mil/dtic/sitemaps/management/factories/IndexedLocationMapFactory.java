package mil.dtic.sitemaps.management.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.IndexedLocation;
import mil.dtic.sitemaps.management.resources.IndexedLocationList;
import mil.dtic.sitemaps.management.resources.IndexedLocationMap;
import mil.dtic.sitemaps.management.resources.util.SitemapIndexKeyUtility;

@Component
public class IndexedLocationMapFactory {
	
    @Autowired
    protected SitemapManagerConfiguration configuration;
    
	@Autowired
	protected SitemapIndexKeyUtility sitemapIndexKeyUtility;
    
	public IndexedLocationMap createIndexedLocationMap() {
		return new IndexedLocationMap();
	}

	public IndexedLocationMap createIndexedLocationMap(IndexedLocationList locationList) {
		IndexedLocationMap indexedLocationMap = createIndexedLocationMap();
		Map<String,List<IndexedLocation>> locationMap = indexedLocationMap.getLocationMap();
		if(locationList.getUrls() != null && locationList.getUrls().size() > 0) {
			for(IndexedLocation location : locationList.getUrls()) {
				String locationKey = sitemapIndexKeyUtility.determineIndexKey(
					configuration.getKeyLength(), 
					location.getName(), 
					location.getLocation()
				);
				
				if(!locationMap.containsKey(locationKey)) {
					locationMap.put(locationKey, new ArrayList<IndexedLocation>());
				}
				locationMap.get(locationKey).add(location);
			}
		}
		return indexedLocationMap;
	}
}
