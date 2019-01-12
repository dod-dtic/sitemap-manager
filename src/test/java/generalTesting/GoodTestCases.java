package generalTesting;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.domain.Sitemapindex;
import mil.dtic.sitemaps.management.resources.domain.TUrl;
import mil.dtic.sitemaps.management.resources.domain.Urlset;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = SitemapManagerApplication.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
//@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
//@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class GoodTestCases {

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	@Autowired
	private SitemapManagerConfiguration config;
	private String sitemapIndexFile;
	private String defaultSitemapName;
	private static final String testResourceDirName = "goodSiteMaps";
	
	public GoodTestCases() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() throws IOException {
		sitemapIndexFile = config.getRootPath() + "sitemap.xml";
		defaultSitemapName = config.getRootPath() + "sitemap-AD100x.xml";
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		Files.createDirectories(Paths.get(config.getRootPath()));
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	private String resourceToString(String resourcePath) throws IOException {
		File correctFile = new ClassPathResource(resourcePath).getFile();
		return new String(Files.readAllBytes(correctFile.toPath()));
	}

	private String fileToString(String filePath) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
		return new String(encoded, Charset.forName("utf-8"));
	}

	/**
	 * @param expectedResourcePath
	 * @param actualXmlPath 
	 */
	private void compareTUrls(TUrl expectedTUrl, TUrl actualTUrl) throws IOException {
		assertEquals(expectedTUrl.getLoc(), actualTUrl.getLoc());

		if (expectedTUrl.getLastmod() != null) {
			assertEquals(expectedTUrl.getLastmod(), actualTUrl.getLastmod());
		} else {
			long allowableDelta = 30000;
			long now = new Date().getTime();
			long createdTime = actualTUrl.getLastmod().getTime();
			// Checking that createdTime isn't after now shouldn't be necessary but can be done if wanted.
			assertTrue((now - allowableDelta) < createdTime);
		}

		// Since the defaults depend on teh test-application.properties, can't put static values in expected XMLs.
		if (expectedTUrl.getChangefreq() != null) {
			assertEquals(expectedTUrl.getChangefreq(), actualTUrl.getChangefreq());
		} else {
			assertEquals(config.getDefaultChangeFrequency(), actualTUrl.getChangefreq());
		}

		if (expectedTUrl.getPriority() != null) {
			assertEquals(expectedTUrl.getPriority(), actualTUrl.getPriority());
		} else {
			assertEquals(config.getDefaultPriority(), actualTUrl.getPriority());
		}
	}

	/**
	 * I'm unsure if I should use DOM or just use Jackson to compare the XML so
	 * I'm creating this function to easily change it later if we change our mind.
	 * @param xmlFile
	 * @return
	 * @throws IOException 
	 */
	private List<TUrl> parseXmlFile(File xmlFile) throws IOException {
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

	private class SitemapCollection {
		public Sitemapindex sitemapindex;
		public List<Sitemap> sitemaps;
	}

	private class Sitemap {
		public String name;
		public List<TUrl> urls;

		/*
		public TUrlWithName(String name, TUrl sitemap) {
			this.name = name;
			this.sitemap = sitemap;
		}
		*/
	}

	private Sitemapindex parseSitemapIndex(File file) {
		// Stub
	}

	/**
	 * Possibly should use ObjectFactory?
	 * @param sitemapDirFile
	 * @return
	 * @throws IOException 
	 */
	private SitemapCollection getAllSitemaps(File sitemapDirFile) throws IOException {
		SitemapCollection sitemaps = new SitemapCollection();
		//File sitemapDirFile = new File(sitemapDir);
		File[] sitemapFiles = sitemapDirFile.listFiles();
		for (File sitemapFile : sitemapFiles) {
			if (sitemapFile.isFile()) {
				if (sitemapFile.getName() != "sitemap.xml") {
					Sitemap sitemap = new Sitemap();
					sitemap.name = sitemapFile.getName();
					sitemap.urls = parseXmlFile(sitemapFile);
					sitemaps.sitemaps.add(sitemap);
				} else {
					sitemaps.sitemapindex = parseSitemapIndex(sitemapFile);
				}
			} else {
				// Possibly throw exception instead.
				fail("Problem getting sitemap file.");
			}
		}
		return sitemaps;
	}

	/**
	 * TODO need to figure out how to test file names. Need to figure out what determines default file names.
	 * TODO need to test the sitemap index
	 * @param requestJsonPath Should be the resource path rather than full paths
	 * @param expectedSitemapPath resource path
	 * @param actualSitemapPath Full path
	 */
	private void generalTest(String requestJsonPath, String expectedDirectoryPath) throws Exception {
		MockHttpServletRequestBuilder postRequest = post("/sitemap-manager")
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));


		List<TUrl> expectedTUrls = parseXmlFile(new ClassPathResource(expectedSitemapPath).getFile());
		List<TUrl> actualTUrls = parseXmlFile(new File(actualSitemapPath));
		compareTUrls(expectedTUrls.get(0), actualTUrls.get(0));
	}

	@Test
	public void testPostEmptyList() throws Exception {
		generalTest("requestJson/emptyList.json", testResourceDirName + "/empty", defaultSitemapName);
	}

	@Test
	public void testPostOneValidEntry() throws Exception {
		generalTest("requestJson/justLocation.json", testResourceDirName + "/justLocation", defaultSitemapName);
	}
}