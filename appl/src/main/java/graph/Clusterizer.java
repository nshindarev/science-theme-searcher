package graph;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import datamapper.ResearchStarters.Author;

import io.Serializer;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
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

    private DefaultUndirectedGraph authorsGraph;

    private String[] names = new String[] { "O1", "O2", "03","04","05","06","07","08","09"};
    private double[][] distances;


    public Clusterizer (){
        this.authorsGraph = convertDbToGraph();
        evaluateDistances(this.authorsGraph);
    }

    public Clusterizer(boolean serialized){
        if (serialized){
            AuthorsDB.initAuthorsStorage();
            AuthorsDB.initPublicationsStorage();
            AuthorsDB.addToAuthorsStorage(Serializer.deserializeData());
        }

        this.authorsGraph = convertDbToGraph();
        evaluateDistances(this.authorsGraph);
        clustering();
    }

    public static DefaultUndirectedGraph convertDbToGraph(){
        DefaultUndirectedGraph<Author, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        for (Author auth: AuthorsDB.getAuthorsStorage()){
            graph.addVertex(auth);
            for(Author auth1: auth.coAuthors){
                graph.addVertex(auth1);
                graph.addEdge(auth, auth1);
            }
        }

        Serializer.serializeData(graph);
        return graph;
    }
    public double[][] evaluateDistances(DefaultUndirectedGraph graph) {
        FloydWarshallShortestPaths shortestPaths = new FloydWarshallShortestPaths(this.authorsGraph);

        List<Author> authorList = new LinkedList<Author>(graph.vertexSet());
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

        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
        names = generateNames();
        Cluster cluster = alg.performClustering(distances, names,
                new AverageLinkageStrategy());

        dp.setModel(cluster);
        frame.setVisible(true);



        getClusters(cluster);
        int i = 0;
        for(Cluster cl: getClusters(cluster)){
            logger.info("___________________________");
            logger.info("LEAFS OF CLUSTER NUMBER "+ i);
            logger.info(dfs(cl).toString());
            i++;

        }
    }


    public List<Cluster> getClusters (Cluster root){

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

        logger.info(visited.toString());
        return visited;
    }


    //___________help methods__________________________
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
    public String[] generateNames(){

        String[] names = new String[authorsGraph.vertexSet().size()];
        for(int i = 0; i < authorsGraph.vertexSet().size(); i++){
            names[i] = Integer.toString(i);
        }
        return names;
    }
    public Set<Cluster> dfs (Cluster cluster){
        Set<Cluster> leafs = new HashSet<>();

        for(Cluster cl: cluster.getChildren()){
            if(cl.isLeaf()){
                leafs.add(cl);
            }
            else {
                for(Cluster dfs_leaf: dfs(cl))
                    leafs.add(dfs_leaf);
            }
        }
        return leafs;
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
}