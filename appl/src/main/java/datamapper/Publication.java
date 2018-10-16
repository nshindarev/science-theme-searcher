package datamapper;

import java.util.*;

public class Publication {

    private String name;
    private List<Author> authors;

    public Publication (String name){
        this.name = name;
        this.authors = new LinkedList<>();
    }

    public Publication(String name, List<Author> authors){
        this.name = name;
        this.authors = authors;
    }

    public Publication (String name, String authors){

        List<String> authInPubl = Arrays.asList(authors.split(","));
        this.authors = new ArrayList<Author>(authInPubl.size());
        this.name = name;

        for (String auth : authInPubl) {
            Author authObj = Author.convertStringToAuthor(auth);
            this.authors.add(authObj);
        }
    }

    public void setAuthors(List<Author> authors){
        this.authors = authors;
    }

    public String getName(){
        return this.name;
    }
    public List<Author> getAuthors(){
        return this.authors;
    }


    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (!(o instanceof Publication)) return false;

        Publication publication = (Publication) o;
        if (this.name.toLowerCase().equals(publication.getName().toLowerCase())) return true;
            else return false;
    }

    @Override
    public String toString (){
        return "PUBLICATION: " + this.name + " AUTHORS: " + this.authors.size();
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.name.toLowerCase());
    }
}
