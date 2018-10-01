import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.html.*;
import datamapper.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

public class RinzParser {
    private static final Logger logger  = LoggerFactory.getLogger(RinzParser.class);
    private User user;

    public RinzParser (){
        this.user = new User("Николай", "Терехов");
        startResearch(this.user);
    }
    public RinzParser(String name, String surname){
        this.user = new User (name, surname);
        startResearch(this.user);
    }


    private void startResearch (User userInfo){
        // __________authorization______________
        ElibAuthorize auth = new ElibAuthorize();

        //___________search by author____________________
        HtmlPage startPage = auth.getElibraryStartPage();
        HtmlPage authPage  = navigateToAuthorsPage(startPage);
        HtmlPage resPage   = authorSearch(this.user, authPage);

        handleSearchResults(resPage);


        HtmlPage firstSearchRez = defaultSearch(userInfo, startPage);
        logger.trace(firstSearchRez.toString());
    }

    /**
     * Search methods, usage depends on current page
     * @param userInfo authors name
     * @param currentPage current page with form to insert
     * @return HtmlPage
     */
    private HtmlPage defaultSearch(User userInfo, HtmlPage currentPage){

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
        textField.setValueAttribute(userInfo.getSurname()+" "+userInfo.getName());

        try{
            List<HtmlElement> listElements = form.getElementsByAttribute("div", "class", "butblue");
            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            writePageIntoFile(resultPage,"basicSearchResults");

            handleSearchResults(navigateToAuthorsPage(resultPage));
            return resultPage;
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
            logger.error("error during search call");
            return currentPage;
        }
    }
    private HtmlPage authorSearch(User userInfo, HtmlPage currentPage){

        //________________trace_____________________
        List<HtmlForm> forms = currentPage.getForms();
        for (HtmlForm form : forms) {
            logger.debug(form.toString());
        }

        HtmlTextInput surnameInput = currentPage.getHtmlElementById("surname");
        surnameInput.setValueAttribute(userInfo.getSurname());

        try {

            HtmlForm form = currentPage.getFormByName("results");
            List<HtmlElement> listElements = form.getElementsByAttribute("div","class", "butred");

            HtmlPage resultPage = listElements.get(0).click();

            //write results into file
            writePageIntoFile(resultPage, "authorSearchResults");

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
            writePageIntoFile((HtmlPage) anchor.openLinkInNewWindow(), "authorsSearchPage");
            return (HtmlPage) anchor.openLinkInNewWindow();
        }
        catch (MalformedURLException ex){
            logger.error(ex.getMessage());
        }
        return startPage;
    }
    private void handleSearchResults (HtmlPage curPage){
       final HtmlTable rezultsTable = curPage.getHtmlElementById("restab");
       for (final HtmlTableRow row : rezultsTable.getRows()){
           logger.info(row.getAlignAttribute());
           for (final HtmlTableCell cell : row.getCells()){
               logger.info(cell.asText());
           }
       }
       logger.info("_______end__________");
    }


    /**
     * logging
     * @param page to be logged into file as xml
     */
    private void writePageIntoFile (HtmlPage page){
        // write into file
        try (PrintWriter out = new PrintWriter("appl/src/main/resources/page.xml")) {
            out.println(page.asXml());
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }
    private void writePageIntoFile (HtmlPage page, String filename){
        // write into file
        try (PrintWriter out = new PrintWriter("appl/src/main/resources/"+filename+".xml")) {
            out.println(page.asXml());
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }

    //0verride zis
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
