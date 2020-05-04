package database.model;

import elibrary.parser.Navigator;
import elibrary.parser.Parser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "publication")
@Table(name = "publication", schema = "science_theme_searcher")
public class Publication {

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
    @Column(name = "annotation")
    private String annotation;

    @Getter
    @Setter
    @Column(name = "year")
    private Integer year;

    @Getter
    @Setter
    @Column(name = "link")
    private String link;

    @Getter
    @Setter
    @Column(name = "metric")
    private Integer metric;

    @Getter
    @Setter
    @ManyToMany()
    @JoinTable(name = "authortopublication", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_publication"),
            inverseJoinColumns = @JoinColumn(name = "id_author"))
    private Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "keywordtopublication", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_publication"),
            inverseJoinColumns = @JoinColumn(name = "id_keyword"))
    private Set<Keyword> keywords = new HashSet<>();


    public void addKeyword(Keyword keyword) {
        if (Parser.allKeywordPublicationIds.stream().anyMatch(this::equals)){
            this.keywords.add(keyword);
        }
    }

    public Publication () {

    }

    public Publication (String name){
        this.name = name;
        this.id = hashCode();
    }

    public Publication (String name, Integer metric){
        this.name = name;
        this.metric = metric;
        this.id = hashCode();
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

//    @Override
//    public int hashCode() {
//        int result = id;
//        result = 31 * result + (name != null ? name.hashCode() : 0);
//        return result;
//    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Publication {" +
                "id: " + id +
                ", name: '" + name + '\'' +
                '}';
    }
}
