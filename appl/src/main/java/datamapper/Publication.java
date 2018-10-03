package datamapper;

import java.util.LinkedList;
import java.util.List;

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
}
