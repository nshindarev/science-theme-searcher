package graph;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import com.sun.istack.internal.NotNull;
import datamapper.ResearchStarters.Author;

import io.Serializer;

import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;
import util.Navigator;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Clusterizer {

    private static final Logger logger = LoggerFactory.getLogger(Clusterizer.class);

    /**
     *  результирующая переменная, содержит перечень кластеров
     */
    public List<List<Author>> historyAuthorLists = new LinkedList<>();
    public List<Author> authorList;
    public Set<List<Author>> authorsConnectedComponents;

    private DefaultDirectedGraph authorsGraph;
    private StrongConnectivityAlgorithm connectivityAlgorithm;

    private String[] names = new String[] { "O1", "O2", "03","04","05","06","07","08","09"};
    private List<String[]> namesList;

    private double[][] distances;
    private List<double[][]> connectedComponentsDistances = new ArrayList<>();
    private List<AsSubgraph> subgraphs;


    public Clusterizer(boolean serialized){
        this.authorsConnectedComponents = new HashSet<>();

        if (serialized){
            AuthorsDB.initAuthorsStorage();
            AuthorsDB.initPublicationsStorage();
            AuthorsDB.addToAuthorsStorage(Serializer.deserializeData());
        }

        this.authorsGraph = convertDbToGraph();
        connectivityAlgorithm = new GabowStrongConnectivityInspector(authorsGraph);

        evaluateDistances(this.authorsGraph);
        clustering();
    }



    public void clustering () {
        JFrame frame = new JFrame();
        frame.setSize(1024, 768);
        frame.setLocation(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        DendrogramPanel dp = new DendrogramPanel();

        frame.setContentPane(content);

        content.setBackground(Color.red);
        content.setLayout(new BorderLayout());
        content.add(dp, BorderLayout.CENTER);

        dp.setBackground(Color.WHITE);
        dp.setLineColor(Color.BLACK);
        dp.setScaleValueDecimals(0);
        dp.setScaleValueInterval(1);
        dp.setShowDistances(false);

        List<Cluster> finalList = new LinkedList<>();
        this.connectedComponentsDistances = evaluateDistancesInGraph(authorsGraph);
//        generateNamesList(this.subgraphs);


        int j =0;
        for (int i = 0; i < subgraphs.size(); i++){
            ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
            names = generateNames(subgraphs.get(i));
            Cluster cluster = alg.performClustering(evaluateDistances(subgraphs.get(i)), names,
                    new AverageLinkageStrategy());

            List<Cluster> curClusters = getClusters(cluster);
            for(Cluster cl: curClusters){
                logger.trace("___________________________");
                logger.trace("LEAFS OF CLUSTER NUMBER "+ j);
                logger.trace(dfs(cl).toString());
                j++;
            }

            finalList.addAll(curClusters);
        }

        insertClustersIntoGraph(finalList);
        storeSearchResults();

//        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//        names = generateNames(authorsGraph);
//        Cluster cluster = alg.performClustering(distances, names,
//                new AverageLinkageStrategy());
//
//        dp.setModel(cluster);
//        frame.setVisible(true);
//
//
//        getClusters(cluster);
//        generateNamesList(this.subgraphs);
//        int i = 0;
//        for(Cluster cl: getClusters(cluster)){
//            logger.trace("___________________________");
//            logger.trace("LEAFS OF CLUSTER NUMBER "+ i);
//            logger.trace(dfs(cl).toString());
//            i++;
//        }
//
//        logger.info("");
//        insertClustersIntoGraph(getClusters(cluster));
//        printClusters();
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


    //___________help methods__________________________
    public static DefaultDirectedGraph convertDbToGraph(){
        DefaultDirectedGraph<Author, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Author auth: AuthorsDB.getAuthorsStorage()){
            graph.addVertex(auth);
            for(Author auth1: auth.coAuthors){
                graph.addVertex(auth1);
                graph.addEdge(auth, auth1);
                graph.addEdge(auth1, auth);

            }
        }

        Serializer.serializeData(graph);
        return graph;
    }
//    public List<String[]> generateNamesList(List<DefaultDirectedGraph> graphs){
//        List<String[]> rez = new LinkedList<>();
//        for(DefaultDirectedGraph graph: graphs){
//            rez.add(generateNames(graph));
//        }
//
//        this.namesList = rez;
//        return  rez;
//    }
    public String[] generateNames(AsSubgraph graph){


        String[] names = new String[graph.vertexSet().size()];
        for(int i = 0; i < graph.vertexSet().size(); i++){
            names[i] = Integer.toString(i);
        }
        return names;
    }
    public static List<Author> getAllAuthAsList (){
        List<Author> res = new ArrayList<>();
        res.addAll(AuthorsDB.getAuthorsStorage());

        for(Author authFirstLevel: res){
            for(Author authSecondLevel: authFirstLevel.coAuthors){
                if(!res.contains(authSecondLevel)) res.add(authSecondLevel);
            }
        }

        return res;
    }

    public List<double[][]> evaluateDistancesInGraph (AbstractGraph graph){
        subgraphs = connectivityAlgorithm.getStronglyConnectedComponents();

        List<double[][]> distances = new ArrayList<>(subgraphs.size());
        for(int i = 0; i< subgraphs.size(); i++){
            subgraphs.get(i);

            double[][] x =  evaluateDistances(subgraphs.get(i));
            distances.add(i,x);
        }

        return distances;
    }
    public double[][] evaluateDistances(AbstractGraph graph) {
        FloydWarshallShortestPaths shortestPaths = new FloydWarshallShortestPaths(this.authorsGraph);

        authorList = new LinkedList<Author>(graph.vertexSet());
        historyAuthorLists.add(authorList);
        distances = new double[authorList.size()][authorList.size()];

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

        printMatrix(distances);
        return distances;
    }
    public Set<Cluster> dfs (Cluster cluster){
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
        return leafs;

    }
    public void insertClustersIntoGraph(List<Cluster> clusters){

        Integer i = 0;
        for(Cluster subTree: clusters){
            try{
                Set<Cluster> leafs = dfs(subTree);
                for(Cluster leaf: leafs){
                    historyAuthorLists.get(i).get(new Integer(leaf.getName())).setCluster(i.toString());
                }
                i++;
            }
            catch (IndexOutOfBoundsException ex){
                logger.error("iteration "+i+" exception" );
            }
        }

    }
    public void storeSearchResults(){
        for(Author toInsert: this.authorList){
            AuthorsDB.replaceInAuthStorage(toInsert);
        }

        logger.info(AuthorsDB.getAuthorsStorage().toString());
    }
    //_____________print______________________
    public void printMatrix(double[][] m){
        try{
            int rows = m.length;
            int columns = m[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                for(int j=0;j<columns;j++){
                    str += m[i][j] + "\t";
                }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }
    public void printClusters(){
        for(Author auth: this.authorList){
           logger.debug(auth.toString() + "CLUSTER: "+ auth.getCluster());
        }
    }

}