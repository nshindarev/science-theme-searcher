package datamapper;

import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Author {

    private final Logger logger = LoggerFactory.getLogger(Author.class);

    private String name;
    private String surname;
    private String patronymic;


    public String linkToUser;
    private WebClient webClient;

    public Author(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
    public Author(String surname, String name, String patronymic){
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPatronymic() { return patronymic; }

    public HtmlPage navigateToAuthor() throws IOException{
        if (this.linkToUser != null){
            webClient = new WebClient();
            return webClient.getPage(this.linkToUser);
        }
        else return null;
    }
    public HtmlPage navigateToPublications() throws IOException{

            HtmlPage  authorsPage = this.navigateToAuthor();

            for (HtmlForm form: authorsPage.getForms()){
                logger.info(form.toString());
            }

            HtmlForm form = authorsPage.getFormByName("results");
            HtmlAnchor referenceToPublications = (HtmlAnchor)form.getElementsByAttribute("a", "title", "Список публикаций автора в РИНЦ").get(0);

            return (HtmlPage) referenceToPublications.openLinkInNewWindow();
    }

//    public List<Publication> getAuthorsPublication(HtmlPage page){
//        List<Publication> publications = new LinkedList<>();
//
//
//
//    }
    /*
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

        return result;*/

    //TODO:STOPPED HERE - NEED TO FIND EXACT TABLE USING FORM FILTER
    public void collectAuthorsPublications(HtmlPage publicationsPage){

        if (!AuthorsDB.authorsStorage.contains(this)){

            int l2 = publicationsPage.getFormByName("result").getElementsByTagName("table").size();
            int l1 = publicationsPage.getElementsByTagName("table").size();
            HtmlTable table = (HtmlTable) publicationsPage.getElementsByTagName("table").get(0);

            for (HtmlTableRow row : table.getRows()) {
                int l = row.getCells().size();

                HtmlElement name =    row.getCell(0).getElementsByTagName("a").get(0);
//              HtmlElement name =    row.getCell(1).getElementsByTagName("a").get(0);
//              HtmlElement authors = row.getCell(1).getElementsByTagName("i").get(0);

              logger.debug(name.asText());
//              logger.debug(authors.asText());
            }
        }
    }

    public String getFullName(){
        if (!name.isEmpty() && !surname.isEmpty()) return surname + " " + name;
        else if (name.isEmpty() || name.equals(null)) return surname;
        else if (surname.isEmpty() || surname.equals(null)) return name;
        else return "";
    }

    @Override
    public boolean equals (Object o){
        if (o == this) return true;
        if (!(o instanceof Author)) return false;

        Author auth = (Author) o;
        if(auth.getName().equals(this.name)
                && auth.getSurname().equals(this.getSurname())
                && auth.getPatronymic().equals(this.getPatronymic())) return true;
        else return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name,surname,patronymic);
    }
}
