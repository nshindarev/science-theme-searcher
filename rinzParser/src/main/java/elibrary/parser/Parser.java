package elibrary.parser;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.StorageHolder;
import com.gargoylesoftware.htmlunit.html.*;
import database.operations.StorageHandler;
import elibrary.tools.Navigator;
import elibrary.tools.Pages;
import database.model.Author;
import database.model.Keyword;
import database.model.Link;
import database.model.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

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

        // keyword search + fill links to authors
        Set<Author> keywordAuthors = getKeywordResults(Pages.keywordSearchPage);
        StorageHandler.saveAuthors(keywordAuthors);

        Set<Author> authors = new HashSet<>(StorageHandler.getAuthorsWithoutRevision());
        authors.forEach(it -> {
            Set<Author> coAuthors1 = getCoAuthors(it);
            StorageHandler.saveAuthors(coAuthors1);
            it.setRevision(1);
        });

        StorageHandler.updateRevision(authors);
    }

    /**
     *  method starts handling keyword search results:
     *  <a> {publicationName} <a/>
     *  <i> {author 1},{author 2}<i/>
     */
    private Set<Author> getKeywordResults (HtmlPage page) throws ElementNotFoundException{

        Set<Author> authorSet = new HashSet<>();
        final HtmlTable rezultsTable = page.getHtmlElementById("restab");

        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                if(authorSet.size()<= Navigator.searchLimit){
                    HtmlElement publName = row.getElementsByTagName("a").get(0);
                    HtmlElement authNames = row.getElementsByTagName("i").get(0);


                    List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                    Publication publ = new Publication(publName.asText());
                    List<Author> authors = new LinkedList<>();

                    // --- convert string into business object
                    // --- get link for authors pages
                    for (String auth : authInPubl) {
                        Author authBO = Author.convertStringToAuthor(auth);
                        authBO.addPublication(publ);

                        if (!authorSet.contains(authBO) && authorSet.size() <= Navigator.searchLimit) {

                            //get page
                            HtmlPage authSearchPage = Navigator.getAuthorSearchResultsPage(authBO);

                            //set link
                            authBO = Parser.setLinkToAuthor(authBO, authSearchPage);
                            authors.add(authBO);
                            authorSet.add(authBO);
                        }
                    }
                }
            }
        }
        return authorSet;
    }

    // take 1st link from authors links
    private Set<Author> getCoAuthors (Author author){
        Set<Author> res;

        if (author.getLinks().iterator().hasNext()){
            return getCoAuthors(author.getLinks().iterator().next());
        }
        else return new HashSet<>();
    }

    private Set<Author> getCoAuthors (Link link){
        HtmlPage dataPage = Navigator.getAuthorsPage(link);

        return getKeywordResults(dataPage);
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

        logger.trace("LINK TO "+author.toString()+" ==> " + author.getLinks().toString());
        return author;
    }
}
