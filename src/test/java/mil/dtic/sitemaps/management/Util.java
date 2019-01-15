package mil.dtic.sitemaps.management;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import mil.dtic.sitemaps.management.resources.domain.Sitemapindex;
import mil.dtic.sitemaps.management.resources.domain.TUrl;
import mil.dtic.sitemaps.management.resources.domain.Urlset;
import static org.junit.Assert.fail;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/**
 *
 */
public class Util {
	// Possibly should be passed into an object
	private static final String indexFilename = "sitemap.xml";

	public static String resourceToString(String resourcePath) throws IOException {
		File correctFile = new ClassPathResource(resourcePath).getFile();
		return new String(Files.readAllBytes(correctFile.toPath()));
	}
	
	/**
	 * I'm unsure if I should use DOM or just use Jackson to compare the XML so
	 * I'm creating this function to easily change it later if we change our mind.
	 * 
	 * Should really be parse to TUrl
	 * In hindsight, modifying IOUtility to use here might have been better.
	 * Might be a good thing to do in the future.
	 * @param xmlFile
	 * @return
	 * @throws IOException 
	 */
	public static List<TUrl> parseXmlFile(File xmlFile) throws IOException {
		Urlset urlset = null;
		if(xmlFile.exists() && !xmlFile.isDirectory()) {
			XmlMapper mapper = new XmlMapper();
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.setDateFormat(new StdDateFormat());
			urlset = mapper.readValue(xmlFile, Urlset.class);
		} else {
			fail("Didn't find XML files correctly.");
		}

		return urlset.getUrl();
	}

	public static class SitemapCollection {
		public Sitemapindex sitemapindex;
		public List<Sitemap> sitemaps;

		public SitemapCollection () {
			sitemaps = new ArrayList<>();
		}
	}

	public static class Sitemap {
		public String name;
		// Could use Urlset, although do we need to?
		public List<TUrl> urls;
	}

	public static Sitemapindex parseSitemapIndex(File file) throws IOException {
		Sitemapindex foundSitemapIndex = null;
		if (file.isFile()) {
			XmlMapper mapper = new XmlMapper();
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.setDateFormat(new StdDateFormat());
			foundSitemapIndex = mapper.readValue(file, Sitemapindex.class);
		} else {
			fail("Can't find sitemap index file.");
		}
		return foundSitemapIndex;
	}

	/**
	 * Possibly should use ObjectFactory?
	 * @param sitemapDirFile
	 * @return
	 * @throws IOException 
	 */
	public static SitemapCollection getAllSitemaps(File sitemapDirFile) throws IOException {
		SitemapCollection sitemaps = new SitemapCollection();
		//File sitemapDirFile = new File(sitemapDir);
		File[] sitemapFiles = sitemapDirFile.listFiles();
		for (File sitemapFile : sitemapFiles) {
			if (sitemapFile.isFile()) {
				if (!sitemapFile.getName().equals(indexFilename)) {
					Sitemap sitemap = new Sitemap();
					sitemap.name = sitemapFile.getName();
					sitemap.urls = Util.parseXmlFile(sitemapFile);
					sitemaps.sitemaps.add(sitemap);
				} else {
					sitemaps.sitemapindex = parseSitemapIndex(sitemapFile);
				}
			} else {
				// Possibly throw exception instead.
				fail("Problem getting sitemap file.");
			}
		}
		if (sitemaps.sitemapindex == null) {
			fail("No index file found.");
		}
		return sitemaps;
	}

	public static MockHttpServletRequestBuilder storedJsonRequest(HttpMethod method, String requestJsonPath) throws Exception {
		return request(method, "/")
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);
	}
}