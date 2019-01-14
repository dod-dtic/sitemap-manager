package generalTesting;

import static generalTesting.Util.getAllSitemaps;
import static generalTesting.Util.resourceToString;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * For the sitemap and sitemap index parsing I maybe should have just modified IOUtility
 * to allow for arbitrary directories.
 */
@SpringBootTest(classes = SitemapManagerApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
public class GoodTestCases {

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	@Autowired
	private SitemapManagerConfiguration config;
	private static final String testResourceDirName = "goodSiteMaps";
	private SitemapAssertions sitemapAssertions; 
	// Querying it outside of code you have to use "/sitemap-manager/sitemap-manager", however I guess doing it within the code gets rid of one of the "/sitemap-managers".
	private static final String basicEndpoint = "/sitemap-manager";
	
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
		sitemapAssertions = new SitemapAssertions(config, 30000);
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		Files.createDirectories(Paths.get(config.getRootPath()));
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	private void storedJsonPostRequest(String requestJsonPath) throws Exception {
	MockHttpServletRequestBuilder postRequest = request(HttpMethod.POST, basicEndpoint)
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));
	}

	/**
	 * @param requestJsonPath Should be the resource path rather than full paths
	 * @param expectedDirectoryPath resource path
	 */
	private void generalPostTest(String requestJsonPath, String expectedDirectoryPath) throws Exception {
		storedJsonPostRequest(requestJsonPath);

		sitemapAssertions.compareSitemapCollections(getAllSitemaps(new ClassPathResource(expectedDirectoryPath).getFile()), getAllSitemaps(new File(config.getRootPath())));
	}

	private void storedJsonPutRequest(String requestJsonPath) throws Exception {
		MockHttpServletRequestBuilder postRequest = request(HttpMethod.PUT, basicEndpoint)
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("updated"));
	}

	private void generalPutTest(String requestJsonPath, String expectedDirectoryPath) throws Exception {
		storedJsonPutRequest(requestJsonPath);

		sitemapAssertions.compareSitemapCollections(getAllSitemaps(new ClassPathResource(expectedDirectoryPath).getFile()), getAllSitemaps(new File(config.getRootPath())));
	}

	@Test
	public void testPostEmptyList() throws Exception {
		generalPostTest("goodRequestJson/emptyList.json", testResourceDirName + "/empty");
	}

	@Test
	public void testPostOneJustLocation() throws Exception {
		generalPostTest("goodRequestJson/justLocation.json", testResourceDirName + "/justLocation");
	}

	@Test
	public void testPostOneWithChangFrequency() throws Exception {
		generalPostTest("goodRequestJson/withChangeFrequency.json", testResourceDirName + "/withChangeFrequency");
	}

	@Test
	public void testPostOneWithName() throws Exception {
		generalPostTest("goodRequestJson/withName.json", testResourceDirName + "/withName");
	}

	@Test
	public void testPostOneWithPriority() throws Exception {
		generalPostTest("goodRequestJson/withPriority.json", testResourceDirName + "/withPriority");
	}

	@Test
	public void testPostOneWithAll() throws Exception {
		generalPostTest("goodRequestJson/withAll.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPostOneWithAllDifferentOrder() throws Exception {
		generalPostTest("goodRequestJson/withAllDifferentOrder.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPostMultipleAllFields() throws Exception {
		generalPostTest("goodRequestJson/multipleAllFields.json", testResourceDirName + "/multipleAllFields");
	}

	@Test
	public void testPostMultipleSomeDefaults() throws Exception {
		generalPostTest("goodRequestJson/multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
	}

	@Test
	public void testPostMultipleSomeDefaultsSameName() throws Exception {
		generalPostTest("goodRequestJson/multipleSomeDefaultsSameName.json", testResourceDirName + "/multipleSomeDefaultsSameName");
	}

	@Test
	public void testPostUpdate() throws Exception {
		generalPostTest("goodRequestJson/multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
		generalPostTest("goodRequestJson/multipleSomeDefaultsUpdate.json", testResourceDirName + "/multipleSomeDefaultsUpdate");
	}

	@Test
	public void testPutEmptyList() throws Exception {
		generalPutTest("goodRequestJson/emptyList.json", testResourceDirName + "/empty");
	}

	@Test
	public void testPutOneJustLocation() throws Exception {
		generalPutTest("goodRequestJson/justLocation.json", testResourceDirName + "/justLocation");
	}

	@Test
	public void testPutOneWithChangFrequency() throws Exception {
		generalPutTest("goodRequestJson/withChangeFrequency.json", testResourceDirName + "/withChangeFrequency");
	}

	@Test
	public void testPutOneWithName() throws Exception {
		generalPutTest("goodRequestJson/withName.json", testResourceDirName + "/withName");
	}

	@Test
	public void testPutOneWithPriority() throws Exception {
		generalPutTest("goodRequestJson/withPriority.json", testResourceDirName + "/withPriority");
	}

	@Test
	public void testPutOneWithAll() throws Exception {
		generalPutTest("goodRequestJson/withAll.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPutOneWithAllDifferentOrder() throws Exception {
		generalPutTest("goodRequestJson/withAllDifferentOrder.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPutMultipleAllFields() throws Exception {
		generalPutTest("goodRequestJson/multipleAllFields.json", testResourceDirName + "/multipleAllFields");
	}

	@Test
	public void testPutMultipleSomeDefaults() throws Exception {
		generalPutTest("goodRequestJson/multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
	}

	@Test
	public void testPutMultipleSomeDefaultsSameName() throws Exception {
		generalPutTest("goodRequestJson/multipleSomeDefaultsSameName.json", testResourceDirName + "/multipleSomeDefaultsSameName");
	}

	@Test
	public void testPutUpdate() throws Exception {
		generalPutTest("goodRequestJson/multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
		generalPutTest("goodRequestJson/multipleSomeDefaultsUpdate.json", testResourceDirName + "/multipleSomeDefaultsUpdate");
	}
}