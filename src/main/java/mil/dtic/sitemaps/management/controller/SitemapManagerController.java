package mil.dtic.sitemaps.management.controller;

import mil.dtic.sitemaps.management.SitemapManager;
import mil.dtic.sitemaps.management.resources.IndexedLocationList;

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
    
    @RequestMapping(value={"/sitemap-manager"}, method=RequestMethod.POST)
    public ResponseEntity<String> addLocations(@RequestBody() IndexedLocationList locationList) {
    	boolean success = sitemapManager.addOrUpdateLocationIndices(locationList);
    	if(success) {
    		return new ResponseEntity<String>("created", HttpStatus.CREATED);
    	} else {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value={"/sitemap-manager"}, method=RequestMethod.PUT)
    public ResponseEntity<String> updateLocations(@RequestBody() IndexedLocationList locationList) {
    	boolean success = sitemapManager.addOrUpdateLocationIndices(locationList);
    	if(success) {
        	return new ResponseEntity<String>("updated", HttpStatus.OK);
    	} else {
    		return new ResponseEntity<String>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value={"/sitemap-manager"}, method=RequestMethod.DELETE)
    public ResponseEntity<String> deleteLocations(@RequestBody() IndexedLocationList locationList) {
    	return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }
    
    
}
