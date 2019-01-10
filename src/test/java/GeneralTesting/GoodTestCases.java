/*
 *  
 */
package GeneralTesting;

import java.time.Instant;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 */
@SpringBootTest(classes = SitemapManagerApplication.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
//@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
//@WebAppConfiguration
//@ContextConfiguration("my-servlet-context.xml")
public class GoodTestCases {

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	
	public GoodTestCases() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void testPostEmptyList() {

	}

	@Test
	public void testPostOneValidEntry() throws Exception {
		mockMvc.perform(post("/sitemap-manager").content("{}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
		//mockMvc.perform(post("/sitemap-manager", "").accept(MediaType.TEXT_PLAIN))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			//.andExpect(jsonPath("$.timestamp").value(String.valueOf(Instant.now().getEpochSecond())))
			//.andExpect(jsonPath("$.status").value("400"))
			//.andExpect(jsonPath("$.error").value("Bad Request"))
			//.andExpect(jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
			//.andExpect(jsonPath("$.message").value("Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> mil.dtic.sitemaps.management.controller.SitemapManagerController.addLocations(mil.dtic.sitemaps.management.resources.IndexedLocationList)"))
			//.andExpect(jsonPath("$.path").value("/sitemap-manager"))
			.andExpect(content().string("created"))
			;
	}

	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	// @Test
	// public void hello() {}
}