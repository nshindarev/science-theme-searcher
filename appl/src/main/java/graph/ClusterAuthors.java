package graph;

import datamapper.ResearchStarters.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClusterAuthors {

    public Logger logger = LoggerFactory.getLogger(ClusterAuthors.class);

    //TODO: equals определяется на основе имени
    public Set<Author> authors;
    public String clusterName;
    public double[][] distances;

    public ClusterAuthors (){
        this.authors = new HashSet<>();
    }

    public ClusterAuthors(String name){

        this.authors = new HashSet<>();
        this.clusterName = name;
    }

    public ClusterAuthors(String name, double[][] distances){

        this.authors = new HashSet<>();
        this.clusterName = name;
        this.distances = distances;
    }

    @Override
    public boolean equals (Object o){
        if (o == this) return true;
        if (!(o instanceof ClusterAuthors)) return false;

        ClusterAuthors auth = (ClusterAuthors) o;
        if (this.clusterName!= null && auth.clusterName!=null){
           return this.clusterName.equalsIgnoreCase(auth.clusterName);
        }

        return false;
    }


    @Override
    public int hashCode(){
        if (clusterName!=null && authors !=null)
            return Objects.hash(clusterName,authors);

        return Objects.hash(authors);
    }

    @Override
    public String toString(){
        return "=====CLUSTER===== " + this.clusterName + " AUTHORS SIZE = " + this.authors.size();
    }
}
