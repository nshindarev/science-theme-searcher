package main;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Cluster;
import database.operations.StorageHandler;
import database.service.AuthorService;
import database.service.AuthorToAuthorService;
import database.service.ClusterService;
import database.service.KeywordService;
import elibrary.auth.LogIntoElibrary;
import elibrary.parser.Navigator;
import elibrary.parser.Parser;
import database.model.Keyword;
import graph.Clusterer;
import graph.gephi.GephiClusterer;
import graph.ui.GraphVisualizer;
import implementation.SuggestingServiceImpl;
import implementation.SynonymyServiceImpl;
import org.apache.commons.cli.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;
import service.SuggestingService;
import service.SynonymyService;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main (String[] args){


        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        String requestKeyword = "социоинженерные атаки";

        /**
         *  parser
         */
//        LogIntoElibrary.auth();
//        new Parser(new Keyword(requestKeyword)).parse();

        /**
         *  map synonymous accounts
         */
//        SynonymyService synonymyService = new SynonymyServiceImpl();
//        synonymyService.authorsSearchForSynonyms();

        /**
         *  get graph from DB
         */
        DefaultDirectedGraph authorsGraph = StorageHandler.getAuthorsGraph();

        /**
         *  Cluster graph
         */
        Clusterer cluster = new Clusterer(authorsGraph);
        cluster.executeClustering();

        /**
         *  UI
         */
        GraphVisualizer visualizer = new GraphVisualizer((DefaultDirectedGraph) cluster.getGraph());
//        GraphVisualizer visualizer = new GraphVisualizer((DefaultDirectedGraph) authorsGraph);
        visualizer.visualize();

        /**
         * Gephi clustering
         */

//        GephiClusterer gc = new GephiClusterer();
//        gc.action();
//        logger.info("FINISHED CLUSTERING");


        /**
         *  save clusters into DB
         */
//        StorageHandler.getTopAuthors(gc.sortRecommendations(),7, 3);
//        StorageHandler.saveClusters(gc.getClusters());
//        logger.info("REACHED ENDPOINT");

        /**
         *  top authors
         */
//        StorageHandler.getTopAuthors(gc.sortRecommendations(),7, 3);

//        StorageHandler.saveClusters(gc.getClusters());

//        SuggestingService suggestingService = new SuggestingServiceImpl();
//        List<String> resultSet = suggestingService.executeSuggestionQuery(requestKeyword);
//        Iterator<String> iterator = resultSet.iterator();
//        while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }

    }

    public static void _main (String[] args){
        /**
         *  disable logs
         */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        Parameters params = parseParameters(args);
        if (params.keyword != ""){

            /**
             *  parser
             */
            if (params.parser){
                LogIntoElibrary.auth();
                new Parser(Navigator.keyword).parse();
            }

            /**
             *  map synonymous accounts
             */
            if (params.synonymy){
                SynonymyService synonymyService = new SynonymyServiceImpl();
                synonymyService.authorsSearchForSynonyms();
            }

            if (params.clustererOld){
                /**
                 *  get graph from DB
                 */
                DefaultDirectedGraph authorsGraph = StorageHandler.getAuthorsGraph();

                /**
                 *  Cluster graph
                 */
                Clusterer cluster = new Clusterer(authorsGraph);
                cluster.executeClustering();

                /**
                 *  UI
                 */
                GraphVisualizer visualizer = new GraphVisualizer((DefaultDirectedGraph) cluster.getGraph());
                visualizer.visualize();
            }

            /**
             * Gephi clustering
             */
            if (params.clustererNew){
                GephiClusterer gc = new GephiClusterer();
                gc.action();

                //save clusters into DB
                StorageHandler.getTopAuthors(gc.sortRecommendations(),7, 3);
                StorageHandler.saveClusters(gc.getClusters());
                logger.info("REACHED ENDPOINT");

            }


            /**
             *  top authors
             */
//        StorageHandler.getTopAuthors(gc.sortRecommendations(),7, 3);

//        StorageHandler.saveClusters(gc.getClusters());
        }


    }
    private static final String keyword =                "keyword";
    private static final String searchLimit =            "searchLimit";
    private static final String searchLevel=             "searchLevel";
    private static final String parser =                 "parser";
    private static final String synonymy =               "synonymy";
    private static final String clustererNew =           "clustererNew";
    private static final String clustererOld =           "clustererOld";
    private static final String minClusterSize =         "minClusterSize";
    private static final String topVerticesCardinality = "topVerticesCardinality";


    public static Parameters parseParameters( String... args){

        Options options = new Options();

        options.addOption(keyword,true,  "publications with this keyword would be analysed");
        options.addOption(searchLimit,true,  "limits number of coAuthors to be added foreach author during search");
        options.addOption(searchLevel,true,  "number of search iterations");
        options.addOption(searchLevel,true,  "number of search iterations");
        options.addOption(searchLevel,true,  "number of search iterations");

        options.addOption(parser,true,  "set true, to proceed with elibrary parsing level");
        options.addOption(synonymy,true,  "set true, to proceed with synonymy preprocessing level");
        options.addOption(clustererNew,true, "if true, uses gephi toolkit to cluster");
        options.addOption(clustererOld,true, "if true, uses jgrapht to cluster ");


        Parameters parameters = new Parameters();
        boolean params = true;
        CommandLine cl;

        try {
            cl = new DefaultParser().parse(options, args);

            if (cl.hasOption(keyword)){
                parameters.keyword = keyword;
                parameters.searchLimit = cl.hasOption(searchLimit)?Integer.parseInt(searchLimit):Integer.MAX_VALUE;
                parameters.searchLevel = cl.hasOption(searchLevel)?Integer.parseInt(searchLevel):Integer.MAX_VALUE;
                parameters.parser = !cl.hasOption(parser) || Boolean.getBoolean(parser);
                parameters.synonymy = !cl.hasOption(synonymy) || Boolean.getBoolean(synonymy);
                parameters.clustererNew = !cl.hasOption(clustererNew) || Boolean.getBoolean(clustererNew);
                parameters.clustererOld = !cl.hasOption(clustererOld) || Boolean.getBoolean(clustererOld);
            }
                if (cl == null){
                    HelpFormatter helpFormatter = new HelpFormatter();
                    helpFormatter.setWidth(132);
                    helpFormatter.printHelp("arguments", options);
                }
        }
        catch (ParseException ex){
            logger.error("Couldn't parse line: {}", ex.getLocalizedMessage());
            params = false;
        }

        if(params) return parameters;
        else return null;
    }


}
