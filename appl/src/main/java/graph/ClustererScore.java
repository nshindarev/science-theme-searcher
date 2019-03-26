package graph;

import com.apporiented.algorithm.clustering.Cluster;
import datamapper.ResearchStarters.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClustererScore {

    public Logger logger = LoggerFactory.getLogger(ClustererScore.class);

    private double[][]    A;
    Map<Integer,Set<ClusterAuthors>> splittedGraph;

    public ClustererScore(double[][] distances){

//        this.A = distances;
//        for(Map.Entry<Integer,Set<ClusterAuthors>> entry  : AuthorsDB.authorsInCluster)
//        this.splittedGraph = AuthorsDB.authorsInCluster
//        logger.info(new Double(this.m()).toString());
    }

    public double QScore(){
        return 0;
    }

    public double sigma(){
        return 0;
    }

    public double m(){
        double sum = 0;
        for (int i=0; i<A[0].length; i++)
            for(int j=0; j<A[0].length; j++)
                sum  += A[i][j];

        return (0.5 * sum);
    }

    public double k(int i){
        double sum = 0;
        for(int j = 0; j<A[0].length; j++){
            sum += A[i][j];
        }

        return sum;
    }

    public int sigm(Author u, Author v){
        if (u.getCluster().equalsIgnoreCase(v.getCluster())) return 1;
        else return 0;
    }
}
