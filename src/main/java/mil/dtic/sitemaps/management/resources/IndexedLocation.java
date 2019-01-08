package mil.dtic.sitemaps.management.resources;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import mil.dtic.sitemaps.management.resources.domain.TChangeFreq;

public class IndexedLocation implements Serializable {
    private static final long serialVersionUID = 7735986239340177169L;
    
    private String location;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastModified;
    private TChangeFreq changeFrequency;
    private BigDecimal priority;
    
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public TChangeFreq getChangeFrequency() {
		return changeFrequency;
	}
	public void setChangeFrequency(TChangeFreq changeFrequency) {
		this.changeFrequency = changeFrequency;
	}
	
	public BigDecimal getPriority() {
		return priority;
	}
	public void setPriority(BigDecimal priority) {
		this.priority = priority;
	}

    
}