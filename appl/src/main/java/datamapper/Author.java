package datamapper;

import auth.ElibAuthorize;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import io.FileWriterWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
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

    public void collectAuthorsPublications(HtmlPage publicationsPage){

        if (!AuthorsDB.authorsStorage.contains(this)) {

            //FileWriterWrap.printFormsEnum(publicationsPage);
            final HtmlTable rezultsTable = publicationsPage.getHtmlElementById("restab");

            for (final HtmlTableRow row : rezultsTable.getRows()) {
                if (row.getElementsByTagName("a").size()>0){
                    HtmlElement name = row.getElementsByTagName("a").get(0);
                    logger.debug(name.asText());
                }
                if (row.getElementsByTagName("i").size()>0){
                    HtmlElement name = row.getElementsByTagName("i").get(0);
                    logger.debug(name.asText());
                }
            }
            if(navigateToNextPublications(publicationsPage)!=null){
                publicationsPage = navigateToNextPublications(publicationsPage);
                collectAuthorsPublications(publicationsPage);
            }
            logger.debug("trace");
        }
    }

    public static HtmlPage navigateToNextPublications(HtmlPage page){
        try{
            HtmlForm form = page.getFormByName("results");
            HtmlAnchor anch = (HtmlAnchor) form.getElementsByAttribute("a", "title", "Следующая страница").get(0);

            return (HtmlPage) anch.openLinkInNewWindow();
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
        catch (IndexOutOfBoundsException ex){
            return null;
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
