package mil.dtic.sitemaps.management.resources;

import java.io.Serializable;
import java.util.List;

public class IndexedLocationList implements Serializable {
	private static final long serialVersionUID = 914508014159074793L;

    private List<IndexedLocation> urls;

	public List<IndexedLocation> getUrls() {
		return urls;
	}

	public void setUrls(List<IndexedLocation> urls) {
		this.urls = urls;
	}
}
