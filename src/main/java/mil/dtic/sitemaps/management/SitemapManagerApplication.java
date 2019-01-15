package mil.dtic.sitemaps.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SitemapManagerApplication extends SpringBootServletInitializer {

    /**
     * Entry point into application
     * @param args command line arguments
     */
	public static void main(String[] args) {
		SpringApplication.run(SitemapManagerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SitemapManagerApplication.class);
	}
}

