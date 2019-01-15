package mil.dtic.sitemaps.management.factories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mil.dtic.sitemaps.management.resources.IndexedLocation;
import mil.dtic.sitemaps.management.resources.IndexedLocationList;
import org.apache.commons.lang3.StringUtils;

/**
 * Factory for creating indexed location lists
 * @author Battelle
 */
@Component
public class IndexedLocationListFactory {
	
	@Autowired
	private IndexedLocationFactory indexedLocationFactory;
	
    /**
     * 
     * @return New, empty indexed location list object
     */
	public IndexedLocationList createIndexedLocationList() {
		return new IndexedLocationList();
	}

    /**
     * 
     * @param simpleList Newline-delimited list of URLs
     * @return New indexed location list containing all locations from simpleList
     * @throws IOException 
     */
	public IndexedLocationList createIndexedLocationList(String simpleList) throws IOException {
		IndexedLocationList returnList = new IndexedLocationList();
		List<IndexedLocation> listOfParsedLocations = new ArrayList<>();
		//read the string line by line, each line containing a location
		BufferedReader reader = new BufferedReader(new StringReader(simpleList));
        String locationString = reader.readLine();
        while (StringUtils.isNotBlank(locationString)) {
        	IndexedLocation newLocation = indexedLocationFactory.createIndexedLocation(locationString);
        	listOfParsedLocations.add(newLocation);
        	locationString = reader.readLine();
        }
        
        returnList.setUrls(listOfParsedLocations);
        
		return returnList;
	}

}
