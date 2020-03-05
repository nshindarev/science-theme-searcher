package elibrary.tools;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.event.KeyboardEvent;
import com.oracle.tools.packager.Log;
import model.Author;
import model.Keyword;
import model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
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

//        List<HtmlElement> elt  = form.getElementsByAttribute("input", "name", "ftext");
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


    public static HtmlPage getAuthorSearchResultsPage(Author authorInfo){
        try {
            HtmlPage authSearchPage = new WebClient().getPage(Pages.authorSearchPage.getUrl());
            HtmlTextInput surnameInput = authSearchPage.getHtmlElementById("surname");


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


            /**
             *  throws IndexOutOfBoundsException ???
             */
//            HtmlPage resultPage = (HtmlPage)surnameInput.type('\n');
//            return resultPage;


            HtmlElement form = authSearchPage.getHtmlElementById("show_param");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");
            HtmlPage resultPage = Pages.authorSearchPage;
            try{
                if (listElements.size()>0) {
                    HtmlElement firstRes = listElements.get(0);
                    resultPage = firstRes.click();
                }
                return resultPage;
            }
            catch (IndexOutOfBoundsException ex){
                logger.warn("IndexOutOfBoundsException for " +  authorInfo.toString());
                return getAuthorSearchResultsPage(authorInfo);
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
    public static Author  setLinkToAuthor (Author author, HtmlPage curPage){
//        logger.trace(curPage.asText());
        List<String> result = new LinkedList<>();

        try{
            HtmlTable table = curPage.getHtmlElementById("restab");

            for (HtmlTableRow row : table.getRows()) {
                if (row.getIndex() < 3) {
                    continue;
                }

               List a = table.getRow(3).getCell(3).getElementsByAttribute("a", "title", "Анализ публикационной активности автора");

                if (!a.isEmpty()) {
                    HtmlAnchor anchor = (HtmlAnchor) a.get(0);
                    String value = anchor.getAttribute("href");
                    result.add("http://elibrary.ru/" + value);

                    author.addLink(new Link("http://elibrary.ru/" + value));
                }
            }
        }
        catch(ElementNotFoundException ex){
            logger.warn("Found author without page " + author.getSurname());
        }

        logger.trace("LINK TO "+author.toString()+" ==> " + author.getLinks().toString());
        return author;
    }

}
