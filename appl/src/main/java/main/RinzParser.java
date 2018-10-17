package main;

import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;
import datamapper.Author;
import datamapper.Publication;
import datamapper.Theme;
import io.FileWriterWrap;
import io.LogStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Navigator;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RinzParser {
    private static final Logger logger  = LoggerFactory.getLogger(RinzParser.class);
    private Author author;
    private Theme theme;

    public RinzParser (Theme theme){
        this.theme = theme;
        startResearch();
    }
    public RinzParser (Author author){
        this.author = author;
        startResearch();
    }


    private void startResearch(){
        // __________authorization______________
        ElibAuthorize auth = new ElibAuthorize();
        HtmlPage startPage = auth.getElibraryStartPage();


        //___________search by author____________________
        if (this.author != null){
            HtmlPage authPage  = Navigator.navigateToAuthorsSearchPage(startPage);
            HtmlPage resPage   = Navigator.navigateToAuthorsSearchRezults(this.author, authPage);

            // method fills link to user
            setLinkToAuthor(this.author, resPage);

            try{
                HtmlPage publicationsPage = Navigator.navigateToPublications(this.author);
                this.author = collectCoAuthors(publicationsPage, this.author);

                for(Author coAuthor: this.author.coAuthors){
                    resPage  = Navigator.navigateToAuthorsSearchRezults(coAuthor, Navigator.navigateToAuthorsSearchPage(startPage));
                    coAuthor.linkToUser = setLinkToAuthor(coAuthor, resPage).linkToUser;
                }

                LogStatistics.logAuthorsPublications(this.author);
                FileWriterWrap.writePageIntoFile(publicationsPage, "authorsPublicationsPage");
            }
            catch (IOException ex){
                logger.error(ex.getMessage());
            }

            logger.trace("");
            // HtmlPage firstSearchRez = defaultSearch(authorInfo, startPage);
            // logger.trace(firstSearchRez.toString());
        }

    }

    /**
     * Search methods, usage depends on current page
     * @param authorInfo authors name
     * @param currentPage current page with form to insert
     * @return HtmlPage
     */
    private HtmlPage defaultSearch(String query, HtmlPage currentPage){

        //__________ log forms in page
        List<HtmlForm> forms = currentPage.getForms();
        for (HtmlForm form : forms) {
            logger.trace(form.toString());
        }

        /**
         *  deprecated search code
         */
        HtmlForm form = currentPage.getFormByName("search");
        HtmlTextInput textField = form.getInputByName("ftext");
        textField.setValueAttribute(query);

        try{
            List<HtmlElement> listElements = form.getElementsByAttribute("div", "class", "butblue");
            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            FileWriterWrap.writePageIntoFile(resultPage,"basicSearchResults");

            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            logger.error("error during search call");
            return currentPage;
        }
    }




    private Author setLinkToAuthor(Author author, HtmlPage curPage){
        List<String> result = new LinkedList<>();

        try{
            HtmlTable table = curPage.getHtmlElementById("restab");

            for (HtmlTableRow row : table.getRows()) {
                if (row.getIndex() < 3) {
                    continue;
                }

                List a = row.getCell(3).getElementsByAttribute("a", "title", "Анализ публикационной активности автора");
                if (!a.isEmpty()) {
                    HtmlAnchor anchor = (HtmlAnchor) a.get(0);
                    String value = anchor.getAttribute("href");
                    result.add("http://elibrary.ru/" + value);

                    author.linkToUser = "http://elibrary.ru/" + value;
                }
            }
        }
        catch(ElementNotFoundException ex){
            logger.warn("Found author without page " + author.getSurname());
        }
        return author;
    }



    public Author collectCoAuthors (HtmlPage publicationsPage, Author curAuthor){
        final HtmlTable rezultsTable = publicationsPage.getHtmlElementById("restab");

        /**
         * <a> Название публикации </a>
         * <i> Фамилия И.О., Фамилия И.О., ...</i>
         */
        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                HtmlElement publName = row.getElementsByTagName("a").get(0);
                HtmlElement authNames = row.getElementsByTagName("i").get(0);

                curAuthor.publications.add(new Publication(publName.asText(), authNames.asText()));

                List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                for (String auth : authInPubl) {
                    Author authObj = Author.convertStringToAuthor(auth);
                    curAuthor.coAuthors.add(authObj);
                }

                logger.debug(publName.asText());
                logger.debug(authNames.asText());
            }
        }

        LogStatistics.logAuthorsDB_auth();

        if(Navigator.navigateToNextPublications(publicationsPage)!=null){
            publicationsPage = Navigator.navigateToNextPublications(publicationsPage);
            logger.debug("________________page ended___________________");
            collectCoAuthors(publicationsPage, curAuthor);
        }

        FileWriterWrap.writeAuthorsSetIntoFile(curAuthor.coAuthors, "authors");
        return curAuthor;
    }
//    public Theme collectCoAuthors (HtmlPage publicationsPage, Theme curTheme){
//
//        if (!AuthorsDB.getAuthorsStorage().contains(this)) {
//            final HtmlTable rezultsTable = publicationsPage.getHtmlElementById("restab");
//
//            /**
//             * <a> Название публикации </a>
//             * <i> Фамилия И.О., Фамилия И.О., ...</i>
//             */
//            for (final HtmlTableRow row : rezultsTable.getRows()) {
//                if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
//                    HtmlElement publName = row.getElementsByTagName("a").get(0);
//                    HtmlElement authNames = row.getElementsByTagName("i").get(0);
//
//                    this.publications.add(new Publication(publName.asText(), authNames.asText()));
//
//                    List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
//                    for (String auth : authInPubl) {
//                        Author authObj = convertStringToAuthor(auth);
//                        AuthorsDB.addToAuthorsStorage(authObj);
//                    }
//
//                    logger.debug(publName.asText());
//                    logger.debug(authNames.asText());
//                }
//            }
//
//            LogStatistics.logAuthorsDB_auth();
//
//            if(navigateToNextPublications(publicationsPage)!=null){
//                publicationsPage = navigateToNextPublications(publicationsPage);
//                logger.debug("page ended");
//                collectAuthorsPublications(publicationsPage);
//            }
//
//            FileWriterWrap.writeAuthorsSetIntoFile(AuthorsDB.getAuthorsStorage(), "authors");
//        }
//    }

}
