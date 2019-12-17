package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "author")
@Table(name = "author", schema = "science_theme_searcher")
public class Author {

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
    @Column(name = "patronymic")
    private String patronymic;

    @Getter
    @Setter
    @Column(name = "surname")
    private String surname;

    @Getter
    @Setter
    @Column(name = "abbreviationn")
    private String abbreviationn;

    @Getter
    @Setter
    @Column(name = "abbreviations")
    private String abbreviations;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "authortopublication", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_author"),
            inverseJoinColumns = @JoinColumn(name = "id_publication"))
    private Set<Publication> publications = new HashSet<>();

    public void addPublication(Publication publication) {
        this.publications.add(publication);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "clustertoauthor", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_author"),
            inverseJoinColumns = @JoinColumn(name = "id_cluster"))
    public Set<Cluster> clusters = new HashSet<>();

    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "linktoauthor", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_author"),
            inverseJoinColumns = @JoinColumn(name = "id_link"))
    private Set<Link> links = new HashSet<>();

    public void addLink(Link link) {
        this.links.add(link);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author that = (Author) o;
        if (id != that.id) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(surname, that.surname)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Author {" +
                "id: " + id +
                ", name: '" + name + '\'' +
                ", surname: '" + surname + '\'' +
                '}';
    }
}