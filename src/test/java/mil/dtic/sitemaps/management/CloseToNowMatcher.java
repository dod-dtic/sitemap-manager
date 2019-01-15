package mil.dtic.sitemaps.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Could possibly use this in the other place that compares time with some slight modifications.
 */
public class CloseToNowMatcher extends TypeSafeMatcher<String> {
	private static final long acceptableDelta = 10000;
	
	@Override
	protected boolean matchesSafely(String timeString) {
		try {
			//String.valueOf(Instant.now().getEpochSecond()))
			Long givenTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
				.parse(timeString)
				.getTime();
			
			Long now = new Date().getTime();

			return (now - givenTime) < acceptableDelta;
		} catch (ParseException ex) {
			Logger.getLogger(CloseToNowMatcher.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("Ensuring the date string given is close to now.");
	}
}