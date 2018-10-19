package auth;

import com.gargoylesoftware.htmlunit.html.*;
import io.FileWriterWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Navigator;

import java.io.IOException;
import java.util.List;

public class ElibAuthorize {

    private static final Logger logger = LoggerFactory.getLogger(ElibAuthorize.class);
    private static final String login    = "nshindarev";
    private static final String password = "v3r0n1k4";

    public static final String elib_start_authors  = "https://elibrary.ru/authors.asp";
    public static final String elib_start          = "https://elibrary.ru";

    //page returned after
    private HtmlPage elibraryStartPage;

    public ElibAuthorize(){
       try{
           auth();
       }
       catch (IOException ex){
           logger.error("error during auth on " + elib_start);
       }
    }

    /**
     * auth is necessary only when it's exception thrown with current IP address
     * in other case elib_start_authors can be used
     */
    private void auth() throws IOException {
            HtmlPage startPage = Navigator.webClient.getPage(elib_start);
            logger.info("received page from " + elib_start);
            logger.info(startPage.asXml());

            for (HtmlForm form : startPage.getForms()) {
                logger.debug(form.toString());
            }

            // goto form "login"
            HtmlForm form = startPage.getFormByName("login");

            HtmlTextInput txtField      = form.getInputByName("login");
            txtField.setValueAttribute(this.login);

            HtmlPasswordInput pswField = form.getInputByName("password");
            pswField.setValueAttribute(this.password);

            logger.trace(form.asText());

            List<HtmlElement> elements = form.getElementsByAttribute("div", "class", "butred");
            HtmlPage searchResults = elements.get(0).click();

            logger.debug(searchResults.asText());
            this.elibraryStartPage = searchResults;
    }

    public HtmlPage getElibraryStartPage(){
        logger.trace(this.elibraryStartPage.asText());
        return this.elibraryStartPage;

    }
}