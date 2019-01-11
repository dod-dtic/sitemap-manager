package generalTesting;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Properties;
import mil.dtic.sitemaps.management.SitemapManagerApplication;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
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
//@TestPropertySource(locations = "classpath:../../resources/test-application.properties")
@TestPropertySource(locations = "classpath:test-application.properties")
public class GoodTestCases {

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	@Autowired
	private SitemapManagerConfiguration config;
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
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
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		//Properties props = System.getProperties();
		//props.setProperty("java.io.tmpdir", config.getRootPath());
		//testFolder.newFile("touch.txt");
		//FileUtils.cleanDirectory(config.getRootPath());
		Files.createDirectories(Paths.get(config.getRootPath()));
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	@Test
	public void testPostEmptyList() throws Exception {
		mockMvc.perform(post("/sitemap-manager").content("{}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
		//mockMvc.perform(post("/sitemap-manager", "").accept(MediaType.TEXT_PLAIN))
			//.andDo(print())
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

		File correctFile = new ClassPathResource("goodSitemaps/emptySitemap.xml").getFile();
		System.out.println(System.getProperty("java.io.tmpdir"));
		String correctXmlString = new String(Files.readAllBytes(correctFile.toPath()));
		byte[] encoded = Files.readAllBytes(Paths.get(config.getRootPath()));
		//String createdXmlString = new String(encoded, Charset.forName("utf-8"));
		//assertEquals(correctXmlString, createdXmlString);
	}

	@Test
	public void testPostOneValidEntry() throws Exception {
	}
}