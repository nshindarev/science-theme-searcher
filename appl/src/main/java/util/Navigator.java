package util;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import datamapper.ResearchStarters.Author;
import datamapper.ResearchStarters.Theme;
import io.FileWriterWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class Navigator {
    private HtmlPage currentPage;
    private static final Logger logger = LoggerFactory.getLogger(Navigator.class);

    public static final WebClient webClient = new WebClient();
    public static final int timeOut = 10000;
    public static final int searchLimit = 5;

    public static HtmlPage authorSearchPage;

    private Navigator(){

    }


    // дефолтная страница с полями для ввода автора
    public static HtmlPage navigateToAuthorsSearchPage(HtmlPage startPage){
        HtmlAnchor anchor = startPage.getAnchorByHref("/authors.asp");
        logger.trace(anchor.toString());

        try{
            FileWriterWrap.writePageIntoFile((HtmlPage) anchor.openLinkInNewWindow(), "authorsSearchPage");
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

    public static HtmlPage navigateToThemeSearchResults(Theme theme, HtmlPage homePage){
        HtmlForm form = homePage.getFormByName("search");
        HtmlTextInput textField = form.getInputByName("ftext");
        textField.setValueAttribute(theme.getName());

        try{
            List<HtmlElement> listElements = form.getElementsByAttribute("div", "class", "butblue");
            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            FileWriterWrap.writePageIntoFile(resultPage,"theme/searchResults.xml");

            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            logger.error("error during search call");
            return homePage;
        }
    }
    // метод вводит ФИО автора и кликает на поиск
    public static HtmlPage navigateToAuthorsSearchResults(Author authorInfo, HtmlPage defaultAuthSearchPage){


        //________________trace_____________________
        List<HtmlForm> forms = defaultAuthSearchPage.getForms();
        for (HtmlForm form : forms) {
            logger.trace(form.toString());
        }

        try {
            HtmlTextInput surnameInput = defaultAuthSearchPage.getHtmlElementById("surname");

            // check if patronymic was inserted
            if(authorInfo.getPatronymic()!=null)
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName()+" "+ authorInfo.getPatronymic());
            else if (authorInfo.getName()!=null)
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName());
            else if (Character.isLetter(authorInfo.getP()))
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getN() + ". "+ authorInfo.getP()+".");
            else if (Character.isLetter(authorInfo.getN()))
                surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getN()+".");
            else surnameInput.setValueAttribute(authorInfo.getSurname());

            logger.info ("_________________________________");
            logger.info ("SEARCH PAGE FOR AUTHOR " + surnameInput.getText());
            logger.info ("_________________________________");
            HtmlForm form = defaultAuthSearchPage.getFormByName("results");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");

            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            FileWriterWrap.writePageIntoFile(resultPage, "authorSearchResults");

            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            return defaultAuthSearchPage;
        }
        catch (ElementNotFoundException ex){
            FileWriterWrap.writePageIntoFile(defaultAuthSearchPage, "error/authorSearchFieldNotFound");
            logger.error(ex.getMessage());

            return defaultAuthSearchPage;
        }
    }


    public static HtmlPage navigateToPublications(Author author) throws IOException{

        HtmlPage  authorsPage = navigateToAuthor(author);


        FileWriterWrap.writePageIntoFile(authorsPage, "authPage");
        HtmlForm form = authorsPage.getFormByName("results");
        HtmlAnchor referenceToPublications = (HtmlAnchor)form.getElementsByAttribute("a", "title", "Список публикаций автора в РИНЦ").get(0);

        return (HtmlPage) referenceToPublications.openLinkInNewWindow();
    }
    public static HtmlPage navigateToAuthor(Author author) throws IOException {
        if (author.linkToUser != null){
            return Navigator.webClient.getPage(author.linkToUser);
        }
        else return null;
    }



    public static HtmlPage navigateToNextPublications(HtmlPage page){
        try{
            HtmlForm form = page.getFormByName("results");
            HtmlAnchor anch = (HtmlAnchor) form.getElementsByAttribute("a", "title", "Следующая страница").get(0);

            return (HtmlPage) anch.openLinkInNewWindow();
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
        catch (IndexOutOfBoundsException ex){
            return null;
        }
    }



}
