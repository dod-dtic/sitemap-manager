package mil.dtic.sitemaps.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static mil.dtic.sitemaps.management.Util.resourceToString;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static mil.dtic.sitemaps.management.Util.storedJsonRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@SpringBootTest(classes = SitemapManagerApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
public class BadTestCases {
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	@Autowired
	private SitemapManagerConfiguration config;
	private static final String badRequestJsonDir = "badRequestJson/";
	private static final String basicEndpoint = "/";
	
	public BadTestCases() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() throws IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		Files.createDirectories(Paths.get(config.getRootPath()));
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	/**
	 * TODO check that a sitemap file and index weren't created.
	 * This should return a 400 response rather than an unhandled exception and 500 response.
	 * Might be handled better with newer changes.
	 * @throws Exception 
	 */
	@Test
	public void testPostMissinglocation() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "missingLocation.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPostBadChangeFrequency() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "badChangeFrequency.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	/**
	 * If sitemap manager is supposed to allow duplicate URLs/locations then this
	 * might actually not be a "bad" request. Change to "good" test if that's the case.
	 * @throws Exception 
	 */
	@Test
	public void testPostDuplicateLocationsInRequest() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "duplicateLocationsInRequest.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPostFutureLastModified() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "futureLastModified.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPostInvalidUrl() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "invalidUrl.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPostOutOfRangePriority() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "outOfRangePriority.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	/**
	 * TODO check that a sitemap file and index weren't created.
	 * This should return a 400 response rather than an unhandled exception and 500 response.
	 * Might be handled better with newer changes.
	 * @throws Exception 
	 */
	@Test
	public void testPutMissinglocation() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "missingLocation.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPutBadChangeFrequency() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "badChangeFrequency.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	/**
	 * If sitemap manager is supposed to allow duplicate URLs/locations then this
	 * might actually not be a "bad" request. Change to "good" test if that's the case.
	 * @throws Exception 
	 */
	@Test
	public void testPutDuplicateLocationsInRequest() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "duplicateLocationsInRequest.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPutFutureLastModified() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "futureLastModified.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPutInvalidUrl() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "invalidUrl.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	@Test
	public void testPutOutOfRangePriority() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.PUT, badRequestJsonDir + "outOfRangePriority.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.timestamp").value(new CloseToNowMatcher()))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.path").value(basicEndpoint));
	}

	//empty entry
	@Test
	public void testDeleteWithEmptyEntry() throws Exception {
		// Create
		MockHttpServletRequestBuilder postRequest = request(HttpMethod.POST, basicEndpoint)
			.content(resourceToString("goodRequestJson/withName.json"))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));

		// Delete and check
		mockMvc.perform(storedJsonRequest(HttpMethod.DELETE, badRequestJsonDir + "deleteEmptyEntry.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}

	/**
	 * Not sure if this should just delete the sitemap file or error. Could be a "good" test
	 * if it's supposed to delete the sitemap file.
	 * @throws Exception 
	 */
	@Test
	public void testDeleteWithOnlyName() throws Exception {
		// Create
		MockHttpServletRequestBuilder postRequest = request(HttpMethod.POST, basicEndpoint)
			.content(resourceToString("goodRequestJson/withName.json"))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_PLAIN);

		mockMvc.perform(postRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"));

		// Delete and check
		mockMvc.perform(storedJsonRequest(HttpMethod.DELETE, badRequestJsonDir + "deleteWithOnlyName.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
}