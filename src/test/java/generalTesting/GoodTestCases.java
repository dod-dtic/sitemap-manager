package generalTesting;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
	private final String sitemapFile;
	
	public GoodTestCases() {
		sitemapFile = config.getRootPath() + "sitemap.xml";
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
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(config.getRootPath()));
	}

	@Test
	public void testPostEmptyList() throws Exception {
		mockMvc.perform(post("/sitemap-manager").content("{}").contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
			.andExpect(status().isCreated())
			.andExpect(content().contentType("text/plain;charset=UTF-8"))
			.andExpect(content().string("created"))
			;

		File correctFile = new ClassPathResource("goodSitemaps/emptySitemap.xml").getFile();
		String correctXmlString = new String(Files.readAllBytes(correctFile.toPath()));
		byte[] encoded = Files.readAllBytes(Paths.get(sitemapFile));
		String createdXmlString = new String(encoded, Charset.forName("utf-8"));
		assertEquals(correctXmlString, createdXmlString);
	}

	@Test
	public void testPostOneValidEntry() throws Exception {
	}
}