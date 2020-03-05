package parser;

import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;

import io.FileWriterWrap;
import model.Author;
import model.Keyword;
import model.Publication;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AuthorService;
import service.PublicationService;
import util.Navigator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class MyRinzParser {
    private static final Logger logger = LoggerFactory.getLogger(MyRinzParser.class);

    private static Keyword keyword;
    private static HtmlPage startPage;
    private static HtmlPage authorSearchPage;

    public MyRinzParser(Keyword keyword) {
        ElibAuthorize auth = new ElibAuthorize();

        this.startPage = auth.getElibraryStartPage();
        this.keyword = keyword;

        authorSearchPage = navigateToAuthorsSearchPage();
    }

    public void search() {

        if (this.keyword != null) {
            HtmlPage resPage = Navigator.navigateByKeyword(keyword, startPage);
            getPublications(resPage, this.keyword);

        }
    }


    /**
     * <a> Название публикации </a>
     * <i> Фамилия И.О., Фамилия И.О., ...</i>
     */
    public void getPublications(HtmlPage page, Keyword key) {
        AuthorService authorService = new AuthorService();
        PublicationService publicationService = new PublicationService();

        authorService.openConnection();
        publicationService.openConnection();

        final HtmlTable rezultsTable = page.getHtmlElementById("restab");

        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                HtmlElement publName = row.getElementsByTagName("a").get(0);
                HtmlElement authNames = row.getElementsByTagName("i").get(0);



                List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                Publication publ = new Publication(publName.asText());
                for (String auth : authInPubl) {
                    try{
                        Author authObj = Author.convertStringToAuthor(auth);
                        Author old = authorService.findAuthor(authObj.getId());
                        publ.addAuthor(authObj);

                        if (old == null){
                            authorService.saveAuthor(authObj);
                        }
                        logger.trace(authObj.toString());
                    }
                    catch (org.hibernate.InstantiationException ex){
                        logger.error(auth);
                        ex.printStackTrace();
                    }
                }

                Publication old = publicationService.findPublication(publ.getId());
                if (old == null)
                    publicationService.savePublication(publ);

                logger.trace(publName.asText());
                logger.trace(authNames.asText());
            }
        }
        publicationService.closeConnection();
        authorService.closeConnection();
    }

    public void getPublications (List<Author> authors){

        for(Author auth: authors){
            getPublicationsPage(auth);
        }
    }

    public HtmlPage getPublicationsPage (Author author){
        try {
            HtmlTextInput surnameInput = authorSearchPage.getHtmlElementById("surname");

            // check if patronymic was inserted
            if(author.getPatronymic()!=null)
                surnameInput.setValueAttribute(author.getSurname()+" "+ author.getName()+" "+ author.getPatronymic());
            else if (author.getName()!=null)
                surnameInput.setValueAttribute(author.getSurname()+" "+ author.getName());
            else if (Character.isLetter(author.getP().charAt(0)))
                surnameInput.setValueAttribute(author.getSurname()+" "+ author.getN() + ". "+ author.getP()+".");
            else if (Character.isLetter(author.getN().charAt(0)))
                surnameInput.setValueAttribute(author.getSurname()+" "+ author.getN()+".");
            else surnameInput.setValueAttribute(author.getSurname());

            HtmlForm form = authorSearchPage.getFormByName("results");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");

            HtmlPage resultPage = listElements.get(0).click();
            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            return authorSearchPage;
        }
        catch (ElementNotFoundException ex){
            logger.error(ex.getMessage());
            return authorSearchPage;
        }
    }
//    public static HtmlPage navigateToAuthorsSearchResults(){
//
//    }
    public static HtmlPage navigateToAuthorsSearchPage(){
        HtmlAnchor anchor = startPage.getAnchorByHref("/authors.asp");
        try{
            MyRinzParser.authorSearchPage = (HtmlPage) anchor.openLinkInNewWindow();
            return authorSearchPage;
        }
        catch (MalformedURLException ex){
            logger.error(ex.getMessage());
        }
        catch (NullPointerException ex){
            logger.error(ex.getMessage());
        }
        return startPage;
    }
}
