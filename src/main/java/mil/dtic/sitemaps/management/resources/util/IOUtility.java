package mil.dtic.sitemaps.management.resources.util;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.domain.ObjectFactory;
import mil.dtic.sitemaps.management.resources.domain.Sitemapindex;
import mil.dtic.sitemaps.management.resources.domain.Urlset;

@Component
public class IOUtility {
    @Autowired
    protected SitemapManagerConfiguration configuration;
    
	@Autowired
	protected SitemapIndexKeyUtility sitemapIndexKeyUtility;
	
	public Sitemapindex loadSitemapIndex() {
    	String sitemapIndexPath = String.format("%ssitemap.xml", configuration.getRootPath());
    	File sitemapIndexFile = new File(sitemapIndexPath);
    	Sitemapindex returnSitemapIndex = null;
    	if(sitemapIndexFile.exists() && !sitemapIndexFile.isDirectory()) {
    		XmlMapper mapper = new XmlMapper();
    		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	    mapper.setDateFormat(new StdDateFormat());
    		try {
				returnSitemapIndex = mapper.readValue(sitemapIndexFile, Sitemapindex.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(returnSitemapIndex == null) {
    		ObjectFactory objectFactory = new ObjectFactory();
    		returnSitemapIndex = objectFactory.createSitemapindex();
    	}
    	return returnSitemapIndex;
    }

    public void saveSitemapIndex(Sitemapindex sitemapIndex) {
    	String sitemapIndexPath = String.format("%ssitemap.xml", configuration.getRootPath());
    	File sitemapIndexFile = new File(sitemapIndexPath);
    	
		XmlMapper mapper = new XmlMapper();
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    mapper.setDateFormat(new StdDateFormat());
		try {
			mapper.writeValue(sitemapIndexFile, sitemapIndex);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Urlset loadSitemap(String sitemapKey) {
    	String sitemapPath = sitemapIndexKeyUtility.getSitemapFilePath(configuration.getRootPath(), sitemapKey);
    	File sitemapFile = new File(sitemapPath);
    	Urlset returnSitemap = null;
    	if(sitemapFile.exists() && !sitemapFile.isDirectory()) {
    		XmlMapper mapper = new XmlMapper();
    		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	    //mapper.setDateFormat(new StdDateFormat());
    		try {
    			returnSitemap = mapper.readValue(sitemapFile, Urlset.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(returnSitemap == null) {
    		ObjectFactory objectFactory = new ObjectFactory();
    		returnSitemap = objectFactory.createUrlset();
    	}
    	return returnSitemap;
    }

    public void saveSitemap(String sitemapKey, Urlset sitemap) {
    	String sitemapPath = sitemapIndexKeyUtility.getSitemapFilePath(configuration.getRootPath(), sitemapKey);
    	File sitemapFile = new File(sitemapPath);
    	
		XmlMapper mapper = new XmlMapper();
		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    //mapper.setDateFormat(new StdDateFormat());
		try {
			mapper.writeValue(sitemapFile, sitemap);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
