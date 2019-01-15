package mil.dtic.sitemaps.management;

import com.redfin.sitemapgenerator.SitemapValidator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mil.dtic.sitemaps.management.configuration.SitemapManagerConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author SEFFERNICKM
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
@SpringBootTest
public class UserStoryTest {

    private static final String SIMPLE_ENDPOINT = "/sitemap-manager/simple";

    private static final int MAX_ENTRIES = 10000;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SitemapManagerConfiguration config;

    private static MockMvc mockMvc;

    private static File outputDir = null;

    private static final int ENTRIES = 110000;

    @Before
    public void setup() throws IOException, Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        if (outputDir == null) {
            outputDir = new File(config.getRootPath());
            if (outputDir.exists()) {
                FileUtils.deleteDirectory(outputDir);            
            }
            Files.createDirectories(Paths.get(config.getRootPath()));
            bulkPost(ENTRIES, 7);
        }
    }

    private static void postText(String data) throws Exception {
        MockHttpServletRequestBuilder request = request(HttpMethod.POST, SIMPLE_ENDPOINT)
                .content(data)
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN);
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("created"));
    }

    private static String leftPad(int i, int width) {
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        while (sb.length() < width) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    private static void bulkPost(int entries, int width) throws Exception {
        int maxEntries = 100000;
        int queued = 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries; i++) {
            sb.append("https://apps.dtic.mil/docs/citations/AD");
            sb.append(leftPad(i, width));
            sb.append("\r\n");

            queued += 1;
            if (queued > maxEntries) {
                postText(sb.toString());
                sb = new StringBuilder();
                queued = 0;
            }
        }

        if (queued > 0) {
            postText(sb.toString());
        }
    }

    private List<String> getSitemapLocs(String data) {
        String regex = "<sitemap>.*?<loc>(.*?)</loc>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(data);
        List<String> retval = new ArrayList<>();
        while (m.find()) {
            retval.add(m.group(1));
        }
        return retval;
    }
    
    // Verify the generated sitemaps are complient with version 0.9 of the sitemap protocol XML schema
    @Test
    public void validateSchema() throws org.xml.sax.SAXException {
        boolean didIndex = false;
        for (File f : outputDir.listFiles()) {
            if (!didIndex) {
                try {
                    SitemapValidator.validateSitemapIndex(f);
                    didIndex = true;
                    continue;
                } catch (org.xml.sax.SAXException ex) {
                    
                }
            }
            SitemapValidator.validateWebSitemap(f);
        }
        assertTrue(didIndex);
    }

    // Ensure generated sitemap files contain no more than 10k entries
    @Test
    public void entryLimit() throws Exception {
        File[] files = outputDir.listFiles();
        int total = files.length;
        for (File f : files) {
            String body = FileUtils.readFileToString(f, Charset.defaultCharset());
            int records = StringUtils.countMatches(body, "<loc>");
            total += records;
            assertTrue(records <= MAX_ENTRIES);
        }
        assertEquals(ENTRIES + (files.length * 2 - 1), total);
    }

    // Ensure generated sitemap files are no more than 50MB in size
    @Test
    public void sizeLimit() {
        for (File f : outputDir.listFiles()) {
            long inB = f.length();
            long inKb = inB / 1024;
            long inMb = inKb / 1024;
            assertTrue(inMb < 50);
        }
    }

    // Ensure there is a sitemap index, containing references to each individual sitemap
    @Test
    public void validateReferences() throws IOException {
        File[] files = outputDir.listFiles();
        Set<String> paths = new HashSet<>();
        for (File f : files) {
            paths.add(config.getRootPathWeb() + f.getName());
        }
        
        int ct = 0;
        for (File f : files) {
            List<String> locs = getSitemapLocs(FileUtils.readFileToString(f, Charset.defaultCharset()));
            for (String l : locs) {
                assertTrue(paths.contains(l));
                paths.remove(l);
            }
            ct += locs.size();
        }
        assertEquals(files.length - 1, ct);
    }
}
