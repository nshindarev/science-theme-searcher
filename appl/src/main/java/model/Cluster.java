package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "cluster")
@Table(name = "cluster", schema = "science_theme_searcher")
public class Cluster {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "clustertoauthor", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_cluster"),
            inverseJoinColumns = @JoinColumn(name = "id_author"))
    private Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "clustertokeyword", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_cluster"),
            inverseJoinColumns = @JoinColumn(name = "id_keyword"))
    private Set<Keyword> keywords = new HashSet<>();

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cluster that = (Cluster) o;
        if (id != that.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Cluster {" +
                "id: " + id +
                '}';
    }
}
