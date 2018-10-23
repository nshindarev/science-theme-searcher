package main;

import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;
import datamapper.ResearchStarters.Author;
import datamapper.Publication;
import datamapper.ResearchStarters.Theme;
import datamapper.ResearchPoint;
import io.FileWriterWrap;
import io.LogStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;
import util.Navigator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RinzParser {
    private static final Logger logger  = LoggerFactory.getLogger(RinzParser.class);

    private Author author;
    private Theme theme;
    private ResearchPoint searchPoint;

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


        //TODO: объединить author и theme для класса RinzParser
        //___________search by author_____________________________________________
        if (this.author != null){
            HtmlPage authPage  = Navigator.navigateToAuthorsSearchPage(startPage);
            logger.debug(authPage.asText());
            HtmlPage resPage   = Navigator.navigateToAuthorsSearchResults(this.author, authPage);
            logger.debug(resPage.asText());

            // method fills link to user
            setLinkToAuthor(this.author, resPage);

            try{
                HtmlPage publicationsPage = Navigator.navigateToPublications(this.author);
                this.author = (Author) collectCoAuthors(publicationsPage, this.author);

                for(Author coAuthor: this.author.coAuthors){
                    resPage  = Navigator.navigateToAuthorsSearchResults(coAuthor, Navigator.navigateToAuthorsSearchPage(startPage));
                    coAuthor.linkToUser = setLinkToAuthor(coAuthor, resPage).linkToUser;
                }

                this.searchPoint = this.author;
                FileWriterWrap.writePageIntoFile(publicationsPage, "authorsPublicationsPage");

                //TODO: переделать хранилище
                for(Author author: this.author.coAuthors) AuthorsDB.addToAuthorsStorage(author);
            }
            catch (IOException ex){
                logger.error(ex.getMessage());
            }

        }

        //___________search by theme___________________________
        else if (this.theme != null){
            HtmlPage resPage   = Navigator.navigateToThemeSearchResults(theme, startPage);
            this.theme = (Theme) collectCoAuthors(resPage, this.theme);

            for(Author coAuthor: this.theme.coAuthors){
                resPage  = Navigator.navigateToAuthorsSearchResults(coAuthor, Navigator.navigateToAuthorsSearchPage(startPage));
                coAuthor.linkToUser = setLinkToAuthor(coAuthor, resPage).linkToUser;
            }
            this.searchPoint = this.theme;


            for(Author author: this.theme.coAuthors) AuthorsDB.addToAuthorsStorage(author);
        }

        LogStatistics.logAuthorsPublications(this.searchPoint);
        LogStatistics.logAuthorsDB_auth();

        /**
         *  Second level search: collects coAuthors for startPoint coauthors (authors, if theme was inserted)
         */
        for(Author coAuthor: this.searchPoint.coAuthors){
            try{
                HtmlPage publicationsPage = Navigator.navigateToPublications(coAuthor);
                coAuthor = (Author) collectCoAuthors(publicationsPage, coAuthor);

                LogStatistics.logAuthorsPublications(coAuthor);
            }
            catch (IOException ex){
                logger.error(ex.getMessage());
            }
        }
        logger.info("");
    }

    /**
     * добавляет поле linkToAuthor с ссылкой на страницу в elibrary
     * @param author автор для добавления ссылки
     * @param curPage страница с результатами поиска по ФИО
     * @return
     */
    private Author setLinkToAuthor(Author author, HtmlPage curPage){
        logger.debug(curPage.asText());
        List<String> result = new LinkedList<>();

        try{
            HtmlTable table = curPage.getHtmlElementById("restab");

            List<HtmlTableRow> rows = table.getRows();
            for (HtmlTableRow row : table.getRows()) {
                if (row.getIndex() < 3) {
                    continue;
                }


//              List a = row.getCell(3).getElementsByAttribute("a", "title", "Анализ публикационной активности автора");
                List a = table.getRow(3).getCell(3).getElementsByAttribute("a", "title", "Анализ публикационной активности автора");

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

        logger.trace("LINK TO "+author.toString()+" ==> " + author.linkToUser);
        return author;
    }


    /**
     * добавляет всех соавторов в поле объекта author
     * @param publicationsPage
     * @param startPoint
     * @return
     */
    public ResearchPoint collectCoAuthors (HtmlPage publicationsPage, ResearchPoint startPoint){
        final HtmlTable rezultsTable = publicationsPage.getHtmlElementById("restab");

        if (startPoint.coAuthors == null || startPoint.publications == null){
            startPoint.publications = new HashSet<>();
            startPoint.coAuthors = new HashSet<>();
        }

        /**
         * <a> Название публикации </a>
         * <i> Фамилия И.О., Фамилия И.О., ...</i>
         */
        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                if (startPoint.coAuthors.size() >= Navigator.searchLimit){
                    continue;
                }
                HtmlElement publName = row.getElementsByTagName("a").get(0);
                HtmlElement authNames = row.getElementsByTagName("i").get(0);

//                logger.trace(publName.asText());
//                logger.trace(authNames.asText());

                startPoint.publications.add(new Publication(publName.asText(), authNames.asText()));

                List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                for (String auth : authInPubl) {
                    Author authObj = Author.convertStringToAuthor(auth);
                    startPoint.coAuthors.add(authObj);
                }

                logger.debug(publName.asText());
                logger.debug(authNames.asText());

                logger.debug("SIZE public = " + startPoint.publications.size());
                logger.debug("SIZE coAuth = " + startPoint.coAuthors.size());
            }
        }

        if(Navigator.navigateToNextPublications(publicationsPage)!=null){
            publicationsPage = Navigator.navigateToNextPublications(publicationsPage);
            logger.debug("________________page ended___________________");
            collectCoAuthors(publicationsPage, startPoint);
        }

        FileWriterWrap.writeAuthorsSetIntoFile(startPoint.coAuthors, "authors");
        return startPoint;
    }
}
