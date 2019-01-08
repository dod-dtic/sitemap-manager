package mil.dtic.sitemaps.management.resources.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SitemapIndexKeyUtilityTest {

	@Autowired
	protected SitemapIndexKeyUtility sitemapIndexKeyUtility;
	
	private int keyLength;
	private String rootWebPath;
	
	private String nullString;
	private String emptyString;
	private String blankString;
	private String malformedUrl;
	
	private String testName1;
	private String testLocation1;
	private String testExpectedKey1;
	private String testExpectedName1;
	private String testExpectedWebPath1;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void before() {
		keyLength = 5;
		rootWebPath = "http://www.dtic.mil/";

		nullString = null;
		emptyString = "";
		blankString = "    ";
		malformedUrl = "/hptps:/w.12233.co.uk";
		
		testName1 = "AD0245212";
		testLocation1 = "https://apps.dtic.mil/docs/citations/AD0245212/";
		testExpectedName1 = "AD0245212";
		testExpectedKey1 = "AD024";
		testExpectedWebPath1 = "http://www.dtic.mil/sitemap-AD024x.xml";
	}
	
	@Test
	public void testDetermineIndexKeyIntString() {
		String actualKey1 = sitemapIndexKeyUtility.determineIndexKey(keyLength, testName1);
		assertEquals("key did not match expected value", testExpectedKey1, actualKey1);
	}
	
	@Test
	public void testDetermineIndexKeyIntString_NegativeKeyLength() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("keyLength must be greater than zero");
		sitemapIndexKeyUtility.determineIndexKey(-1, testName1);
	}
	@Test
	public void testDetermineIndexKeyIntString_ZeroKeyLength() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("keyLength must be greater than zero");
		sitemapIndexKeyUtility.determineIndexKey(0, testName1);
	}
	@Test
	public void testDetermineIndexKeyIntString_NullName() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("name cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineIndexKey(keyLength, nullString);
	}
	@Test
	public void testDetermineIndexKeyIntString_EmptyName() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("name cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineIndexKey(keyLength, emptyString);
	}
	@Test
	public void testDetermineIndexKeyIntString_BlankName() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("name cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineIndexKey(keyLength, blankString);
	}

	@Test
	public void testDetermineIndexKeyIntStringString() {
		String actualKey1 = sitemapIndexKeyUtility.determineIndexKey(keyLength, testName1, testLocation1);
		assertEquals("key did not match expected value", testExpectedKey1, actualKey1);
	}
	@Test
	public void testDetermineIndexKeyIntStringString_NegativeKeyLength() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("keyLength must be greater than zero");
		sitemapIndexKeyUtility.determineIndexKey(-1, testName1, testLocation1);
	}
	@Test
	public void testDetermineIndexKeyIntStringString_ZeroKeyLength() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("keyLength must be greater than zero");
		sitemapIndexKeyUtility.determineIndexKey(0, testName1, testLocation1);
	}
	@Test
	public void testDetermineIndexKeyIntStringString_NullName() {
		String actualKey1 = sitemapIndexKeyUtility.determineIndexKey(keyLength, nullString, testLocation1);
		assertEquals("key did not match expected value", testExpectedKey1, actualKey1);
	}
	@Test
	public void testDetermineIndexKeyIntStringString_EmptyName() {
		String actualKey1 = sitemapIndexKeyUtility.determineIndexKey(keyLength, emptyString, testLocation1);
		assertEquals("key did not match expected value", testExpectedKey1, actualKey1);
	}
	@Test
	public void testDetermineIndexKeyIntStringString_BlankName() {
		String actualKey1 = sitemapIndexKeyUtility.determineIndexKey(keyLength, blankString, testLocation1);
		assertEquals("key did not match expected value", testExpectedKey1, actualKey1);
	}

	@Test
	public void testDetermineName() {
		String actualName = sitemapIndexKeyUtility.determineName(testLocation1);
		assertEquals("name did not match expected value", testExpectedName1, actualName);
	}
	@Test
	public void testDetermineName_NullLocation() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("location cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineName(nullString);
	}
	@Test
	public void testDetermineName_EmptyLocation() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("location cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineName(emptyString);
	}
	@Test
	public void testDetermineName_BlankLocation() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("location cannot be null, empty, or blank");
		sitemapIndexKeyUtility.determineName(blankString);
	}
	@Test
	public void testDetermineName_MalformedUrl() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("location is not a valid URL");
		sitemapIndexKeyUtility.determineName(malformedUrl);
	}

	@Test
	public void testGetSitemapWebPath() {
		String actualWebPath = sitemapIndexKeyUtility.getSitemapWebPath(rootWebPath, testExpectedKey1);
		assertEquals("web path did not match expected value", testExpectedWebPath1, actualWebPath);
	}

}
