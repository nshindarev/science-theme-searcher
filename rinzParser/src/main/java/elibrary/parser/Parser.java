package elibrary.parser;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;
import elibrary.tools.LogParser;
import elibrary.tools.Navigator;
import elibrary.tools.Pages;
import model.Author;
import model.Keyword;
import model.Link;
import model.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//        LogParser.logPage(Pages.keywordSearchPage, "KEYWORD");


       getKeywordResults(Pages.keywordSearchPage);
    }

    /**
     *  method starts handling keyword search results:
     *  <a> {publicationName} <a/>
     *  <i> {author 1},{author 2}<i/>
     */
    private Map<Publication, List<Author>> getKeywordResults (HtmlPage page){
        //TODO: unique search!!!
        Set<Author> authorSet = new HashSet<>();
        Set<Publication> publicationSet = new HashSet<>();

        Map result = new HashMap<Publication, List<Author>>();
        final HtmlTable rezultsTable = page.getHtmlElementById("restab");

        for (final HtmlTableRow row : rezultsTable.getRows()) {
            if (row.getElementsByTagName("a").size() > 0 && row.getElementsByTagName("i").size() > 0) {
                HtmlElement publName = row.getElementsByTagName("a").get(0);
                HtmlElement authNames = row.getElementsByTagName("i").get(0);


                List<String> authInPubl = Arrays.asList(authNames.asText().split(","));
                Publication publ = new Publication(publName.asText());
                List<Author> authors = new LinkedList<>();

                // --- convert string into business object
                // --- get link for authors pages
                for (String auth : authInPubl) {
                    Author authBO = Author.convertStringToAuthor(auth);
                    if (!authorSet.contains(authBO)) {
                        //get page
                        HtmlPage authSearchPage = Navigator.getAuthorSearchResultsPage(authBO);

                        //set link
                        authBO = Parser.setLinkToAuthor(authBO, authSearchPage);
                        authors.add(authBO);
                        authorSet.add(authBO);
                    }
                }

                result.put(publ, authors);
            }
        }
        return result;
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

            HtmlAnchor anchor = (HtmlAnchor)table.getRow(3)
                    .getElementsByAttribute("a", "title", "Список публикаций данного автора в РИНЦ")
                    .get(0);

            author.addLink(new Link("http://elibrary.ru/" + anchor.getAttribute("href")));
        }
        catch(ElementNotFoundException ex){
            logger.warn("Found author without page " + author.getSurname());
        }

        logger.trace("LINK TO "+author.toString()+" ==> " + author.getLinks().toString());
        return author;
    }

}
