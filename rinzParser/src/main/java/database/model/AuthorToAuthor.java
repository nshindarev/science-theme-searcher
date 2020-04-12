package database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "authortoauthor")
@Table(name = "authortoauthor", schema = "science_theme_searcher")
public class AuthorToAuthor {

    public AuthorToAuthor(){

    }
    public AuthorToAuthor(Author author_first, Author author_second) {
        this.author_first = author_first;
        this.author_second = author_second;
        this.weight = 1;
        this.id = Integer.toString(author_first.getId())+Integer.toString(author_second.getId());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorToAuthor that = (AuthorToAuthor) o;

        //if (id != that.id) return false;

        if (!Objects.equals(author_first, that.author_first) &&  !Objects.equals(author_first, that.author_second)) return false;
        if (!Objects.equals(author_second, that.author_second) && !Objects.equals(author_second, that.author_first)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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