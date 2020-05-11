package elibrary.tools;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogParser {
    private static final Logger logger = LoggerFactory.getLogger(LogParser.class);

    public static void logPage(HtmlPage page, String name){
        logger.trace("======== " + name + " =========");
        logger.trace(page.asText());
    }
}
