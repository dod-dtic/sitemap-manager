package generalTesting;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
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
	private String sitemapFile;
	
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
		sitemapFile = config.getRootPath() + "sitemap-AD100x.xml";
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
	 * I'm unsure if I should use DOM or just use Jackson to compare the XML so
	 * I'm creating this function to easily change it later if we change our mind.
	 * @param expectedResourcePath
	 * @param actualXmlPath 
	 */
	private void compareXmlFiles(String expectedResourcePath, String actualXmlPath) throws IOException {
		/*
		String correctXmlString = resourceToString(expectedResourcePath);
		String createdXmlString = fileToString(actualXmlPath);
		assertEquals(correctXmlString, createdXmlString);
		*/
		
		File expectedXmlFile = new ClassPathResource(expectedResourcePath).getFile();
		File actualXmlFile = new File(actualXmlPath);
		if(expectedXmlFile.exists() && !expectedXmlFile.isDirectory() && actualXmlFile.exists() && !actualXmlFile.isDirectory()) {
			XmlMapper mapper = new XmlMapper();
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.setDateFormat(new StdDateFormat());
			Urlset expectedUrlset = mapper.readValue(expectedXmlFile, Urlset.class);
			Urlset actualUrlset = mapper.readValue(actualXmlFile, Urlset.class);

			TUrl expectedTUrl = expectedUrlset.getUrl().get(0);
			TUrl actualTUrl = actualUrlset.getUrl().get(0);

			//assertEquals(expectedTUrl.getLastmod().getTime(), actualTUrl.getLastmod().getTime(), 1);// 60000
			assertEquals(expectedTUrl.getLoc(), actualTUrl.getLoc());
		} else {
			fail("Didn't find XML files correctly.");
		}
	}

	/**
	 * 
	 * @param requestJsonPath Should be the resource path rather than full paths
	 * @param expectedSitemapPath resource path
	 * @param actualSitemapPath Full path
	 */
	private void generalTest(String requestJsonPath, String expectedSitemapPath, String actualSitemapPath) throws Exception {
		MockHttpServletRequestBuilder postRequest = post("/sitemap-manager")
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));

		compareXmlFiles(expectedSitemapPath, actualSitemapPath);
	}

	@Test
	public void testPostEmptyList() throws Exception {
		generalTest("requestJson/emptyList.json", "goodSitemaps/emptySitemap.xml", sitemapFile);
	}

	@Test
	public void testPostOneValidEntry() throws Exception {
		/*
		String locationJson = resourceToString("requestJson/justLocation.json");

		MockHttpServletRequestBuilder postRequest = post("/sitemap-manager")
			.content(locationJson)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));

		String createdXmlString = fileToString(sitemapFile);
		System.out.println(createdXmlString);
*/
		generalTest("requestJson/justLocation.json", "goodSitemaps/justLocationSitemap.xml", sitemapFile);
	}
}