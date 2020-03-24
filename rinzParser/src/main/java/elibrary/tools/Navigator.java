package elibrary.tools;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import database.model.Author;
import database.model.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class Navigator {
    private static final Logger logger = LoggerFactory.getLogger(Navigator.class);

    public static final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    public static final int timeOut = 10000;
    public static int searchLimit = 30;


    /**
     * after logging in -> goto authors search page
     * @param startPage https://elibrary.ru/defaultx.asp
     * @return https://elibrary.ru/authors.asp
     */
    public static HtmlPage getAuthorsSearchPage (HtmlPage startPage){

//        try{
//            webClient.getCache().clear();
//            webClient.getCookies(new URL("https://www.elibrary.ru/authors.asp")).clear();
//        }
//        catch (MalformedURLException ex){
//            ex.printStackTrace();
//        }

        HtmlAnchor anchor = startPage.getAnchorByHref("/authors.asp");
        try{
            return (HtmlPage) anchor.openLinkInNewWindow();
        }
        catch (MalformedURLException ex){
            logger.error(ex.getMessage());
        }
        catch (NullPointerException ex){
            logger.error(ex.getMessage());
        }
        return startPage;
    }
    public static HtmlPage getKeywordSearchResultsPage (Keyword key){
        HtmlElement form = Pages.startPage.getHtmlElementById("win_search");

        HtmlTextInput textField = (HtmlTextInput)form.getElementsByAttribute("input", "name", "ftext").get(0);
        textField.setValueAttribute(key.getKeyword());

        try{
            List<HtmlElement> listElements = form.getElementsByAttribute("div", "class", "butblue");
            HtmlPage resultPage = listElements.get(0).click();
            return resultPage;
        }
        catch(IOException ex){
            logger.error("===== EXCEPTION DURING KEYWORD SEARCH ======");
            logger.error(ex.getMessage());
            return Pages.startPage;
        }
    }


    /**
     * returns result search page for author's page
     * @param authorInfo
     * @return
     */
    public static HtmlPage getAuthorSearchResultsPage(Author authorInfo){
        try {

            WebClient wc = new WebClient(BrowserVersion.CHROME);
            wc.getOptions().setCssEnabled(true);
            wc.getOptions().setJavaScriptEnabled(true);
            wc.getOptions().setThrowExceptionOnScriptError(true);
            wc.waitForBackgroundJavaScript(25000);
            wc.setJavaScriptTimeout(25000);
            wc.getCache().clear();


            HtmlPage authSearchPage = wc.getPage(Pages.authorSearchPage.getUrl());
            HtmlTextInput surnameInput = authSearchPage.getHtmlElementById("surname");

            HtmlElement _form = authSearchPage.getHtmlElementById("show_param");
            List<HtmlElement> _listElements = _form.getElementsByAttribute("div","class", "butblue");

            try{
                if (_listElements.size()>0) {
                    HtmlElement firstRes = _listElements.get(0);
                    firstRes.click();
                }
            }
            catch (IndexOutOfBoundsException ex){
                logger.warn("cannot click clear button: " +  authorInfo.toString());
                return Pages.authorSearchPage;
            }



            // check if patronymic was inserted
            if(authorInfo.getPatronymic()!=null){
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName()+" "+ authorInfo.getPatronymic());
                surnameInput.setDefaultValue(authorInfo.getSurname()+" "+ authorInfo.getName()+" "+ authorInfo.getPatronymic());
            }
            else if (authorInfo.getName()!=null){
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName());
                surnameInput.setDefaultValue(authorInfo.getSurname()+" "+ authorInfo.getName());
            }
            else if (Character.isLetter(authorInfo.getP().charAt(0))) {
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getN() + ". "+ authorInfo.getP()+".");
                surnameInput.setDefaultValue(authorInfo.getSurname()+" "+ authorInfo.getN() + ". "+ authorInfo.getP()+".");
            }
            else if (Character.isLetter(authorInfo.getN().charAt(0))){
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getN()+".");
                surnameInput.setDefaultValue(authorInfo.getSurname()+" "+ authorInfo.getN()+".");
            }
            else {
                surnameInput.setValueAttribute(authorInfo.getSurname());
                surnameInput.setDefaultValue(authorInfo.getSurname());
            }

            // find search button
            HtmlElement form = authSearchPage.getHtmlElementById("show_param");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");
            HtmlPage resultPage = Pages.authorSearchPage;

            //sort list by publications
            HtmlSelect select = (HtmlSelect) authSearchPage.getElementById("sortorder");
            select.getOptionByText("по числу публикаций").setSelected(true);


            // click search button
            try{
                if (listElements.size()>0) {

                    HtmlElement firstRes = listElements.get(0);
                    resultPage = firstRes.click();
                }
                return resultPage;
            }
            catch (IndexOutOfBoundsException ex){
                logger.warn("IndexOutOfBoundsException for " +  authorInfo.toString());
                logger.warn(resultPage.asText());
                return Pages.authorSearchPage;
            }
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            return Pages.authorSearchPage;
        }
        catch (ElementNotFoundException ex){
            logger.error(ex.getMessage());
            return Pages.authorSearchPage;
        }
    }


}
