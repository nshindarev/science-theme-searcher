package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "keyword")
@Table(name = "keyword", schema = "science_theme_searcher")
public class Keyword {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @Column(name = "keyword")
    private String keyword;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "clustertokeyword", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_keyword"),
            inverseJoinColumns = @JoinColumn(name = "id_cluster"))
    private Set<Cluster> clusters = new HashSet<>();

    public Keyword (String key){
        keyword = key;
    }
    public void addCluster(Cluster cluster) {
        this.clusters.add(cluster);
    }

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "keywordtopublication", schema = "science_theme_searcher",
            joinColumns = @JoinColumn(name = "id_keyword"),
            inverseJoinColumns = @JoinColumn(name = "id_publication"))
    private Set<Publication> publications = new HashSet<>();

    public void addPublication(Publication publication) {
        this.publications.add(publication);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword that = (Keyword) o;
        if (id != that.id) return false;
        if (!Objects.equals(keyword, that.keyword)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (keyword != null ? keyword.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Keyword {" +
                "id: " + id +
                ", keyword: '" + keyword + '\'' +
                '}';
    }
}
