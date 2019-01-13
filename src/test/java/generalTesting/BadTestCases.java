package generalTesting;

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
import static org.junit.Assert.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

	private String resourceToString(String resourcePath) throws IOException {
		File correctFile = new ClassPathResource(resourcePath).getFile();
		return new String(Files.readAllBytes(correctFile.toPath()));
	}

	private MockHttpServletRequestBuilder storedJsonRequest(HttpMethod method, String requestJsonPath) throws Exception {
		System.out.println(requestJsonPath);
		System.out.println(resourceToString(requestJsonPath));
		return request(method, "/sitemap-manager")
			.content(resourceToString(requestJsonPath))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);
	}

	/**
	 * TODO check that a sitemap file and index weren't created.
	 * This should return a 400 response rather than an unhandled exception and 500 response.
	 * Might be handled better with newer changes.
	 * @throws Exception 
	 */
	@Test
	public void testPostMissinglocation() throws Exception {
		MockHttpServletRequestBuilder foo = storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "missingLocation.json");
		mockMvc.perform(foo)
		//mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "missingLocation.json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}

	@Test
	public void testPostBadChangeFrequency() throws Exception {
		ResultActions foo = mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "badChangeFrequency.json"));

			foo.andExpect(status().isBadRequest())
			.andDo(print())
			//.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}

	/**
	 * If sitemap manager is supposed to allow duplicate URLs/locations then this
	 * might actually not be a "bad" request. Change to "good" test if that's the case.
	 * @throws Exception 
	 */
	@Test
	public void testPostDuplicateLocationsInRequest() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "duplicateLocationsInRequest.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}

	@Test
	public void testPostFutureLastModified() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "futureLastModified.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}

	@Test
	public void testPostInvalidUrl() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "invalidUrl.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}

	@Test
	public void testOutOfRangePriority() throws Exception {
		mockMvc.perform(storedJsonRequest(HttpMethod.POST, badRequestJsonDir + "outOfRangePriority.json"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.error").value("Bad Request"))
			.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			.andExpect(jsonPath("$.path").value("/sitemap-manager"));
	}
}