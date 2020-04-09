package database.model;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.*;

@Entity(name = "author")
@Table(name = "author", schema = "science_theme_searcher")
public class Author {

    public Author (){

    }
    public Author (String surname, String n, String p) {
        this.surname = surname;
        this.n = n;
        this.p = p;
        this.revision = 0;
        this.id = hashCode();
    }

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
    @Column(name = "patronymic")
    private String patronymic;

    @Getter
    @Setter
    @Column(name = "surname")
    private String surname;

    @Getter
    @Setter
    @Column(name = "n")
    private String n;

    @Getter
    @Setter
    @Column(name = "p")
    private String p;

    @Getter
    @Setter
    @Column(name = "revision")
    private Integer revision;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL,
                fetch = FetchType.EAGER)
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
    @ManyToMany(cascade = CascadeType.ALL,
                fetch = FetchType.EAGER)
    @JoinTable(name = "linktoauthor", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_author"),
            inverseJoinColumns = @JoinColumn(name = "id_link"))
    private Set<Link> links = new HashSet<>();

    public void addLink(Link link) {
        this.links.add(link);
    }

    @Getter
    @Setter
    @OneToMany(mappedBy = "author_second",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AuthorToAuthor> incomingAuthorToAuthors = new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "author_first",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AuthorToAuthor> outgoingAuthorToAuthors = new HashSet<>();

    public static Author convertStringToAuthor (String auth) {

        ArrayList<String> surname_n_p = new ArrayList<>(Arrays.asList(auth.split(" ")));

        if (surname_n_p.get(0).isEmpty())
            surname_n_p.remove(0);

        try{
            char n = surname_n_p.get(1).charAt(0);
            char p;

            if (surname_n_p.get(1).length()>=3) {
                p = surname_n_p.get(1).charAt(2)!='.'?surname_n_p.get(1).charAt(2):' ';
            }
            else p = ' ';

            return new Author(surname_n_p.get(0), String.valueOf(n), String.valueOf(p));
        }
        catch (IndexOutOfBoundsException ex){
            LoggerFactory.getLogger(Author.class).warn("author "+ surname_n_p.get(0)+" has only surname and cannot be analyzed");        }
        return null;
    }

    public Author join(Author author){
        if (this.equals(author)){
            author.getPublications()
                    .stream()
                    .filter(it -> !this.getPublications().contains(it))
                    .forEach(this::addPublication);
        }

        if (author.revision == 1)
            this.setRevision(1);

        return this;
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
        int result = surname != null ? surname.hashCode() : 0;
        result = 31 * result + (n != null ? n.hashCode() : 0);
        result = 1024 * result + (p != null ? p.hashCode() : 0);
        return result;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(surname, n, p);
//    }

//    @Override
//    public String toString() {
//        return "Author {" +
//                "id: " + id +
//                ", n: '" + n + '\'' +
//                ", p: '" + p + '\'' +
//                ", surname: '" + surname + '\'' +
//                '}';
//    }
    @Override
    public String toString() {
        return surname + " "+n + ". " + p + ".";
    }
}