package main;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Cluster;
import database.model.Keyword;
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
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Navigator.keyword = new Keyword("базы данных");
        String requestKeyword = "базы данных";


        /**
         *  parser
         */
        LogIntoElibrary.withoutAuth();
        new Parser(new Keyword(requestKeyword)).parse();

        /**
         *  map synonymous accounts
         */
//        SynonymyService synonymyService = new SynonymyServiceImpl();
//        synonymyService.authorsSearchForSynonyms();

        /**
         *  get graph from DB
         */
//        DefaultDirectedGraph authorsGraph = StorageHandler.getAuthorsGraph();

        /**
         *  Cluster graph
         */
//        Clusterer cluster = new Clusterer(authorsGraph);
//        cluster.executeClustering();

        /**
         *  UI
         */
//        GraphVisualizer visualizer = new GraphVisualizer((DefaultDirectedGraph) cluster.getGraph());
//        GraphVisualizer visualizer = new GraphVisualizer((DefaultDirectedGraph) authorsGraph);
//        visualizer.visualize();

        /**
         * Gephi clustering
         */
//
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

//        Author test1 = new Author("Test1", "1", "1");
//        Author test2 = new Author("Test2", "2", "2");
//        AuthorService authorService = new AuthorService();
//        authorService.openConnection();
//        authorService.closeConnection();
//        AuthorToAuthorService authorToAuthorService = new AuthorToAuthorService();
//        AuthorToAuthor authorToAuthor = new AuthorToAuthor(test1,test2);
//        authorToAuthorService.openConnection();
//        authorToAuthorService.saveAuthorToAuthor(authorToAuthor);
//        authorToAuthorService.closeConnection();
    }

}
