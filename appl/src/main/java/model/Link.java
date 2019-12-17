package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "link")
@Table(name = "link", schema = "science_theme_searcher")
public class Link {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @Column(name = "url")
    private String url;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "linktoauthor", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_link"),
            inverseJoinColumns = @JoinColumn(name = "id_author"))
    private Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link that = (Link) o;
        if (id != that.id) return false;
        if (!Objects.equals(url, that.url)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Link {" +
                "id: " + id +
                ", url: '" + url + '\'' +
                '}';
    }
}
