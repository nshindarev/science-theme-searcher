package auth;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ElibAuthorize {

    private static final Logger logger = LoggerFactory.getLogger(ElibAuthorize.class);
    private static final String login    = "nshindarev";
    private static final String password = "v3r0n1k4";

    private static final String elib_start_authors  = "https://elibrary.ru/authors.asp";
    private static final String elib_start          = "https://elibrary.ru";

    //page returned after
    private HtmlPage elibraryStartPage;
    private WebClient webClient = new WebClient();

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
            HtmlPage startPage = webClient.getPage(elib_start);
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

            logger.trace(form.asXml());

            List<HtmlElement> elements = form.getElementsByAttribute("div", "class", "butred");
            HtmlPage searchResults = elements.get(0).click();

            logger.debug(searchResults.asXml());
            this.elibraryStartPage = searchResults;
    }

    public HtmlPage getElibraryStartPage(){
        return this.elibraryStartPage;
    }
}