package io;

import datamapper.ResearchPoint;
import datamapper.ResearchStarters.Author;
import graph.ClusterAuthors;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.AsSubgraph;
import storage.AuthorsDB;
import datamapper.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static storage.AuthorsDB.authorsInCluster;

public class LogStatistics {
    private static final Logger logger = LoggerFactory.getLogger(LogStatistics.class);
    private LogStatistics(){

    }
    public static void logAuthorsPublications(ResearchPoint auth){
        logger.debug("___________________________ ");
        logger.debug("___Statistics For Point____ ");
        logger.debug("___________________________ ");

        logger.debug(auth.toString());
        for(Publication pub: auth.publications){
            logger.debug(pub.toString());
        }
        logger.debug("------------------------ ");
    }
    public static void logAuthorsDB_auth (){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        logger.debug("________________________ ");
        logger.debug("DB STATE ON " + dateFormat.format(date));
        logger.debug("STORAGE SIZE "+ AuthorsDB.getAuthorsStorage().size());
        logger.debug("________________________");

        for (Author a : AuthorsDB.getAuthorsStorage()){
            logger.debug(a.getSurname()+" "+a.getN()+". "+a.getP()+".");
        }

    }
    public static void logAuthorsDB_auth (Set<Author> authors){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        logger.debug("________________________ ");
        logger.debug("DB CONDITION ON " + dateFormat.format(date));
        logger.debug("STORAGE SIZE "+ AuthorsDB.getAuthorsStorage().size());
        logger.debug("________________________");

        for (Author a : AuthorsDB.getAuthorsStorage()){
            logger.debug(a.getSurname()+" "+a.getN()+". "+a.getP()+".");
        }

    }

    public static void logClustersStatistics(List<AsSubgraph> splittedGraph){
        for (AsSubgraph subGraph: splittedGraph){
            logger.debug(subGraph.toString());
        }
    }

    public static void logDistancesMatrixNames(String[] names){
        logger.debug(Arrays.toString(names));
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

                logger.debug(str + "|");
                str = "|\t";
            }

        }
        catch(Exception e){logger.error("Matrix is empty!!");}
    }

    public static void logClusterInfoUpdates (List<Author> authorsToUpdate){

        logger.debug("________________________");
        logger.debug("NEW CLUSTER UPDATE");
        logger.debug("________________________");

        for(Author auth: authorsToUpdate){
            logger.debug(auth.toString()+ " CLUSTER: "+ auth.getCluster());
        }
    }

    public static void logClusterObj (Set<ClusterAuthors> clusters){
        for(ClusterAuthors cl : clusters){
            logger.info(cl.toString());
        }
    }

    public static void logFinalParseStatistic(AbstractGraph graph){

        logger.info("=========================");
        logger.info("=====FINAL STATISTIC=====");
        logger.info("=========================");

        logger.info("Authors to analyze size:      " + AuthorsDB.getAuthorsStorage().size());
        logger.info("All analyzed authors:         " + graph.vertexSet().size());

        authorsInCluster.forEach((numberOfComponent, setClusters )->{
            int[] iarr = {0}; // final not neccessary here if no other array is assigned
            setClusters.forEach((set) -> iarr[0] +=set.authors.size());

            if (iarr[0]!=0)
                logger.info("elements in component "+ numberOfComponent+  " =  " +iarr[0]);

        });

        logger.info("Connected components: " + authorsInCluster.size());
    }

}
