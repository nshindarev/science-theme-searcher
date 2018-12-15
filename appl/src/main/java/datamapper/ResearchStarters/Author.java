package datamapper.ResearchStarters;

import datamapper.Publication;
import datamapper.ResearchPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class Author extends ResearchPoint {

    private final Logger logger = LoggerFactory.getLogger(Author.class);

    private String name;
    private String surname;
    private String patronymic;
    private String cluster;

    /**
     * initials:
     *   n = name
     *   p = patronymic
     */
    private char n;
    private char p;


    public String linkToUser;

    public Author(String surname, String name){
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
       char n = this.name == null ? this.n : this.name.charAt(0);
        if(Character.isLetter(n)) return n;
        else return '\u0000';
    }
    public char getP(){
        char p = this.patronymic == null? this.p : this.patronymic.charAt(0);
        if(Character.isLetter(p)) return p;
        else return '\u0000';
    }
    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPatronymic() { return patronymic; }
    public String getCluster() {return cluster;}
    public Set<Publication> getPublications() { return publications; }
    public Set<Author> getCoAuthors(){ return coAuthors; }
    public void setCluster(String cluster){
        if (this.cluster == null) this.cluster = cluster;
    }

    public static Author convertStringToAuthor (String auth){
        ArrayList<String> surname_n_p = new ArrayList<>(Arrays.asList(auth.split(" ")));

        if (surname_n_p.get(0).isEmpty())
            surname_n_p.remove(0);

        try{
            char n = surname_n_p.get(1).charAt(0);
            char p;

            if (surname_n_p.get(1).length()>=3) {
                p = surname_n_p.get(1).charAt(2)!='.'?surname_n_p.get(1).charAt(2):' ';
            }
            else p = ' ';

            return new Author(surname_n_p.get(0), n, p);
        }
        catch (IndexOutOfBoundsException ex){
            LoggerFactory.getLogger(Author.class).warn("author "+ surname_n_p.get(0)+" has only surname and cannot be analyzed");        }
        return null;
    }


    @Override
    public boolean equals (Object o){
        if (o == this) return true;
        if (!(o instanceof Author)) return false;

        Author auth = (Author) o;
        if (this.surname!= null && auth.surname!=null){
            if(Character.isLetter(this.n) && Character.isLetter(auth.getN())){
                if(Character.isLetter(this.p) && Character.isLetter(auth.getP())){
                    if (this.n == auth.n && this.p == auth.p) return true;
                }
                else if (this.n == auth.n) return true;
            }
            else if (this.surname.equals(auth.surname)) return true;
        }
        return false;
    }


    @Override
    public int hashCode(){
        if (patronymic!=null && name !=null)
          return Objects.hash(name,surname,patronymic);

        return Objects.hash(n,surname);
    }

    @Override
    public String toString(){
        if(this.getPatronymic()!=null)
            return (this.getSurname()+" "+ this.getName()+" "+ this.getPatronymic());
        else if (this.getName()!=null)
            return (this.getSurname()+" "+ this.getName());
        else if (Character.isLetter(this.getP()))
            return (this.getSurname()+" "+ this.getN() + ". "+ this.getP()+".");
        else if (Character.isLetter(this.getN()))
            return (this.getSurname()+" "+ this.getN()+".");
        else return (this.getSurname());
    }
}
