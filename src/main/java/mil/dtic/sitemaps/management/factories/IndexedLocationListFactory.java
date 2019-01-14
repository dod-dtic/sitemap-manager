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

@Component
public class IndexedLocationListFactory {
	
	@Autowired
	private IndexedLocationFactory indexedLocationFactory;
	
	public IndexedLocationList createIndexedLocationList() {
		return new IndexedLocationList();
	}

	public IndexedLocationList createIndexedLocationList(String simpleList) throws IOException {
		IndexedLocationList returnList = new IndexedLocationList();
		List<IndexedLocation> listOfParsedLocations = new ArrayList<IndexedLocation>();
		//read the string line by line, each line containing a location
		BufferedReader reader = new BufferedReader(new StringReader(simpleList));
        String locationString = reader.readLine();
        while (locationString != null) {
        	IndexedLocation newLocation = indexedLocationFactory.createIndexedLocation(locationString);
        	listOfParsedLocations.add(newLocation);
        	locationString = reader.readLine();
        }
        
        returnList.setUrls(listOfParsedLocations);
        
		return returnList;
	}

}
