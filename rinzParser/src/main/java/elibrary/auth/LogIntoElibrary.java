package elibrary.auth;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import elibrary.tools.LogParser;
import elibrary.parser.Navigator;
import elibrary.tools.Pages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LogIntoElibrary {

    private static final Logger logger = LoggerFactory.getLogger(LogIntoElibrary.class);


    private static Map<String, String> loginData = new HashMap<String, String>() {{
        put("nshindarev","v3r0n1k4");
        put("Zhucharek", "t921760");
        put("lex.suleimanov", "FtXTk4Vd");
        put("Olga Suleymanova", "123yes456");
        put("Elena.Gorbacheva", "ForNikita");
        put("bulba_in_august", "ghjkl12345678");
        put("arsnevl@yahoo.com", "N1k1t0s1n4");
    }};

    private static Iterator<Map.Entry<String, String>> logPass =
            LogIntoElibrary.loginData.entrySet().iterator();


    private static final String elib_start  = "https://www.elibrary.ru";

    public static void auth (){
        try{
            Navigator.webClient = new WebClient(BrowserVersion.CHROME);
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
            txtField.setValueAttribute(logPass.next().getKey());
            pswField.setValueAttribute(logPass.next().getValue());


            HtmlElement elt = startPage.getHtmlElementById("win_login");
            List<HtmlElement> elements = elt
                    .getElementsByAttribute("div", "class", "butred");

            HtmlPage searchResults = elements.get(0).click();

            Pages.startPage = searchResults;
            Pages.authorSearchPage = Navigator.getAuthorsSearchPage(Pages.startPage);

            LogParser.logPage(Pages.authorSearchPage, "AUTHOR SEARCH");
        }
        catch (ElementNotFoundException ex){
            if (logPass.hasNext()) logPass.next();
            auth();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
