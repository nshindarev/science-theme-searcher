package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "publication", schema = "science_theme_schema")
public class Publication {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Column(name = "annotation")
    private String annotation;

    @Getter
    @Setter
    @Column(name = "descriptioneng")
    private String descriptioneng;

    @Getter
    @Setter
    @Column(name = "descriptionrus")
    private String descriptionrus;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "authortopublication",
            joinColumns = @JoinColumn(name = "id_publication"),
            inverseJoinColumns = @JoinColumn(name = "id_author"))
    private Set<Author> authors;

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "keywordtopublication",
            joinColumns = @JoinColumn(name = "id_publication"),
            inverseJoinColumns = @JoinColumn(name = "id_keyword"))
    private Set<Keyword> keywords;

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publication that = (Publication) o;
        if (id != that.id) return false;
        if (!Objects.equals(name, that.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Publication {" +
                "id: " + id +
                ", name: '" + name + '\'' +
                '}';
    }
}
