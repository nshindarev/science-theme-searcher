package elibrary.parser;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import elibrary.tools.LogParser;
import elibrary.tools.Navigator;
import elibrary.tools.Pages;
import model.Author;
import model.Keyword;
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
     *  method starts handles keyword search results:
     *  <a> {publicationName} <a/>
     *  <i> {author 1},{author 2}<i/>
     */
    private Map<Publication, List<Author>> getKeywordResults (HtmlPage page){
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

                    //get page
                    HtmlPage authSearchPage = Navigator.getAuthorSearchResultsPage(authBO);

                    //set link
                    authBO = Navigator.setLinkToAuthor(authBO, authSearchPage);
                    authors.add(authBO);
                }

                result.put(publ, authors);
            }
        }
        return result;
    }

}
