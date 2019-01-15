package mil.dtic.sitemaps.management.resources.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

/**
 * Utility for determining sitemap names (keys) and getting paths to sitemap files
 * @author Battelle
 */
@Component
public class SitemapIndexKeyUtility {
	
    /**
     * 
     * @param keyLength Max number of characters to consider from name
     * @param name Name to derive key from
     * @return Key based on name and key length
     */
	public String determineIndexKey(int keyLength, String name) {
		if(keyLength <= 0) {
			throw new IllegalArgumentException("keyLength must be greater than zero");
		}
		if(StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name cannot be null, empty, or blank");
		}
		//Trim the name
		String trimmedName = StringUtils.trim(name);
		//Determine the remaining key length, bounded by keyLength 
		int allowableLength = Math.min(keyLength, trimmedName.length());
		//Generate the returned key
		String returnKey = trimmedName.substring(0, allowableLength);
		return returnKey;
	}
	
    /**
     * 
     * @param keyLength Max number of characters to consider from name or location
     * @param name Optional, name of location to derive key from
     * @param location Location to derive key for
     * @return Sitemap name/key based on name or location and key length
     */
	public String determineIndexKey(int keyLength, String name, String location) {
		if(keyLength <= 0) {
			throw new IllegalArgumentException("keyLength must be greater than zero");
		}
		//If the name is not blank, use the name to determine the key
		if(!StringUtils.isBlank(name)) {
			return determineIndexKey(keyLength, name);
		}
		if(StringUtils.isBlank(location)) {
			throw new IllegalArgumentException("location cannot be null, empty, or blank when name is null, empty, or blank");
		}
		UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(location)) {
			throw new IllegalArgumentException("location is not a valid URL");
		}
		//Determine the name from the location
		String determinedName = determineName(location);
		//Determine the key from the determined name
		return determineIndexKey(keyLength, determinedName);
		
	}
	
    /**
     * 
     * @param location Location to determine sitemap file name for
     * @return Name of sitemap file based on location
     */
	public String determineName(String location) {
		if(StringUtils.isBlank(location)) {
			throw new IllegalArgumentException("location cannot be null, empty, or blank");
		}
		UrlValidator urlValidator = new UrlValidator();
		if(!urlValidator.isValid(location)) {
			throw new IllegalArgumentException("location is not a valid URL");
		}
		URL locationUrl;
		try {
			locationUrl = new URL(location);
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("location is not a valid URL", ex);
		}
		
		String locationUrlPath = locationUrl.getPath();
		if(StringUtils.isBlank(locationUrlPath)) {
			throw new IllegalArgumentException("location does not have a path component");
		}
		String[] pathSegments = locationUrlPath.split("/");
		String lastPathSegment = pathSegments[pathSegments.length-1];
		//guard against trailing slash in the path
		if(StringUtils.isBlank(lastPathSegment) && pathSegments.length > 1) {
			lastPathSegment = pathSegments[pathSegments.length-2];
		}
		return lastPathSegment;
	}
	
    /**
     * 
     * @param rootWebPath Root web path to sitemap file
     * @param key Name of sitemap file
     * @return URL to sitemap file with given name
     */
	public String getSitemapWebPath(String rootWebPath, String key) {
		return String.format("%ssitemap-%sx.xml", rootWebPath, key);
	}
	
    /**
     * 
     * @param rootPath Path to directory containing sitemap files
     * @param key Name of sitemap file
     * @return Path to sitemap file with given name
     */
	public String getSitemapFilePath(String rootPath, String key) {
		return String.format("%ssitemap-%sx.xml", rootPath, key);
	}
}
