package elibrary.auth;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import elibrary.tools.LogParser;
import elibrary.tools.Navigator;
import elibrary.tools.Pages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogIntoElibrary {

    private static final Logger logger = LoggerFactory.getLogger(LogIntoElibrary.class);
    private static final String login       = "nshindarev";
    private static final String password    = "v3r0n1k4";
    private static final String elib_start  = "https://www.elibrary.ru";

    public static void auth (){
        try{

            Navigator.webClient.getOptions().setCssEnabled(true);
            Navigator.webClient.getOptions().setJavaScriptEnabled(true);
            Navigator.webClient.getOptions().setThrowExceptionOnScriptError(true);
            Navigator.webClient.waitForBackgroundJavaScript(25000);
            Navigator.webClient.setJavaScriptTimeout(25000);
            Navigator.webClient.getCache().clear();

            HtmlPage startPage = Navigator.webClient.getPage(elib_start);
            logger.debug(startPage.asText());

            // goto form "login"
            HtmlForm form = startPage.getFormByName("login");
            HtmlTextInput txtField      = form.getInputByName("login");
            HtmlPasswordInput pswField = form.getInputByName("password");

            //set login/password values
            txtField.setValueAttribute(LogIntoElibrary.login);
            pswField.setValueAttribute(LogIntoElibrary.password);


            HtmlElement elt = startPage.getHtmlElementById("win_login");
            List<HtmlElement> elements = elt.getElementsByAttribute("div", "class", "butred");

            HtmlPage searchResults = elements.get(0).click();

            Pages.startPage = searchResults;
            Pages.authorSearchPage = Navigator.getAuthorsSearchPage(Pages.startPage);

            LogParser.logPage(Pages.authorSearchPage, "AUTHOR SEARCH");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
