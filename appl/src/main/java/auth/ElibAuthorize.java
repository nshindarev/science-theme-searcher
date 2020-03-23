package auth;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;
import io.FileWriterWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Navigator;

import java.io.IOException;
import java.util.List;

public class ElibAuthorize {

    private static final Logger logger = LoggerFactory.getLogger(ElibAuthorize.class);
    private static final String login    = "lex.suleimanov";
    private static final String password = "FtXTk4Vd";

    public static final String elib_start_authors  = "https://elibrary.ru/authors.asp";
    public static final String elib_start          = "https://www.elibrary.ru";

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

    private void auth() throws IOException {
            HtmlPage startPage = Navigator.webClient.getPage(elib_start);
            logger.trace(startPage.asText());

            logger.info("received page from " + elib_start);

            try{
                // goto form "login"
                HtmlForm form = startPage.getFormByName("login");
                HtmlTextInput txtField      = form.getInputByName("login");
                HtmlPasswordInput pswField = form.getInputByName("password");

                txtField.setValueAttribute(this.login);
                pswField.setValueAttribute(this.password);


                List<HtmlElement> elements = form.getElementsByAttribute("div", "class", "butred");
                HtmlPage searchResults = elements.get(0).click();

                logger.debug(searchResults.asText());
                this.elibraryStartPage = searchResults;
            }
            catch (com.gargoylesoftware.htmlunit.ElementNotFoundException ex){
                this.elibraryStartPage = startPage;
            }
    }

    public HtmlPage getElibraryStartPage(){
        return this.elibraryStartPage;
    }
}