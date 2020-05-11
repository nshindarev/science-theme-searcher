package database.model;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.*;

@Entity(name = "affiliation")
@Table(name = "affiliation", schema = "science_theme_searcher")
public class Affiliation {

    @Id
    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @ManyToMany()
    @JoinTable(name = "authortoaffiliation", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_affiliation"),
            inverseJoinColumns = @JoinColumn(name = "id_author"))
    private Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public Affiliation () { }

    public Affiliation (String name){
        this.name = name;
        this.id = hashCode();
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Affiliation {" +
                "id: " + id +
                ", name: '" + name + '\'' +
                '}';
    }
}