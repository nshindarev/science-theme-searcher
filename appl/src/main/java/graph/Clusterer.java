package graph;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.sun.istack.internal.NotNull;
import datamapper.ResearchStarters.Author;
import io.LogStatistics;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.AsSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;
import sun.rmi.runtime.Log;
import util.Navigator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Clusterer {

    public static final Logger logger = LoggerFactory.getLogger(Clusterer.class);
    public Clusterer(AbstractGraph graph){
        this.graph = graph;
    }
    public AbstractGraph graph;

    public void executeClustering (){

        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
        List<AsSubgraph> splittedGraph = this.splitConnectedComponents();
        List<Author> allAuthors = new LinkedList<>();

        /**
         * input: distances + names
         * output: list<cluster>
         */
        for (int i = 0; i < splittedGraph.size(); i++ ){

            Cluster rootCluster = alg.performClustering(evaluateDistances(splittedGraph.get(i)),
                    generateNames(splittedGraph.get(i),i),
                    new AverageLinkageStrategy());

            List<Cluster> cutClusters = getClusters(rootCluster);

            allAuthors.addAll(insertClustersIntoGraph(cutClusters, splittedGraph.get(i), new Integer(i)));
        }

        storeSearchResults(allAuthors);
    }

    public List<AsSubgraph> splitConnectedComponents(){
        List<AsSubgraph> subgraphs = new GabowStrongConnectivityInspector(graph).getStronglyConnectedComponents();

        for(AsSubgraph subgraph: subgraphs){
            subgraph.vertexSet().size();
        }

        LogStatistics.logClustersStatistics(subgraphs);
        return subgraphs;
    }


    public String[] generateNames(AsSubgraph connectedComponent, Integer clusterNumber){

        List<Author> authors =  new LinkedList<Author>(connectedComponent.vertexSet());
        String[] names = new String[connectedComponent.vertexSet().size()];

        for(int i = 0; i < connectedComponent.vertexSet().size(); i++){
            names[i] = Integer.toString(i);
        }

        LogStatistics.logDistancesMatrixNames(names);
        return names;
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

        LogStatistics.logDistancesMatrix(distances);
        return distances;
    }
    public List<Cluster> getClusters (@NotNull Cluster root){

        List<Cluster>  visited = new LinkedList<>();
        LinkedBlockingQueue<Cluster> clusterQueue = new LinkedBlockingQueue<>();


        try {
            clusterQueue.put(root);

            while (visited.size()< Navigator.clusterNumber){
                Cluster curGroup = clusterQueue.poll();
                visited.add(curGroup);
                for(Cluster child: curGroup.getChildren())
                    clusterQueue.add(child);
            }
        }
        catch (InterruptedException ex){
            logger.error(ex.getMessage());
        }
        catch (NullPointerException ex){
        }

        logger.info(visited.toString());
        return visited;
    }
    public List<Cluster> dfs (Cluster cluster){
        Set<Cluster> leafs = new HashSet<>();
        try{
            if (!cluster.isLeaf()){
                for(Cluster cl: cluster.getChildren()){
                    if(cl.isLeaf()){
                        leafs.add(cl);
                    }
                    else {
                        for(Cluster dfs_leaf: dfs(cl))
                            leafs.add(dfs_leaf);
                    }
                }
            }
            else {
                leafs.add(cluster);
            }
        }
        catch (NullPointerException ex){
            logger.error(ex.getMessage());
        }
        List rez = new LinkedList();
        rez.addAll(leafs);
        return rez;
    }



    // вызывается для каждой компоненты связности
    public List<Author> insertClustersIntoGraph(List<Cluster> cutClusters, AsSubgraph connectedComponent, Integer componentId){

        //номер кластера
        Integer number = 0;

        // index -> author
        List<Author> authInConComp = new LinkedList<>();
        authInConComp.addAll(connectedComponent.vertexSet());

        // все кластеры в к.с.
        for(Integer i = 0; i< cutClusters.size(); i++){

            // все листья из одного кластера
            try{
                for(Cluster leaf: dfs(cutClusters.get(i))){
                    authInConComp.get(new Integer(leaf.getName())).setCluster(componentId.toString()+"_"+i.toString());
                }
            }
            catch (IndexOutOfBoundsException ex){
                logger.error("iteration "+i+" exception" );
            }
        }

        LogStatistics.logClusterInfoUpdates(authInConComp);
        return authInConComp;
    }
    public void storeSearchResults(List<Author> allAuthorsToUpdate){
        for(Author toInsert: allAuthorsToUpdate){
            AuthorsDB.replaceInAuthStorage(toInsert);
        }
    }
}
