package mil.dtic.sitemaps.management.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping of sitemap names to indexed location lists
 * @author Battelle
 */
public class IndexedLocationMap {
	private Map<String,List<IndexedLocation>> locationMap;
	
	public IndexedLocationMap() {
		locationMap = new HashMap<>();
	}

    /**
     * 
     * @return Mapping between sitemap names and indexed location lists
     */
	public Map<String,List<IndexedLocation>> getLocationMap() {
		return locationMap;
	}
}
