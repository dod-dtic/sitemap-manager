package mil.dtic.sitemaps.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import mil.dtic.sitemaps.management.Util.Sitemap;
import mil.dtic.sitemaps.management.Util.SitemapCollection;
import static mil.dtic.sitemaps.management.Util.getAllSitemaps;
import static mil.dtic.sitemaps.management.Util.resourceToString;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.domain.TUrl;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import static org.springframework.http.RequestEntity.method;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Make test names more consistant.
 */
@SpringBootTest(classes = SitemapManagerApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
public class SimpleApiTests {
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	@Autowired
	private SitemapManagerConfiguration config;
	private static final String simpleRequestDirName = "simpleRequests/";
	private static final String simpleEndpoint = "/sitemap-manager/simple";
	private static final String testResourceDirName = "simpleSiteMaps/";
	private SitemapAssertions sitemapAssertions;
	private final long allowableDelta = 10000;
	
	public SimpleApiTests() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() throws IOException {
		sitemapAssertions = new SitemapAssertions(config, 30000);
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		Files.createDirectories(Paths.get(config.getRootPath()));
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	private MockHttpServletRequestBuilder storedRequest(HttpMethod method, String requestPath) throws Exception {
		return request(method, simpleEndpoint)
			.content(resourceToString(requestPath))
			.contentType(MediaType.TEXT_PLAIN)
			.accept(MediaType.APPLICATION_JSON);
	}

	private void performGenericRequest (HttpMethod method, String requestPath) throws Exception {
		ResultMatcher expectedStatus;
		String expectedContent;
		if (null == method) {
			throw new Exception("performGenericRequest is currently only written for POST, PUT, and DELETE");
		} else switch (method) {
			case POST:
				expectedStatus = status().isCreated();
				expectedContent = "created";
				break;
			case PUT:
				expectedStatus = status().isOk();
				expectedContent = "updated";
				break;
			case DELETE:
				expectedStatus = status().isOk();
				expectedContent = "deleted";
				break;
			default:
				throw new Exception("performGenericRequest is currently only written for POST, PUT, and DELETE");
		}

		mockMvc.perform(storedRequest(method, requestPath))
			.andExpect(expectedStatus)
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(expectedContent));
	}

	/**
	 * @param requestJsonPath Should be the resource path rather than full paths
	 * @param expectedDirectoryPath resource path
	 */
	private void generalTest(HttpMethod method, String requestPath, String expectedDirectoryPath) throws Exception {
		performGenericRequest(method, requestPath);

		sitemapAssertions.compareSitemapCollections(getAllSitemaps(new ClassPathResource(expectedDirectoryPath).getFile()), getAllSitemaps(new File(config.getRootPath())));
	}

	/**
	 * Not sure if this should be "good" or "bad"
	 * Don't need a file for this one. Although, if the easy way to create
	 * the test requires one, then I suppose it would just be easier to have an empty file.
	 */
	//@Test
	public void testEmptyPost() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "empty.txt", testResourceDirName + "empty");
	}

	@Test 
	public void testSingleGoodPost() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
	}

	@Test
	public void testMultipleGoodPost() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
	}

	/**
	 * Might need to get the time off of the old one and compare to possibly updated new last modification.
	 * Probably should make a test like this in GoodTestCases for default last mod updates.
	 * Also, if a URL update makes a last mod set before the last mod its replacing is that okay?
	 */
	@Test
	public void testSingleGoodPostUpdate() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");

		SitemapCollection sitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap sitemap = sitemaps.sitemaps.get(0);
		TUrl tUrl = sitemap.urls.get(0);
		Date initialLastMod = tUrl.getLastmod();

		//TimeUnit.SECONDS.sleep(1);

		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
		SitemapCollection updatedSitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap updatedSitemap = updatedSitemaps.sitemaps.get(0);
		TUrl updatedTUrl = updatedSitemap.urls.get(0);
		Date updatedLastMod = updatedTUrl.getLastmod();

		assertTrue(updatedLastMod.getTime() > initialLastMod.getTime());
	}

	/**
	 * Might need to get the time off of the old one and compare to possibly updated new last modification.
	 */
	@Test
	public void testMultipleGoodPostUpdate() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");

		SitemapCollection sitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap sitemap = sitemaps.sitemaps.get(0);
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
		SitemapCollection updatedSitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap updatedSitemap = updatedSitemaps.sitemaps.get(0);

		Comparator<TUrl> comp = (expectedTUrl, actualTUrl) -> expectedTUrl.getLoc().compareTo(actualTUrl.getLoc());
		List<TUrl> ordInitial = new ArrayList<>(sitemap.urls);
		ordInitial.sort(comp);
		List<TUrl> ordUpdated = new ArrayList<>(updatedSitemap.urls);
		ordUpdated.sort(comp);
		assertEquals("Sitemap name: " + sitemap.name, ordInitial.size(), ordUpdated.size());

		
		for (int i=0; i<ordInitial.size(); i++) {
			assertTrue(ordInitial.get(i).getLastmod().getTime() < ordUpdated.get(i).getLastmod().getTime());
		}
	}

	@Test
	public void testSingleBadPost() throws Exception {
		mockMvc.perform(storedRequest(HttpMethod.POST, simpleRequestDirName + "singleBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void testMultipleBadPost() throws Exception {
		mockMvc.perform(storedRequest(HttpMethod.POST, simpleRequestDirName + "multipleBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	/**
	 * Mixed good and bad posts. Actually not sure exactly how it should operate.
	 * Possibly try having some updates in there as well.
	 */
	@Test
	public void testMultipleBadPostMixed() throws Exception {
		// Just there to create index.
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");

		mockMvc.perform(storedRequest(HttpMethod.POST, simpleRequestDirName + "mixedBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// Theoretically, this should fail if anything new has been created.
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
	}

	/**
	 * Not sure if this should be "good" or "bad"
	 * Don't need a file for this one.
	 */
	@Test
	public void testEmptyPut() throws Exception {
		generalTest(HttpMethod.PUT, simpleRequestDirName + "empty.txt", testResourceDirName + "empty");
	}

	@Test 
	public void testSingleGoodPut() throws Exception {
		generalTest(HttpMethod.PUT, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
	}

	@Test
	public void testMultipleGoodPut() throws Exception {
		generalTest(HttpMethod.PUT, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
	}

	/**
	 * Might need to get the time off of the old one and compare to possibly updated new last modification.
	 */
	@Test
	public void testSingleGoodPutUpdate() throws Exception {
		generalTest(HttpMethod.PUT, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");

		SitemapCollection sitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap sitemap = sitemaps.sitemaps.get(0);
		TUrl tUrl = sitemap.urls.get(0);
		Date initialLastMod = tUrl.getLastmod();

		//TimeUnit.SECONDS.sleep(1);

		generalTest(HttpMethod.PUT, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
		SitemapCollection updatedSitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap updatedSitemap = updatedSitemaps.sitemaps.get(0);
		TUrl updatedTUrl = updatedSitemap.urls.get(0);
		Date updatedLastMod = updatedTUrl.getLastmod();

		assertTrue(updatedLastMod.getTime() > initialLastMod.getTime());
	}

	/**
	 * Might need to get the time off of the old one and compare to possibly updated new last modification.
	 */
	@Test
	public void testMultipleGoodPutUpdate() throws Exception {
		generalTest(HttpMethod.PUT, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");

		SitemapCollection sitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap sitemap = sitemaps.sitemaps.get(0);
		generalTest(HttpMethod.PUT, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
		SitemapCollection updatedSitemaps = getAllSitemaps(new File(config.getRootPath()));
		Sitemap updatedSitemap = updatedSitemaps.sitemaps.get(0);

		Comparator<TUrl> comp = (expectedTUrl, actualTUrl) -> expectedTUrl.getLoc().compareTo(actualTUrl.getLoc());
		List<TUrl> ordInitial = new ArrayList<>(sitemap.urls);
		ordInitial.sort(comp);
		List<TUrl> ordUpdated = new ArrayList<>(updatedSitemap.urls);
		ordUpdated.sort(comp);
		assertEquals("Sitemap name: " + sitemap.name, ordInitial.size(), ordUpdated.size());

		
		for (int i=0; i<ordInitial.size(); i++) {
			assertTrue(ordInitial.get(i).getLastmod().getTime() < ordUpdated.get(i).getLastmod().getTime());
		}
	}

	@Test
	public void testSingleBadPut() throws Exception {
		mockMvc.perform(storedRequest(HttpMethod.PUT, simpleRequestDirName + "singleBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void testMultipleBadPut() throws Exception {
		mockMvc.perform(storedRequest(HttpMethod.PUT, simpleRequestDirName + "multipleBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	/**
	 * Mixed good and bad posts. Actually not sure exactly how it should operate.
	 * Possibly try having some updates in there as well.
	 */
	@Test
	public void testMultipleBadPutMixed() throws Exception {
		// Just there to create index.
		generalTest(HttpMethod.PUT, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");

		mockMvc.perform(storedRequest(HttpMethod.PUT, simpleRequestDirName + "mixedBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// Theoretically, this should fail if anything new has been created.
		generalTest(HttpMethod.PUT, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
	}

	/**
	 * Not sure if this should be "good" or "bad"
	 * Don't need a file for this one.
	 */
	@Test
	public void testEmptyDelete() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
		generalTest(HttpMethod.DELETE, simpleRequestDirName + "empty.txt", testResourceDirName + "singleGoodPost");
	}

	@Test
	public void testSingleGoodDelete() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");
		generalTest(HttpMethod.DELETE, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "empty");
	}

	@Test
	public void testMultipleGoodDelete() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
		generalTest(HttpMethod.DELETE, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "empty");
	}

	//@Test
	public void testSingleBadDelete() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "singleGoodUrl.txt", testResourceDirName + "singleGoodPost");

		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "singleBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// Checking to make sure the good sitemap is still there.
		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "singleGoodPost").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);

	}

	@Test
	public void testMultipleBadDelete() throws Exception {
		// Just there to create index.
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");

		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "mixedBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "multipleGood").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}

	/**
	 * Make delete and other test names more consistant.
	 */
	@Test
	public void testMultipleBadDeleteMixed() throws Exception {
		// Just there to create index.
		generalTest(HttpMethod.POST, simpleRequestDirName + "differentOverlappingMultipleGoodUrl.txt", testResourceDirName + "differentOverlappingMultipleGoodUrl");

		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "mixedBadUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "differentOverlappingMultipleGoodUrl").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}

	/**
	 * May not be needed depending on how delete works when making a sitemap empty.
	 */
	@Test
	public void testDeleteSubsetOfTotalUrls() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrl.txt", testResourceDirName + "multipleGood");
		generalTest(HttpMethod.DELETE, simpleRequestDirName + "multipleGoodUrlSubset.txt", testResourceDirName + "multipleGoodUrlWithoutSubset");
	}

	@Test
	public void testDeleteSingleNonexistant() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodDifferentUrl.txt", testResourceDirName + "multipleGoodDifferent");
		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "singleGoodUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "multipleGoodDifferent").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}

	@Test
	public void testDeleteMultipleNonexistant() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodDifferentUrl.txt", testResourceDirName + "multipleGoodDifferent");
		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "multipleGoodUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "multipleGoodDifferent").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}

	@Test
	public void testDeleteMultipleMixedNonexistant() throws Exception {
		generalTest(HttpMethod.POST, simpleRequestDirName + "multipleGoodUrlSubset.txt", testResourceDirName + "multipleGoodSubset");
		mockMvc.perform(storedRequest(HttpMethod.DELETE, simpleRequestDirName + "multipleGoodUrl.txt"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(testResourceDirName + "multipleGoodSubset").getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}
}