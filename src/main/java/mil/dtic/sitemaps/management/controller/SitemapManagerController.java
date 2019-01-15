package mil.dtic.sitemaps.management.controller;

import mil.dtic.sitemaps.management.SitemapManager;
import mil.dtic.sitemaps.management.factories.IndexedLocationListFactory;
import mil.dtic.sitemaps.management.resources.IndexedLocationList;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SitemapManagerController {
    @Autowired
    protected SitemapManager sitemapManager;
    
    @Autowired
    protected IndexedLocationListFactory indexedLocationListFactory;
    
    @RequestMapping(value={"/"}, method=RequestMethod.POST)
    public ResponseEntity<String> addLocations(@RequestBody() IndexedLocationList locationList) {
    	boolean success = sitemapManager.addOrUpdateLocationIndices(locationList);
    	if(success) {
    		return new ResponseEntity<String>("created", HttpStatus.CREATED);
    	} else {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value={"/"}, method=RequestMethod.PUT)
    public ResponseEntity<String> updateLocations(@RequestBody() IndexedLocationList locationList) {
    	boolean success = sitemapManager.addOrUpdateLocationIndices(locationList);
    	if(success) {
        	return new ResponseEntity<String>("updated", HttpStatus.OK);
    	} else {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value={"/"}, method=RequestMethod.DELETE)
    public ResponseEntity<String> removeLocations(@RequestBody() IndexedLocationList locationList) {
    	boolean success = sitemapManager.removeLocationIndices(locationList);
    	if(success) {
        	return new ResponseEntity<String>("deleted", HttpStatus.OK);
    	} else {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    

    @RequestMapping(value={"/simple"}, method=RequestMethod.POST)
    public ResponseEntity<String> addLocations(@RequestBody() String fileContent) {
    	IndexedLocationList locationList;
		try {
			locationList = indexedLocationListFactory.createIndexedLocationList(fileContent);
		} catch (IOException e) {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	return addLocations(locationList);
    }

    @RequestMapping(value={"/simple"}, method=RequestMethod.PUT)
    public ResponseEntity<String> updateLocations(@RequestBody() String fileContent) {
    	IndexedLocationList locationList;
		try {
			locationList = indexedLocationListFactory.createIndexedLocationList(fileContent);
		} catch (IOException e) {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	return updateLocations(locationList);
    }

    @RequestMapping(value={"/simple"}, method=RequestMethod.DELETE)
    public ResponseEntity<String> removeLocations(@RequestBody() String fileContent) {
    	IndexedLocationList locationList;
		try {
			locationList = indexedLocationListFactory.createIndexedLocationList(fileContent);
		} catch (IOException e) {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	return removeLocations(locationList);
    }
    
}
