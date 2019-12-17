package main;

import datamapper.ResearchStarters.Theme;
import graph.CitationGraphDB;
import graph.Clusterer;
import io.LogStatistics;
import io.Parameters;
import model.Author;
import model.Cluster;
import model.Keyword;
import org.apache.commons.cli.*;
import service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ClusterService;
import service.KeywordService;
import storage.AuthorsDB;
import util.Navigator;
import io.Serializer;


import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main (String[] args) throws IOException {
        Parameters params = parseParameters(args);

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        AuthorsDB.initAuthorsStorage();
        AuthorsDB.initPublicationsStorage();

        Navigator.webClient.setTimeout(Navigator.timeOut);

//      RinzParser parser = new RinzParser(new Author("Терехов", "Андрей", "Николаевич"));
//      RinzParser parser = new RinzParser(new Theme("социоинженерные атаки"));
//      RinzParser parser = new RinzParser(new Author("Галактионов", "Владимир", "Михайлович"));

        if (params.deserializeMode) {
             AuthorsDB.addToAuthorsStorage(Serializer.deserializeData());
             LogStatistics.logAuthorsDB_auth();
             logger.info("");
        }
        else {
            new RinzParser(params.startPoint).startResearch();
            Serializer.serializeData();
        }

        Navigator.webClient.closeAllWindows();

        Clusterer clusterizer = new Clusterer(Serializer.convertDbToGraph());

        clusterizer.splitConnectedComponents();
        clusterizer.executeClustering();

        CitationGraphDB neo4jDB = new CitationGraphDB("bolt://localhost:7687", "neo4j", "v3r0n1k4");
        neo4jDB.cleanDB();
        neo4jDB.storeParserResults();
    }

    //TODO: Example of adding many-to-many connections into the database. Delete before merge into muster.
//    public static void main(String[] args) {
//        AuthorService authorService = new AuthorService();
//        authorService.openConnection();
//        KeywordService keywordService = new KeywordService();
//        keywordService.openConnection();
//        ClusterService clusterService = new ClusterService();
//        clusterService.openConnection();
//
//
//        Author author = authorService.findAuthor(3);
//        Keyword key = keywordService.findKeyword(2);
//        Cluster cluster = clusterService.findCluster(2);
//
//        keywordService.closeConnection();
//        cluster.addKeyword(key);
//        clusterService.updateCluster(cluster);
//
//        clusterService.closeConnection();
//        author.addCluster(cluster);
//        authorService.updateAuthor(author);
//    }


    private static final String theme =          "theme";
    private static final String name =           "name";
    private static final String surname =        "surname";
    private static final String patronymic =     "patronymic";
    private static final String searchLimit =    "search_limit";
    private static final String cachedMode =     "cached_mode";


    public static Parameters parseParameters( String... args){

        Options options = new Options();

        options.addOption(theme,      true,  "point name for theme search");
        options.addOption(name,       true,  "author search, author's name");
        options.addOption(surname,    true,  "author search, author's surname");
        options.addOption(patronymic, true,  "author search, author's surname");
        options.addOption(searchLimit,true,  "limits number of connections to other users during research");
        options.addOption(cachedMode, false, "if added, doesn't parse ELIBRARY, but uploads data from authDB.out");


        Parameters parameters = new Parameters();
        boolean params = true;
        CommandLine cl;

        try {
            cl = new DefaultParser().parse(options, args);

            if (!cl.hasOption(cachedMode)){
                if (!cl.hasOption(theme) && !cl.hasOption(surname) && !cl.hasOption(name)){
                    logger.error("Start Point wasn't inserted");
                    params = false;
                }
                else if (cl.hasOption(theme)){
                    parameters.startPoint = new Theme(cl.getOptionValue(theme));
                }
                else if(cl.hasOption(surname) && cl.hasOption(name)){
//                    parameters.startPoint = new Author(cl.getOptionValue(surname), cl.getOptionValue(name));
                }

                if (cl.hasOption(searchLimit)){
                    parameters.searchLimit = Integer.parseInt(cl.getOptionValue(searchLimit));
                }
                if (cl == null){
                    HelpFormatter helpFormatter = new HelpFormatter();
                    helpFormatter.setWidth(132);
                    helpFormatter.printHelp("arguments", options);
                }
            }
            else {
                parameters.deserializeMode = true;
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
