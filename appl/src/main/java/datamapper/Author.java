package datamapper;

import com.gargoylesoftware.htmlunit.html.*;
import io.FileWriterWrap;
import io.LogStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;
import util.Navigator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class Author {

    private final Logger logger = LoggerFactory.getLogger(Author.class);

    private String name;
    private String surname;
    private String patronymic;
    public Set<Publication> publications = new HashSet<>();
    public Set<Author> coAuthors = new HashSet<>();

    /**
     * initials:
     *   n = name
     *   p = patronymic
     */
    private char n;
    private char p;


    public String linkToUser;

    public Author(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
    public Author(String surname, String name, String patronymic){
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;

        this.n = getN();
        this.p = getP();
    }
    public Author(String surname, char n, char p){
        this.surname = surname;
        this.n = n;
        this.p = p;
    }


    public char getN(){
       return this.name == null ? this.n : this.name.charAt(0);
    }
    public char getP(){
        return this.patronymic == null? this.p : this.patronymic.charAt(0);
    }
    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPatronymic() { return patronymic; }
    public Set<Publication> getPublications() { return publications; }
    public Set<Author> getCoAuthors(){ return coAuthors; }


//    public HtmlPage navigateToAuthor() throws IOException{
//        if (this.linkToUser != null){
//            return Navigator.webClient.getPage(this.linkToUser);
//        }
//        else return null;
//    }
//    public HtmlPage navigateToPublications() throws IOException{
//
//            HtmlPage  authorsPage = this.navigateToAuthor();
//
//
//            for (HtmlForm form: authorsPage.getForms()){
//                logger.info(form.toString());
//            }
//
//            FileWriterWrap.writePageIntoFile(authorsPage, "authPage");
//            HtmlForm form = authorsPage.getFormByName("results");
//            HtmlAnchor referenceToPublications = (HtmlAnchor)form.getElementsByAttribute("a", "title", "Список публикаций автора в РИНЦ").get(0);
//
//            return (HtmlPage) referenceToPublications.openLinkInNewWindow();
//    }
//    public HtmlPage navigateToNextPublications(HtmlPage page){
//        try{
//            HtmlForm form = page.getFormByName("results");
//            HtmlAnchor anch = (HtmlAnchor) form.getElementsByAttribute("a", "title", "Следующая страница").get(0);
//
//            return (HtmlPage) anch.openLinkInNewWindow();
//        }
//        catch (MalformedURLException ex){
//            ex.printStackTrace();
//            return null;
//        }
//        catch (IndexOutOfBoundsException ex){
//            return null;
//        }
//    }


//    public void collectAuthorsPublications(HtmlPage publicationsPage){
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

    public static Author convertStringToAuthor (String auth){
        ArrayList<String> surname_n_p = new ArrayList<>(Arrays.asList(auth.split(" ")));

        if (surname_n_p.get(0).isEmpty())
            surname_n_p.remove(0);

        char n = surname_n_p.get(1).charAt(0);
        char p;

        if (surname_n_p.get(1).length()>=3) {
            p = surname_n_p.get(1).charAt(2)!='.'?surname_n_p.get(1).charAt(2):' ';
        }
        else p = ' ';

        return new Author(surname_n_p.get(0), n, p);
    }

    @Override
    public boolean equals (Object o){
        if (o == this) return true;
        if (!(o instanceof Author)) return false;

        Author auth = (Author) o;
        if (this.name != null && this.patronymic != null){
            if(auth.getName().equals(this.name)
                    && auth.getSurname().equals(this.getSurname())
                    && auth.getPatronymic().equals(this.getPatronymic())) return true;
        }
        else if (Character.isLetter(this.n)){
            if(auth.getSurname().equals(this.surname)
                    && auth.getN() == this.n) return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        if (patronymic!=null && name !=null)
          return Objects.hash(name,surname,patronymic);

        return Objects.hash(n,surname);
    }
}
