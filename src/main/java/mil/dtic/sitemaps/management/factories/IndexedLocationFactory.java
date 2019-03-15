package mil.dtic.sitemaps.management.factories;

import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.resources.IndexedLocation;

/**
 * Factory for creating IndexedLocation objects
 * @author Battelle
 */
@Component
public class IndexedLocationFactory {

    /**
     * 
     * @return A default, initialized IndexedLocation
     */
	public IndexedLocation createIndexedLocation() {
		return new IndexedLocation();
	}
	
    /**
     * 
     * @param location Location to set in new IndexedLocation
     * @return A new IndexedLocation with the given location already set
     */
	public IndexedLocation createIndexedLocation(String location) {
		IndexedLocation returnLocation = new IndexedLocation();
		returnLocation.setLocation(location);
		
		return returnLocation;
	}
}
