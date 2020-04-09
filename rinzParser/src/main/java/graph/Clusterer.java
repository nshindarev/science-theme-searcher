package graph;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.sun.istack.internal.NotNull;
import database.model.Author;
import elibrary.parser.Navigator;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.AsSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Clusterer {

    private static final Logger logger =
            LoggerFactory.getLogger(Clusterer.class);
    private static final int MIN_DEGREE = 1;

    private AbstractGraph graph;
    private double[][] graphDistances;

    public Clusterer(AbstractGraph graph){
        this.graph = graph;
    }

    public void executeClustering (){
        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();

        List<AsSubgraph> splittedGraph
                = trimSmallGraph(splitConnectedComponents());

        /**
         *  foreach connected component
         */
        for (int i = 0; i < splittedGraph.size(); i++ ) {
            double[][] subDistances = evaluateDistances(splittedGraph.get(i));

            for (Navigator.clusterNumber = 0; Navigator.clusterNumber < subDistances.length; Navigator.clusterNumber++) {
                Cluster rootCluster = alg.performClustering(subDistances,
                        generateNames(splittedGraph.get(i)),
                        new AverageLinkageStrategy());

                List<Cluster> cutClusters = getClusters(rootCluster);

            }
        }
    }


    private List<AsSubgraph> trimSmallGraph(List<AsSubgraph> subgraphs){
        return subgraphs.stream()
                .filter(subgraph -> subgraph.vertexSet().size() > MIN_DEGREE)
                .collect(Collectors.toList());
    }

    public AbstractGraph getGraph(){
        return this.graph;
    }
    public List<Cluster> getClusters (@NotNull Cluster root){

        List<Cluster>  visited = new LinkedList<>();
        LinkedBlockingQueue<Cluster> clusterQueue = new LinkedBlockingQueue<>();


        try {
            clusterQueue.put(root);

            while (clusterQueue.size()< Navigator.clusterNumber){
                Cluster curGroup = clusterQueue.poll();
                for(Cluster child: curGroup.getChildren()){
                    clusterQueue.add(child);
                }
                clusterQueue.remove(curGroup);
            }
        }
        catch (InterruptedException ex){
            logger.error(ex.getMessage());
        }
        catch (NullPointerException ex){
        }

        logger.info(visited.toString());
        return new LinkedList<>(clusterQueue);

    }
    public List<AsSubgraph> splitConnectedComponents(){
        List<AsSubgraph> subgraphs = new GabowStrongConnectivityInspector(graph).getStronglyConnectedComponents();

        for(AsSubgraph subgraph: subgraphs){
            logger.info(Integer.toString(subgraph.vertexSet().size()));
        }

        return subgraphs;
    }
    public double[][] evaluateDistances(AbstractGraph graph) {
        FloydWarshallShortestPaths shortestPaths = new FloydWarshallShortestPaths(graph);

        List<Author> authorList = new LinkedList<Author>(graph.vertexSet());
        double[][] distances    = new double[authorList.size()][authorList.size()];

        for (int i = 0; i < authorList.size(); i++){
            for (int j = i + 1; j < authorList.size(); j++) {
                try {
                    distances[i][j] = shortestPaths.getPath(authorList.get(i), authorList.get(j)).getLength();
                    distances[j][i] = distances[i][j];
                } catch (NullPointerException ex) {
                    distances[i][j] = 100;
                    distances[j][i] = distances[i][j];
                }
            }
        }
        logDistancesMatrix(distances);
        return distances;
    }
    public String[] generateNames(AsSubgraph connectedComponent){
        List<Author> authors =  new LinkedList<Author>(connectedComponent.vertexSet());
        String[] names = new String[connectedComponent.vertexSet().size()];

        for(int i = 0; i < connectedComponent.vertexSet().size(); i++){
            names[i] = Integer.toString(i);
        }

        return names;
    }
    public static void logDistancesMatrix(double[][] m){
        try{
            int rows = m.length;
            int columns = m[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                for(int j=0;j<columns;j++){
                    str += m[i][j] + "\t";
                }

                logger.trace(str + "|");
                str = "|\t";
            }

        }
        catch(Exception e){logger.error("Matrix is empty!!");}
    }
}
