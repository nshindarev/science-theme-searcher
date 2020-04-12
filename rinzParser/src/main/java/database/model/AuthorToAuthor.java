package database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "authortoauthor")
@Table(name = "authortoauthor", schema = "science_theme_searcher")
public class AuthorToAuthor {

    public AuthorToAuthor(){

    }
    public AuthorToAuthor(Author author_first, Author author_second) {
        this.author_first = author_first;
        this.author_second = author_second;
        this.weight = 1;
        this.id = Integer.toString(author_first.getId() + author_second.getId());
    }

    @Id
    private String id;

    @Getter
    @Setter
    @ManyToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="Id_First", referencedColumnName = "id")
    private Author author_first;

    @Getter
    @Setter
    @ManyToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="Id_Second", referencedColumnName = "id")
    private Author author_second;

    @Getter
    @Setter
    @Column(name = "Weight")
    private int weight;

    public void incrementWeight() {
        this.weight++;
    }

    @Override
    public String toString() {
        return "Connection {" +
                "First Author: " + author_first.getId() +
                ", Second Author: '" + author_second.getId() +
                ", Weight: '" + weight +
                '}';
    }

}