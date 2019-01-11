package generalTesting;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BadTestCases {
	
	public BadTestCases() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	@Test
	public void testMissinglocation() {
	}

	// If we make a test for empty body then this code might be useful.
		//mockMvc.perform(post("/sitemap-manager", "").accept(MediaType.TEXT_PLAIN))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			//.andExpect(jsonPath("$.status").value("400"))
			//.andExpect(jsonPath("$.error").value("Bad Request"))
			//.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			//.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			//.andExpect(jsonPath("$.path").value("/sitemap-manager"))
}
