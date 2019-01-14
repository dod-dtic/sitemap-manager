package mil.dtic.sitemaps.management.integrationTests;

import mil.dtic.sitemaps.management.integrationTests.Util.Sitemap;
import mil.dtic.sitemaps.management.integrationTests.Util.SitemapCollection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import mil.dtic.sitemaps.management.resources.domain.Sitemapindex;
import mil.dtic.sitemaps.management.resources.domain.TSitemap;
import mil.dtic.sitemaps.management.resources.domain.TUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 *
 */
public class SitemapAssertions {
	private final long allowableDelta;
	private SitemapManagerConfiguration config;

	public SitemapAssertions(SitemapManagerConfiguration config, long allowableDelta) {
		this.allowableDelta = allowableDelta;
		this.config = config;
	}
	/**
	 * @param expectedResourcePath
	 * @param actualXmlPath 
	 */
	public void compareTUrls(TUrl expectedTUrl, TUrl actualTUrl) throws IOException {
		assertEquals(expectedTUrl.getLoc(), actualTUrl.getLoc());

		if (expectedTUrl.getLastmod() != null) {
			assertEquals(expectedTUrl.getLastmod(), actualTUrl.getLastmod());
		} else {
			long now = new Date().getTime();
			long createdTime = actualTUrl.getLastmod().getTime();
			// Checking that createdTime isn't after now shouldn't be necessary but can be done if wanted.
			assertTrue((now - allowableDelta) < createdTime);
		}

		// Since the defaults depend on teh test-application.properties, can't put static values in expected XMLs.
		if (expectedTUrl.getChangefreq() != null) {
			assertEquals(expectedTUrl.getChangefreq(), actualTUrl.getChangefreq());
		} else {
			assertEquals(config.getDefaultChangeFrequency(), actualTUrl.getChangefreq());
		}

		if (expectedTUrl.getPriority() != null) {
			assertEquals("For URL: " + actualTUrl.getLoc(), expectedTUrl.getPriority(), actualTUrl.getPriority());
		} else {
			assertEquals("For URL: " + actualTUrl.getLoc(), config.getDefaultPriority(), actualTUrl.getPriority());
		}
	}

	/**
	 * The comparison could be done so much better. Just want to get it done though.
	 * Improve later.
	 * @param expected
	 * @param actual 
	 */
	public void compareSitemaps(Util.Sitemap expected, Util.Sitemap actual) throws IOException {
		assertEquals(expected.name, actual.name);

		Comparator<TUrl> comp = (expectedTUrl, actualTUrl) -> expectedTUrl.getLoc().compareTo(actualTUrl.getLoc());
		List<TUrl> ordExpected = new ArrayList<>(expected.urls);
		ordExpected.sort(comp);
		List<TUrl> ordActual = new ArrayList<>(actual.urls);
		ordActual.sort(comp);
		assertEquals("Sitemap name: " + actual.name, ordExpected.size(), ordActual.size());
		
		for (int i=0; i<ordExpected.size(); i++) {
			this.compareTUrls(ordExpected.get(i), ordActual.get(i));
		}
	}

	/**
	 * If I could figure out how to pass the stuff in the for loop in as a
	 * lambda to some function I could simplify several functions a lot.
	 * @param expected
	 * @param actual 
	 */
	public void compareSitemapIndices(Sitemapindex expected, Sitemapindex actual) {
		Comparator<TSitemap> comp = (expectedTSitemap, actualTSitemap) -> expectedTSitemap.getLoc().compareTo(actualTSitemap.getLoc());
		List<TSitemap> ordExpected = new ArrayList<>(expected.getSitemap());
		ordExpected.sort(comp);
		List<TSitemap> ordActual = new ArrayList<>(actual.getSitemap());
		ordActual.sort(comp);
		assertEquals(ordExpected.size(), ordActual.size());
		
		for (int i=0; i<ordExpected.size(); i++) {
			long now = new Date().getTime();
			long createdTime = ordActual.get(i).getLastmod().getTime();
			// Checking that createdTime isn't after now shouldn't be necessary but can be done if wanted.
			assertTrue((now - allowableDelta) < createdTime);
			assertEquals(ordExpected.get(i).getLoc(), ordActual.get(i).getLoc());
		}
	}

	/**
	 * Could check that sitemap index locations match file names, but we have
	 * an expected sitemap index which should assure this anyways (if the actual
	 * sitemap index and sitemaps are correct. It would fail anyways if they weren't)
	 * @param expected
	 * @param actual 
	 */
	public void compareSitemapCollections(SitemapCollection expected, SitemapCollection actual) throws IOException {
		compareSitemapIndices(expected.sitemapindex, actual.sitemapindex);

		Comparator<Sitemap> comp = (expectedSitemap, actualSitemap) -> expectedSitemap.name.compareTo(actualSitemap.name);
		List<Sitemap> ordExpected = new ArrayList<>(expected.sitemaps);
		ordExpected.sort(comp);
		List<Sitemap> ordActual = new ArrayList<>(actual.sitemaps);
		ordActual.sort(comp);
		assertEquals(ordExpected.size(), ordActual.size());
		
		for (int i=0; i<ordExpected.size(); i++) {
			compareSitemaps(ordExpected.get(i), ordActual.get(i));
		}
	}
}