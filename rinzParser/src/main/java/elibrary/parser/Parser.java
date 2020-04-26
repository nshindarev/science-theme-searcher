package elibrary.parser;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;
import database.operations.StorageHandler;
import elibrary.auth.LogIntoElibrary;
import elibrary.tools.Pages;
import database.model.Author;
import database.model.Keyword;
import database.model.Link;
import database.model.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static elibrary.parser.Navigator.getKeywordNextResults;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private Keyword keyword;

    public Parser (Keyword key){
        this.keyword = key;
    }

    /**
     *  starts searching co-authors according to keyword inserted
     */
    public void parse(){

        if (keyword != null && Pages.startPage != null)
            Pages.keywordSearchPage = Navigator.getKeywordSearchResultsPage(keyword);

        Set<Author> keywordAuthors = getKeywordResults(Pages.keywordSearchPage, Navigator.searchLimit);
        StorageHandler.saveCoAuthors(keywordAuthors);
        StorageHandler.saveAuthors(keywordAuthors);

        // names of all publications associated with current keyword
        Navigator.allKeywordPublicationIds = getAllPublicationIds(Pages.keywordSearchPage, 1);
        Navigator.allKeywordPublicationIds.forEach(it -> logger.info(it.toString()));

        // 2-level limited search
        for(int i=0; i<Navigator.searchLevel; i++){
            Set<Author> authors = new HashSet<>(StorageHandler.getAuthorsWithoutRevision());
            authors.forEach(it -> {
                Set<Author> coAuthors = getCoAuthors(it);
                StorageHandler.saveAuthors(coAuthors);
                it.setRevision(1);

                StorageHandler.saveCoAuthors(coAuthors);
            });
            StorageHandler.updateRevision(authors);
        }

        StorageHandler.updateKeyword(Navigator.allKeywordPublicationIds);
//        StorageHandler.saveCoAuthors();
//
//        Set<Author> allKeywordAuthors = new HashSet<>();
//        try {
//            int i = 1;
//            while (true){
//                allKeywordAuthors
//                        .addAll(getKeywordResults(getKeywordNextResults(Pages.keywordSearchPage, i), Integer.MAX_VALUE));
//                allKeywordAuthors.forEach(it -> logger.info(it.toString()));
//                i++;
//            }
//
//        }
//        catch (IOException ex){
//            logger.error(ex.getMessage());
//        }
//        catch (RuntimeException ex){
//            logger.error(ex.getMessage());
//        }
//
//        logger.info("");


//        Set<Author> keywordAuthors = getKeywordResults(Pages.keywordSearchPage, Navigator.searchLimit);

        //        StorageHandler.saveAuthors(keywordAuthors);
//
//
    }

    /**
     *  collects publications for keyword:
     *  collects publications for author:
     *  <a> {publicationName} <a/>
     *  <i> {author 1},{author 2}<i/>
     */
    private Set<Author> getKeywordResults (HtmlPage page, int searchLimit) throws ElementNotFoundException{

        try{
            Set<Author> authorSet = new HashSet<>();
            final HtmlTable rezultsTable = page.getHtmlElementById("restab");

            for (final HtmlTableRow row : rezultsTable.getRows()) {

                // parse publication citations metric
                int citations = 0;
                if (row.getCells().size()>=3){
                    try {
                        if (!row.getCells().get(2).asText().equals("Цит."))
                            citations = Integer.parseInt(row.getCells().get(2).asText());
                    }
                    catch (NumberFormatException ex){
                        logger.warn(ex.getMessage());
                    }
                }

                if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                    if(authorSet.size()<= searchLimit){
                        HtmlElement publName = row.getElementsByTagName("a").get(0);
                        HtmlElement authNames = row.getElementsByTagName("i").get(0);


                        List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                        Publication publ = new Publication(publName.asText());
                        publ.setMetric(citations);
                        List<Author> authors = new LinkedList<>();

                        // --- convert string into business object
                        // --- get link for authors pages
                        for (String auth : authInPubl) {
                            Author authBO = Author.convertStringToAuthor(auth);
                            authBO.addPublication(publ);

                            if (!authorSet.contains(authBO) && authorSet.size() <= searchLimit) {
                                //get page
                                HtmlPage authSearchPage = Navigator.getAuthorSearchResultsPage(authBO);

                                //set link
                                authBO = Parser.setLinkToAuthor(authBO, authSearchPage);
                                authors.add(authBO);
                                authorSet.add(authBO);
                            }
                            else if (authorSet.contains(authBO)){

                                // get data from set
                                ArrayList<Author> authorList = new ArrayList(authorSet);
                                int authBoSavedIndex = authorList.indexOf(authBO);
                                Author authBoSaved = authorList.get(authBoSavedIndex);

                                // join publications
                                authBO.join(authBoSaved);

                                // update author
                                authorSet.remove(authBO);
                                authorSet.add(authBO);
                            }
                        }
                    }
                }
            }
            return authorSet;
        }
        catch (Exception ex){
            logger.error(ex.getMessage());
//            return getKeywordResults (page, searchLimit);
            return new HashSet<>();
        }
    }

    // take 1st link from authors links
    private Set<Author> getCoAuthors (Author author){

        if (author.getLinks().iterator().hasNext()){
            return getCoAuthors(author.getLinks().iterator().next());
        }
        else return new HashSet<>();
    }

    // collects coauthors
    private Set<Author> getCoAuthors (Link link){
        HtmlPage dataPage = Navigator.getAuthorsPage(link);

        return getKeywordResults(dataPage, Navigator.searchLimit);
    }
    /**
     * fills in link to page with authors publications
     * @param author
     * @param curPage after clicking red search button
     * @return
     */
    private static Author setLinkToAuthor (Author author, HtmlPage curPage){
        try{
            HtmlTable table = curPage.getHtmlElementById("restab");
            if (table.getRows().size() >= 3){
                HtmlAnchor anchor = (HtmlAnchor)table.getRow(3)
                        .getElementsByAttribute("a", "title", "Список публикаций данного автора в РИНЦ")
                        .get(0);

                author.addLink(new Link("http://elibrary.ru/" + anchor.getAttribute("href")));
            }
            else {
                logger.warn("Found author without page " + author.getSurname());
            }
        }
        catch(ElementNotFoundException ex){
            logger.warn("Found author without page " + author.getSurname());
        }
        catch (IndexOutOfBoundsException ex){
            logger.warn(ex.getMessage());
            logger.warn("Found author without page " + author.getSurname());
        }

        logger.trace("LINK TO "+author.toString()+" ==> " + author.getLinks().toString());
        return author;
    }

    private Set<Publication> getAllPublicationIds (HtmlPage page, int currentPageNumber) {
        Set<Publication> res = new HashSet<>();
        final HtmlTable rezultsTable = page.getHtmlElementById("restab");

        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                res.add(new Publication(row.getElementsByTagName("a").get(0).asText()));
            }
        }
        try{
            currentPageNumber ++;
            HtmlPage nextPage = Navigator.webClient.getPage("https://elibrary.ru/query_results.asp?pagenum="+currentPageNumber);
            res.addAll(getAllPublicationIds(nextPage, currentPageNumber));
        }
        catch (Exception ex){
            logger.warn("reached last page during keyword search");
            logger.warn("keyword search made in " + --currentPageNumber + " pages");
            return res;
        }
        return res;
    }


    }
