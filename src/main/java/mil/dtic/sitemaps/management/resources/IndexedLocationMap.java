package mil.dtic.sitemaps.management.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexedLocationMap {
	private Map<String,List<IndexedLocation>> locationMap;
	
	public IndexedLocationMap() {
		locationMap = new HashMap<String,List<IndexedLocation>>();
	}

	public Map<String,List<IndexedLocation>> getLocationMap() {
		return locationMap;
	}
}
