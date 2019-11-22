import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import datamapper.ResearchStarters.Author;
import graph.CitationGraphDB;
import graph.Clusterer;
import graph.Clusterizer;
import io.LogStatistics;
import io.Serializer;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;
import util.Translator;

import java.util.List;

import static io.Serializer.deserializeGraph;

public class ScoreTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterTest.class);

    DefaultDirectedGraph<Author, DefaultEdge> graph;

    @Before
    public void testFileReader(){
        this.graph = deserializeGraph();
    }


    @Test
    public void testClustererInit(){
        Clusterer clusterizer = new Clusterer(Serializer.convertDbToGraph());
        clusterizer.splitConnectedComponents();

        clusterizer.executeClustering();

        CitationGraphDB neo4jDB = new CitationGraphDB("bolt://localhost:7687", "neo4j", "v3r0n1k4");
        neo4jDB.cleanDB();
        neo4jDB.storeParserResults();

        LogStatistics.logFinalParseStatistic(clusterizer.graph);


    }

    @Test
    public void icu4jTest(){
        String s = "Шиндарев Н. А.\n" +
                "Петрунева Р. \u0000.\n" +
                "Хлобыстова А. О.\n" +
                "Абрамов М. В.\n" +
                "Слезкин Н. Е.\n" +
                "Бродовская Е. В.\n" +
                "Михайлова М. Д.\n" +
                "Катаева В. И.\n" +
                "Шульженок В. И.\n" +
                "Юсупов Р. М.\n" +
                "Азаров А. А.\n" +
                "Дмитриева О. В.\n" +
                "Серпилин А. Л.\n" +
                "Багрецов Г. И.\n" +
                "Бушмелев Ф. В.\n" +
                "Суворова А. В.\n" +
                "Тулупьева Т. В.\n" +
                "Глазков А. А.\n" +
                "Тулупьев А. Л.\n" +
                "Фролова Е. В.\n" +
                "Петрунёва Р. М.\n" +
                "Нечаев В. Д.\n" +
                "Фильченков А. А.\n" +
                "Уржа О. А.\n" +
                "Золотин А. А.\n" +
                "Сулейманов А. А.\n" +
                "Дулина Н. В.\n" +
                "Бушмелёв Ф. В.\n" +
                "Вахромеева А. В.\n" +
                "Мусина В. Ф.\n";

        logger.info(Translator.translate(s));
    }

}
