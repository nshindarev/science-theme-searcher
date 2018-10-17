package datamapper;

import java.util.HashSet;
import java.util.Set;

public class Theme {

    private String name;


    public Theme (String name){
        this.name = name;
        this.authors = new HashSet<>();
    }

    public String getName() {
        return name;
    }
    public Set<Author> authors;
}
