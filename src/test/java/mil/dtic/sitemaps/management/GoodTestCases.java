package mil.dtic.sitemaps.management;

import static mil.dtic.sitemaps.management.Util.getAllSitemaps;
import static mil.dtic.sitemaps.management.Util.resourceToString;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static mil.dtic.sitemaps.management.Util.storedJsonRequest;
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
import org.springframework.test.web.servlet.ResultActions;
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
	private static final String basicEndpoint = "/";
	private static final String requestDirPath = "goodRequestJson/";
	
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

	private void storedJsonDeleteRequest(String requestJsonPath) throws Exception {
		MockHttpServletRequestBuilder postRequest = request(HttpMethod.DELETE, "/sitemap-manager")
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("updated"));

		// Need to check and make sure the files are deleted.
	}

	private static void deleteExpects(ResultActions response) throws Exception {
		response.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("deleted"));
	}

	private void generalDeleteTest(String postRequestPath, String postExpectedPath, String deleteRequestPath, String deleteExpectedPath) throws Exception {
		// Create
		generalPostTest(postRequestPath, postExpectedPath);
		// Delete
		deleteExpects(mockMvc.perform(storedJsonRequest(HttpMethod.DELETE, deleteRequestPath)));
		// Check
		sitemapAssertions.compareSitemapCollections(
			getAllSitemaps(new ClassPathResource(deleteExpectedPath).getFile()),
			getAllSitemaps(new File(config.getRootPath()))
		);
	}

	@Test
	public void testPostEmptyList() throws Exception {
		generalPostTest(requestDirPath + "emptyList.json", testResourceDirName + "/empty");
	}

	@Test
	public void testPostOneJustLocation() throws Exception {
		generalPostTest(requestDirPath + "justLocation.json", testResourceDirName + "/justLocation");
	}

	@Test
	public void testPostOneWithChangFrequency() throws Exception {
		generalPostTest(requestDirPath + "withChangeFrequency.json", testResourceDirName + "/withChangeFrequency");
	}

	@Test
	public void testPostOneWithName() throws Exception {
		generalPostTest(requestDirPath + "withName.json", testResourceDirName + "/withName");
	}

	@Test
	public void testPostOneWithPriority() throws Exception {
		generalPostTest(requestDirPath + "withPriority.json", testResourceDirName + "/withPriority");
	}

	@Test
	public void testPostOneWithAll() throws Exception {
		generalPostTest(requestDirPath + "withAll.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPostOneWithAllDifferentOrder() throws Exception {
		generalPostTest(requestDirPath + "withAllDifferentOrder.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPostMultipleAllFields() throws Exception {
		generalPostTest(requestDirPath + "multipleAllFields.json", testResourceDirName + "/multipleAllFields");
	}

	@Test
	public void testPostMultipleSomeDefaults() throws Exception {
		generalPostTest(requestDirPath + "multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
	}

	@Test
	public void testPostMultipleSomeDefaultsSameName() throws Exception {
		generalPostTest(requestDirPath + "multipleSomeDefaultsSameName.json", testResourceDirName + "/multipleSomeDefaultsSameName");
	}

	@Test
	public void testPostUpdate() throws Exception {
		generalPostTest(requestDirPath + "multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
		generalPostTest(requestDirPath + "multipleSomeDefaultsUpdate.json", testResourceDirName + "/multipleSomeDefaultsUpdate");
	}

	@Test
	public void testPutEmptyList() throws Exception {
		generalPutTest(requestDirPath + "emptyList.json", testResourceDirName + "/empty");
	}

	@Test
	public void testPutOneJustLocation() throws Exception {
		generalPutTest(requestDirPath + "justLocation.json", testResourceDirName + "/justLocation");
	}

	@Test
	public void testPutOneWithChangFrequency() throws Exception {
		generalPutTest(requestDirPath + "withChangeFrequency.json", testResourceDirName + "/withChangeFrequency");
	}

	@Test
	public void testPutOneWithName() throws Exception {
		generalPutTest(requestDirPath + "withName.json", testResourceDirName + "/withName");
	}

	@Test
	public void testPutOneWithPriority() throws Exception {
		generalPutTest(requestDirPath + "withPriority.json", testResourceDirName + "/withPriority");
	}

	@Test
	public void testPutOneWithAll() throws Exception {
		generalPutTest(requestDirPath + "withAll.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPutOneWithAllDifferentOrder() throws Exception {
		generalPutTest(requestDirPath + "withAllDifferentOrder.json", testResourceDirName + "/withAll");
	}

	@Test
	public void testPutMultipleAllFields() throws Exception {
		generalPutTest(requestDirPath + "multipleAllFields.json", testResourceDirName + "/multipleAllFields");
	}

	@Test
	public void testPutMultipleSomeDefaults() throws Exception {
		generalPutTest(requestDirPath + "multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
	}

	@Test
	public void testPutMultipleSomeDefaultsSameName() throws Exception {
		generalPutTest(requestDirPath + "multipleSomeDefaultsSameName.json", testResourceDirName + "/multipleSomeDefaultsSameName");
	}

	@Test
	public void testPutUpdate() throws Exception {
		generalPutTest(requestDirPath + "multipleSomeDefaults.json", testResourceDirName + "/multipleSomeDefaults");
		generalPutTest(requestDirPath + "multipleSomeDefaultsUpdate.json", testResourceDirName + "/multipleSomeDefaultsUpdate");
	}

	@Test
	public void testDeleteEmpty() throws Exception {
		generalDeleteTest(
			requestDirPath + "emptyList.json",
			testResourceDirName + "/empty",
			requestDirPath + "deleteEmpty.json",
			testResourceDirName + "/empty"
		);
	}

	@Test
	public void testDeleteWithLocation() throws Exception {
		generalDeleteTest(
			requestDirPath + "justLocation.json",
			testResourceDirName + "/justLocation",
			requestDirPath + "deleteWithLocation.json",
			testResourceDirName + "/empty"
		);
	}

	@Test
	public void testDeleteWithName() throws Exception {
		generalDeleteTest(
			requestDirPath + "withName.json",
			testResourceDirName + "/withName",
			requestDirPath + "deleteWithName.json",
			testResourceDirName + "/empty"
		);
	}

	/**
	 * If sitemaps files are deleted need to test that files with multiple
	 * URLs can have individual URLs deleted but not whole files.
	 * @throws Exception 
	 */
	@Test
	public void testDeleteMultiple() throws Exception {
		generalDeleteTest(
			requestDirPath + "multipleSomeDefaults.json",
			testResourceDirName + "/multipleSomeDefaults",
			requestDirPath + "deleteMultiple.json",
			testResourceDirName + "/empty"
		);
	}

	// Need another test for deleting some files as well as another for deleting some Urls but not whole file.

	@Test
	public void testDeleteSingleInMultipleFile() throws Exception {
		generalDeleteTest(
			requestDirPath + "multipleSomeDefaults.json",
			testResourceDirName + "/multipleSomeDefaults",
			requestDirPath + "deleteWithName.json",
			testResourceDirName + "/deleteSingleInMultipleFile"
		);
	}

	@Test
	public void testDeleteMultipleSubsets() throws Exception {
		generalDeleteTest(
			requestDirPath + "multipleAcrossMultipleFiles.json",
			testResourceDirName + "/multipleAcrossMultipleFiles",
			requestDirPath + "deleteMultipleAcrossMultipleFiles.json",
			testResourceDirName + "/deleteMultipleAcrossMultipleFiles"
		);
	}
}