import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.html.*;
import datamapper.Author;
import io.FileWriterWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

public class RinzParser {
    private static final Logger logger  = LoggerFactory.getLogger(RinzParser.class);
    private Author author;

    public RinzParser (){
        this.author = new Author("Терехов", "Андрей", "Николаевич");
        startResearch(this.author);
    }
    public RinzParser(String name, String surname){
        this.author = new Author(name, surname);
        startResearch(this.author);
    }


    private void startResearch (Author authorInfo){
        // __________authorization______________
        ElibAuthorize auth = new ElibAuthorize();

        //___________search by author____________________
        HtmlPage startPage = auth.getElibraryStartPage();
        HtmlPage authPage  = navigateToAuthorsPage(startPage);
        HtmlPage resPage   = authorSearch(this.author, authPage);

        FileWriterWrap.writePageIntoFile(resPage, "authorSearchRezults");
        handleSearchResults(resPage);

        try{
            //TODO: for debugging, change call hierarchy
            HtmlPage publicationsPage = this.author.navigateToPublications();
            this.author.collectAuthorsPublications(publicationsPage);

            FileWriterWrap.writePageIntoFile(publicationsPage, "authorsPublicationsPage");
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        // HtmlPage firstSearchRez = defaultSearch(authorInfo, startPage);
        // logger.trace(firstSearchRez.toString());
    }

    /**
     * Search methods, usage depends on current page
     * @param authorInfo authors name
     * @param currentPage current page with form to insert
     * @return HtmlPage
     */
    private HtmlPage defaultSearch(Author authorInfo, HtmlPage currentPage){

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
        textField.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName());

        try{
            List<HtmlElement> listElements = form.getElementsByAttribute("div", "class", "butblue");
            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            FileWriterWrap.writePageIntoFile(resultPage,"basicSearchResults");

            //handleSearchResults(navigateToAuthorsPage(resultPage));
            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            logger.error("error during search call");
            return currentPage;
        }
    }
    private HtmlPage authorSearch(Author authorInfo, HtmlPage currentPage){

        //________________trace_____________________
        List<HtmlForm> forms = currentPage.getForms();
        for (HtmlForm form : forms) {
            logger.debug(form.toString());
        }

        HtmlTextInput surnameInput = currentPage.getHtmlElementById("surname");

        // check if patronymic was inserted
        if(authorInfo.getPatronymic()!=null)
            surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName()+" "+ authorInfo.getPatronymic());
        else
            surnameInput.setValueAttribute(authorInfo.getSurname()+" "+ authorInfo.getName());


        try {
            HtmlForm form = currentPage.getFormByName("results");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");

            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            FileWriterWrap.writePageIntoFile(resultPage, "authorSearchResults");

            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            return currentPage;
        }

    }

    private HtmlPage navigateToAuthorsPage (HtmlPage startPage){
        HtmlAnchor anchor = startPage.getAnchorByHref("/authors.asp");
        logger.trace(anchor.toString());

        try{
            FileWriterWrap.writePageIntoFile((HtmlPage) anchor.openLinkInNewWindow(), "authorsSearchPage");
            return (HtmlPage) anchor.openLinkInNewWindow();
        }
        catch (MalformedURLException ex){
            logger.error(ex.getMessage());
        }
        return startPage;
    }
    private void handleSearchResults (HtmlPage curPage){
        logger.debug(getLinksToAuthors(curPage).toString());
        final HtmlTable rezultsTable = curPage.getHtmlElementById("restab");

        for (final HtmlTableRow row : rezultsTable.getRows()){
           logger.info(row.getAlignAttribute());
           for (final HtmlTableCell cell : row.getCells()){
               logger.info(cell.asText());
           }
       }
       logger.info("_______end__________");
    }

    private List<String> getLinksToAuthors (HtmlPage curPage){
        List<String> result = new LinkedList<>();
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

                this.author.linkToUser = "http://elibrary.ru/" + value;
            }
        }

        return result;
    }
    private HtmlPage getNextPage(HtmlPage page) {

        try {
            List b = page.getByXPath("//*[text()='Следующая страница']/@href");
            String link = ((DomAttr) b.get(0)).getNodeValue();

            List<HtmlElement> list = page.getFormByName("results").getElementsByAttribute("a", "href",
                    link);
            HtmlPage nextPage = list.get(0).click();
            return nextPage;
        } catch (Throwable ex) {
            return null;
        }
    }

}
