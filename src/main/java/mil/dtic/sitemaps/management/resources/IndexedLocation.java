package mil.dtic.sitemaps.management.resources;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import mil.dtic.sitemaps.management.resources.domain.TChangeFreq;

/**
 * Location entry in sitemap file
 * @author Battelle
 */
public class IndexedLocation implements Serializable {
    private static final long serialVersionUID = 7735986239340177169L;
    
    private String location;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastModified;
    private TChangeFreq changeFrequency;
    private BigDecimal priority;
    
    /**
     * 
     * @return Location (URL)
     */
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
    /**
     * 
     * @return Name of sitemap containing location entry
     */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
    /**
     * 
     * @return Date contents of location last modified
     */
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
    /**
     * 
     * @return Frequency with which contents of location changes
     */
	public TChangeFreq getChangeFrequency() {
		return changeFrequency;
	}
	public void setChangeFrequency(TChangeFreq changeFrequency) {
		this.changeFrequency = changeFrequency;
	}
	
    /**
     * 
     * @return Importance of location
     */
	public BigDecimal getPriority() {
		return priority;
	}
	public void setPriority(BigDecimal priority) {
		this.priority = priority;
	}

    
}